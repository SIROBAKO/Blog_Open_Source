package com.hako.web.blog.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KakaoDTO {

	private long id;
	private String email;
	private String nickname;

}