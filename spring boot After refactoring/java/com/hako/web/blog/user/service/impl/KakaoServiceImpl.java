package com.hako.web.blog.user.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.hako.web.blog.user.service.KakaoService;

@Service
public class KakaoServiceImpl implements KakaoService {

	private Logger LOG = LogManager.getLogger(KakaoService.class);

	// application.properties에서 값을 가져오기 위해 @Value를 사용하여 필드 주입
	@Value("${kakao.client.id}")
	private String KAKAO_CLIENT_ID;

	@Value("${kakao.client.secret}")
	private String KAKAO_CLIENT_SECRET;

	@Value("${kakao.redirect.url}")
	private String KAKAO_REDIRECT_URL;

	@Value("${kakao.admin}")
	private String KAKAO_ADMIN;

	private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";
	private final static String KAKAO_API_URI = "https://kapi.kakao.com";

	private static final String FAILED_AUTHORIZATION_CODE = "Failed to get authorization code";
	private static final String API_CALL_FAILED = "API call failed";
	private static final String FAILED_GET_USER_INFO = "Failed to get user info";
	private static final String KAKAO_AUTHORIZATION_HEADER = "Authorization";
	private static final String KAKAO_BEARER_TOKEN_PREFIX = "Bearer ";
	private static final String KAKAO_CONTENT_TYPE_HEADER = "Content-type";
	private static final String KAKAO_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded;charset=utf-8";
	private static final String GRANT_TYPE_PARAM = "grant_type";
	private static final String CLIENT_ID_PARAM = "client_id";
	private static final String CLIENT_SECRET_PARAM = "client_secret";
	private static final String CODE_PARAM = "code";
	private static final String REDIRECT_URI_PARAM = "redirect_uri";
	private static final String GRANT_TYPE_VALUE = "authorization_code";
	private static final String KAKAO_USER_INFO_URI = "/v2/user/me";
	private static final String KAKAO_TOKEN_URI = "/oauth/token";
	private static final String ACCESS_TOKEN_JSON_KEY = "access_token";
	private static final String ID_JSON_KEY = "id";
	private static final String KAKAO_AK_HEADER = "Authorization";
	private static final String KAKAO_AK_PREFIX = "KakaoAK ";
	private static final String TARGET_ID_TYPE_PARAM = "target_id_type";
	private static final String TARGET_ID_PARAM = "target_id";
	private static final String KAKAO_UNLINK_URI = "/v1/user/unlink";

	public String getKakaoInfo(String code) throws Exception {
		if (code == null)
			throw new Exception(FAILED_AUTHORIZATION_CODE);

		String accessToken = "";

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add(KAKAO_CONTENT_TYPE_HEADER, KAKAO_CONTENT_TYPE_VALUE);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add(GRANT_TYPE_PARAM, GRANT_TYPE_VALUE);
			params.add(CLIENT_ID_PARAM, KAKAO_CLIENT_ID);
			params.add(CLIENT_SECRET_PARAM, KAKAO_CLIENT_SECRET);
			params.add(CODE_PARAM, code);
			params.add(REDIRECT_URI_PARAM, KAKAO_REDIRECT_URL);

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
			ResponseEntity<String> response = restTemplate.exchange(KAKAO_AUTH_URI + KAKAO_TOKEN_URI, HttpMethod.POST,
					httpEntity, String.class);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

			accessToken = (String) jsonObj.get(ACCESS_TOKEN_JSON_KEY);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(API_CALL_FAILED);
		}

		return getUserInfoWithToken(accessToken);
	}

	private String getUserInfoWithToken(String accessToken) throws Exception {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add(KAKAO_AUTHORIZATION_HEADER, KAKAO_BEARER_TOKEN_PREFIX + accessToken);
			headers.add(KAKAO_CONTENT_TYPE_HEADER, KAKAO_CONTENT_TYPE_VALUE);

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(KAKAO_API_URI + KAKAO_USER_INFO_URI,
					HttpMethod.POST, httpEntity, String.class);

			HttpStatus statusCode = response.getStatusCode();
			if (!statusCode.is2xxSuccessful()) {
				LOG.error("Failed to get user info - Status code: {}, Response: {}", statusCode, response.getBody());
				throw new Exception("Failed to get user info - Status code: " + statusCode);
			}

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

			long id = (long) jsonObj.get(ID_JSON_KEY);

			return String.valueOf(id);
		} catch (Exception e) {
			LOG.error(FAILED_GET_USER_INFO, e);
			throw new Exception(FAILED_GET_USER_INFO, e);
		}
	}

	@Override
	public void delKakaoUser(String user_id) throws Exception {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add(KAKAO_AK_HEADER, KAKAO_AK_PREFIX + KAKAO_ADMIN);
			headers.add(KAKAO_CONTENT_TYPE_HEADER, KAKAO_CONTENT_TYPE_VALUE);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add(TARGET_ID_TYPE_PARAM, "user_id");
			params.add(TARGET_ID_PARAM, user_id);

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
			ResponseEntity<String> response = restTemplate.exchange(KAKAO_API_URI + KAKAO_UNLINK_URI, HttpMethod.POST,
					httpEntity, String.class);

			HttpStatus statusCode = response.getStatusCode();
			if (!statusCode.is2xxSuccessful()) {
				LOG.error("Failed to unlink user - Status code: {}, Response: {}", statusCode, response.getBody());
				throw new Exception("Failed to unlink user - Status code: " + statusCode);
			}

		} catch (Exception e) {
			LOG.error(FAILED_GET_USER_INFO, e);
			throw new Exception(FAILED_GET_USER_INFO, e);
		}
	}

}
