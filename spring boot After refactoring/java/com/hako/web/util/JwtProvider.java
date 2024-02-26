package com.hako.web.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {


	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	// 1시간 단위
	private static final long HOUR = 1000 * 60 * 60;
	private static final long ACC_TOKEN_VALIDITY = HOUR / 2;
	private static final long REF_TOKEN_VALIDITY = HOUR * 24 * 15;

	private static final String ISSUER = "HAKO_BLOG";
	private static final String AUDIENCE = "HAKO_BLOG_USER";

	// 모든 token에 대한 사용자 속성정보 조회
	private Jws<Claims> getAllClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
	}

	// token으로 사용자 속성정보 조회
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

		Jws<Claims> claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims.getBody());

	}

	// 토큰의 주제(subject) 조회
	public String getSubjectFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);

	}

	// 토큰의 발급자(issuer) 조회
	public String getIssuerFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuer);
	}

	// 토큰의 대상자(audience) 조회
	public String getAudienceFromToken(String token) {
		return getClaimFromToken(token, Claims::getAudience);
	}

	// jwt 고유 식별자 조회
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getId);
	}

	// 토큰 생성일자 조회
	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	// 토큰 활성 시간 조회
	public Date getNotBeforeDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getNotBefore);
	}

	// 토큰 만료일자 조회
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	// id를 입력받아 accessToken, refreshToken 생성
	public Map<String, String> generateTokenSet(String userId) {
		return generateTokenSet(userId, new HashMap<>());
	}

	// id, 속성정보를 이용해 accessToken, refreshToken 생성
	public Map<String, String> generateTokenSet(String userId, Map<String, Object> claims) {
		return doGenerateTokenSet(userId, claims);
	}

	// JWT accessToken, refreshToken 생성
	private Map<String, String> doGenerateTokenSet(String userId, Map<String, Object> claims) {
		Map<String, String> tokens = new HashMap<String, String>();

		Date currentTime = new Date();
		Date accTokenExpirationTime = new Date(System.currentTimeMillis() + ACC_TOKEN_VALIDITY);
		JwtBuilder builder = Jwts.builder();
		builder.setId(userId);
		builder.setSubject(userId);
		builder.setIssuer(ISSUER);
		builder.setAudience(AUDIENCE);
		builder.setIssuedAt(currentTime);
		builder.setExpiration(accTokenExpirationTime); // 1 hour later
		builder.signWith(SECRET_KEY);
		String accessToken = builder.compact();

		Date refTokenExpirationTime = new Date(System.currentTimeMillis() + REF_TOKEN_VALIDITY);
		builder.setExpiration(refTokenExpirationTime);
		builder.signWith(SECRET_KEY);
		String refreshToken = builder.compact();

		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		return tokens;
	}

	// 토큰검증
	public String validateToken(String token) {
		try {
			getAllClaimsFromToken(token);
			return "valid";
		} catch (MalformedJwtException e) {
			return "MalformedJwtException";
		} catch (ExpiredJwtException e) {
			return "Expired";
		} catch (UnsupportedJwtException e) {
			return "unsupported";
		} catch (IllegalArgumentException e) {
			return "Empty";
		} catch (Exception e) {
			return "Exception";
		}
	}

}