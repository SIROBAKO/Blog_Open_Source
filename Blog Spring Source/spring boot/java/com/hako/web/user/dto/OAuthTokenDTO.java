package com.hako.web.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthTokenDTO {
	private String access_token;
    private String refresh_token;
}
