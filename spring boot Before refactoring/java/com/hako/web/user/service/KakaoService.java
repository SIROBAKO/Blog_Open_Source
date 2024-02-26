package com.hako.web.user.service;

public interface KakaoService {

	public String getKakaoInfo(String code) throws Exception;

	public void delKakaoUser(String user_id)  throws Exception;

}
