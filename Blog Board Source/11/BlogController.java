private boolean shouldCrawl(String userAgent) {
		// User-Agent를 분석하여 크롤링 여부를 결정하는 로직 작성
		// 크롤링을 허용하는 경우 true, 제한하는 경우 false를 반환합니다.
		// 이 예시에서는 User-Agent가 "CrawlerBot"으로 시작하는 경우에만 크롤링으로 허용하도록 설정하였습니다.
		return userAgent != null && userAgent.startsWith("CrawlerBot");
}

boolean isCrawling = shouldCrawl(userAgent);

if (!isCrawling) {

	// 크롤링이 아닐때 실행

}