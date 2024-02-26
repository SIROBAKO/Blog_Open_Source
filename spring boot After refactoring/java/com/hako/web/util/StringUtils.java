package com.hako.web.util;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {
	public boolean isValidPattern(String value, String pattern) {
		return value != null && Pattern.matches(pattern, value);
	}

	public String cleanXSS(String value) {
		// You'll need to remove the spaces from the html entities below
		value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		value = value.replaceAll("'", "&#39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		return value;
	}

}
