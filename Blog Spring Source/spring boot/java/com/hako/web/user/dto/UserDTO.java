package com.hako.web.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDTO extends ResponseDTO {

	private String user_name;
	private String user_email;

}
