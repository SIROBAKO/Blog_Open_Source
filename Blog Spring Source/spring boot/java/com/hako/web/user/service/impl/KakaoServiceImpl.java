package com.hako.web.user.service.impl;

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

import com.hako.web.user.service.KakaoService;

@Service
public class KakaoServiceImpl implements KakaoService {

	private Logger LOG = LogManager.getLogger(KakaoService.class);

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

	// 카카오로부터 받은 인가 코드를 사용하여 Access Token 및 사용자 정보를 가져오는 메서드
	public String getKakaoInfo(String code) throws Exception {
		if (code == null)
			throw new Exception("Failed to get authorization code");

		String accessToken = "";
		String refreshToken = "";

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("grant_type", "authorization_code");
			params.add("client_id", KAKAO_CLIENT_ID);
			params.add("client_secret", KAKAO_CLIENT_SECRET);
			params.add("code", code);
			params.add("redirect_uri", KAKAO_REDIRECT_URL);

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

			// 카카오로부터 Access Token 및 Refresh Token 요청
			ResponseEntity<String> response = restTemplate.exchange(KAKAO_AUTH_URI + "/oauth/token", HttpMethod.POST,
					httpEntity, String.class);

			// JSON 형식의 응답 데이터 파싱
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

			accessToken = (String) jsonObj.get("access_token");
			refreshToken = (String) jsonObj.get("refresh_token");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("API call failed");
		}

		// Access Token을 사용하여 사용자 정보 요청
		return getUserInfoWithToken(accessToken);
	}

	// Access Token을 사용하여 카카오 사용자 정보를 가져오는 메서드
	private String getUserInfoWithToken(String accessToken) throws Exception {
		try {
			// HTTP 헤더 생성
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + accessToken);
			headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

			// HTTP 헤더 설정 및 요청
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(KAKAO_API_URI + "/v2/user/me", HttpMethod.POST,
					httpEntity, String.class);

			// HTTP 상태 코드 확인
			HttpStatus statusCode = response.getStatusCode();
			if (!statusCode.is2xxSuccessful()) {
				// 에러 상태 코드와 응답 내용을 로그에 출력
				LOG.error("Failed to get user info - Status code: {}, Response: {}", statusCode, response.getBody());
				throw new Exception("Failed to get user info - Status code: " + statusCode);
			}

			// 응답 데이터 파싱
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

			long id = (long) jsonObj.get("id");

			// KakaoDTO 객체 생성하여 반환
			return id + "";
		} catch (Exception e) {
			// 사용자 정보 요청 중에 예외가 발생한 경우
			LOG.error("Failed to get user info", e);
			throw new Exception("Failed to get user info", e);
		}
	}

	@Override
	public void delKakaoUser(String user_id) throws Exception {
		try {
			// HTTP 헤더 생성
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "KakaoAK " + KAKAO_ADMIN);
			headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("target_id_type", "user_id");
			params.add("target_id", user_id);
			

			// HTTP 헤더 설정 및 요청
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

			ResponseEntity<String> response = restTemplate.exchange(KAKAO_API_URI + "/v1/user/unlink", HttpMethod.POST,
					httpEntity, String.class);

			// HTTP 상태 코드 확인
			HttpStatus statusCode = response.getStatusCode();
			if (!statusCode.is2xxSuccessful()) {
				// 에러 상태 코드와 응답 내용을 로그에 출력
				LOG.error("Failed to get user info - Status code: {}, Response: {}", statusCode, response.getBody());
				throw new Exception("Failed to get user info - Status code: " + statusCode);
			}

		
		} catch (Exception e) {
			// 사용자 정보 요청 중에 예외가 발생한 경우
			LOG.error("Failed to get user info", e);
			throw new Exception("Failed to get user info", e);
		}
	}
}
