package com.hako.web.config.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtProvider {

	@Value("${jwt.secretKey}")
	private String secretKey;

	// 1시간 단위
	public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60;

	// token으로 사용자 id 조회
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getId);
	}

	// token으로 사용자 속성정보 조회
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// 모든 token에 대한 사용자 속성정보 조회
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}

	// 토큰 만료일자 조회
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	// id를 입력받아 accessToken, refreshToken 생성
	public Map<String, String> generateTokenSet(String id) {
		return generateTokenSet(id, new HashMap<>());
	}

	// id, 속성정보를 이용해 accessToken, refreshToken 생성
	public Map<String, String> generateTokenSet(String id, Map<String, Object> claims) {
		return doGenerateTokenSet(id, claims);
	}

	// JWT accessToken, refreshToken 생성
	private Map<String, String> doGenerateTokenSet(String id, Map<String, Object> claims) {
		Map<String, String> tokens = new HashMap<String, String>();

		String accessToken = Jwts.builder().setClaims(claims).setId(id)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 2))// 2시간
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();

		String refreshToken = Jwts.builder().setId(id)
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 24 * 15)) // 15일
				.setIssuedAt(new Date(System.currentTimeMillis())).signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();

		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		return tokens;
	}

	// 토근 검증
	public String validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return "valid";
		} catch (MalformedJwtException e) {
			return "invalid";
		} catch (ExpiredJwtException e) {
			return "Expired";
		} catch (UnsupportedJwtException e) {
			return "unsupported";
		} catch (IllegalArgumentException e) {
			return "Empty";
		} catch (Exception e) {
			if (e instanceof MalformedJwtException) {
				return "MalformedJwtException";
			}

			return "Exception";
		}
	}

}