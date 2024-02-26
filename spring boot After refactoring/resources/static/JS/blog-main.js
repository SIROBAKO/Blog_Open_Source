// 초기 실행
const DARK_MODE_ATTRIBUTE = 'color-theme';
const LIGHT_MODE_VALUE = 'light';
const DARK_MODE_VALUE = 'dark';
const MAINTAIN_DAYS = 3;

const DARK_MODE_IMAGE_PATH = '/image/logo/HAK-logo-white.png';
const LIGHT_MODE_IMAGE_PATH = '/image/logo/HAK-logo.png';


const DARK_MODE_LABEL = 'Light';
const LIGHT_MODE_LABEL = 'Dark';
const ICON_SUN_CLASS = 'icon-sun';
const ICON_MOON_CLASS = 'icon-moon';

const LOGO_ID = 'logo';
const MODE_CONSOLE_LABEL_SELECTOR = 'label[for="Mode-Console"]';
const MODE_CONSOLE_ICON_ID = 'Mode-Console';
const MOBILE_LOGO_ID = 'mobile-logo';
const MOBILE_MODE_CONSOLE_LABEL_SELECTOR = 'label[for="mobile-Mode-Console"]';
const MOBILE_MODE_CONSOLE_ICON_ID = 'mobile-Mode-Console';


const DETAIL_PAGE_URL = 'detail';
const CODE_DARK_CLASS = '.code_dark';
const CODE_LIGHT_CLASS = '.code_light';
const DISPLAY_BLOCK = 'block';
const DISPLAY_NONE = 'none';

var pageNum = 1

// 페이지 로드시 실행되는 함수
document.addEventListener('DOMContentLoaded', () => {
	setFixedElementHeight();
	initializeDarkMode();
	window.addEventListener('resize', setFixedElementHeight)
	window.addEventListener('scroll', moveFooter)
});

// Dark Mode를 초기화하는 함수
function initializeDarkMode() {
	if (!getCookie(DARK_MODE_ATTRIBUTE)) {
		setCookie(DARK_MODE_ATTRIBUTE, LIGHT_MODE_VALUE, MAINTAIN_DAYS);
	} else {
		maintainDarkMode();
	}

	if (isLightMode() || !navigator.cookieEnabled) {
		changeLight();
	} else {
		changeDark();
	}
}

// Dark Mode를 유지하는 함수
function maintainDarkMode() {
	const currentMode = getCookie(DARK_MODE_ATTRIBUTE);
	setCookie(DARK_MODE_ATTRIBUTE, currentMode, MAINTAIN_DAYS);
}

// Light Mode 여부를 확인하는 함수
function isLightMode() {
	return getCookie(DARK_MODE_ATTRIBUTE) === LIGHT_MODE_VALUE;
}


function DarkModeChange() {
	var modeData = document.documentElement.getAttribute(DARK_MODE_ATTRIBUTE)
	if (modeData == DARK_MODE_VALUE) {
		setCookie(DARK_MODE_ATTRIBUTE, LIGHT_MODE_VALUE, MAINTAIN_DAYS)
		changeLight()
	} else {
		setCookie(DARK_MODE_ATTRIBUTE, DARK_MODE_VALUE, MAINTAIN_DAYS)
		changeDark()
	}
}



function toggleDarkMode(isDarkMode) {
	const mode = isDarkMode ? DARK_MODE_VALUE : LIGHT_MODE_VALUE;

	document.documentElement.setAttribute(DARK_MODE_ATTRIBUTE, mode);

	const logoSrc = isDarkMode ? DARK_MODE_IMAGE_PATH : LIGHT_MODE_IMAGE_PATH;
	const labelInnerHtml = isDarkMode ? LIGHT_MODE_LABEL : DARK_MODE_LABEL;
	const iconClass = isDarkMode ? ICON_SUN_CLASS : ICON_MOON_CLASS;

	// DOM 요소 참조를 가져오는 코드
	const logo = document.getElementById(LOGO_ID);
	const modeConsoleLabel = document.querySelector(MODE_CONSOLE_LABEL_SELECTOR);
	const modeConsoleIcon = document.getElementById(MODE_CONSOLE_ICON_ID);
	const mobileLogo = document.getElementById(MOBILE_LOGO_ID);
	const mobileModeConsoleLabel = document.querySelector(MOBILE_MODE_CONSOLE_LABEL_SELECTOR);
	const mobileModeConsoleIcon = document.getElementById(MOBILE_MODE_CONSOLE_ICON_ID);

	// DOM 요소 수정 코드
	logo.src = logoSrc;
	modeConsoleLabel.innerHTML = labelInnerHtml;
	modeConsoleIcon.className = iconClass;
	mobileLogo.src = logoSrc;
	mobileModeConsoleLabel.innerHTML = labelInnerHtml;
	mobileModeConsoleIcon.className = iconClass;


	if (window.location.href.includes(DETAIL_PAGE_URL)) {
		const codeDarkElements = document.querySelectorAll(CODE_DARK_CLASS);
		const codeLightElements = document.querySelectorAll(CODE_LIGHT_CLASS);

		codeDarkElements.forEach(dark => {
			dark.style.display = isDarkMode ? DISPLAY_BLOCK : DISPLAY_NONE;
		});

		codeLightElements.forEach(light => {
			light.style.display = isDarkMode ? DISPLAY_NONE : DISPLAY_BLOCK;
		});
	}

}


function changeDark() {
	toggleDarkMode(true);
}

function changeLight() {
	toggleDarkMode(false);
}

function scrollIndicator() {
	const windowHeight = window.innerHeight
	const documentHeight = document.documentElement.scrollHeight
	const scrollableDistance = documentHeight - windowHeight
	const scrolled = (window.pageYOffset / scrollableDistance) * 100
	const scrollIndicator = document.getElementById('scrollIndicator')
	scrollIndicator.style.width = scrolled + '%'
}




function AddList() {
	pageNum += 1

	const form = {
		page: pageNum,
	}

	const option = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(form),
	}

	fetch('/AddList', option)
		.then(response => {
			if (response.status == 200) {
				return response.json()
			} else if (response.status === 204) {

				alert("게시물이 더이상 없습니다.");

				throw new Error('NoContentError');
			} else {

				alert("서버 조회 오류 발생");

				throw new Error('ServerError');
			}
		})
		.then(data => {
			data.forEach(board => {
				input_console = '<li>'
				input_console +=
					'<a href="/detail/' + board.num + '">'
				input_console +=
					'<img src="/upload/image/fileupload/' +
					board.num +
					'/thumbnail/thumbnail.PNG" onerror="this.src=\'/image/logo/black.png\'">'
				input_console += '<h3>' + board.title + '</h3></a>'
				input_console += '<p>' + board.category + '</p></li>'
				document
					.getElementById('index-list')
					.insertAdjacentHTML('beforeend', input_console)

			})
		}).catch(error => {
			console.error('요청 처리 중 오류 발생:', error);
		});
}



function openNav() {
	document.getElementById('sub_bg').style.display = 'block'
	document.getElementById('mobile-nav').style.display = 'block'
	document.getElementById('mobile-nav').animate(
		{
			transform: ['translateX(0px)', 'translateX(300px)'],
		},
		{
			duration: 400, // 밀리초 지정
			fill: 'forwards', // 종료 시 속성을 지님
			easing: 'ease', // 가속도 종류
		}
	)
}
function closeNav() {
	document.getElementById('sub_bg').style.display = 'none   '
	document.getElementById('mobile-nav').style.display = 'none'

	document.getElementById('mobile-nav').animate(
		{
			transform: ['translateX(0px)', 'translateX(-300px)'],
		},
		{
			duration: 400, // 밀리초 지정
			fill: 'forwards', // 종료 시 속성을 지님
			easing: 'ease', // 가속도 종류
		}
	)
}

function goTop() {
	window.scroll({
		top: 0,
		left: 0,
		behavior: 'smooth',
	})
}

function showInfo() {
	check = document.getElementById('blog-info').style.display

	if (check == 'block') {
		closeInfo()
	} else {
		window.removeEventListener('scroll', closeInfo)
		document.getElementById('blog-info').style.display = 'block'
	}
	window.scroll({
		top: document.body.scrollHeight,
		left: 0,
		behavior: 'smooth',
	})

	setTimeout(function() {
		window.addEventListener('scroll', closeInfo)
	}, 1000)
}

function closeInfo() {
	document.getElementById('blog-info').style.display = 'none'

	setFixedElementHeight()
}



function moveFooter() {
	var footer = document.querySelector('footer')
	var footerHeight = footer.offsetHeight
	var scrollPosition =
		window.pageYOffset ||
		document.documentElement.scrollTop ||
		document.body.scrollTop
	var windowHeight = window.innerHeight
	var documentHeight = Math.max(
		document.body.scrollHeight,
		document.documentElement.scrollHeight,
		document.body.offsetHeight,
		document.documentElement.offsetHeight,
		document.body.clientHeight,
		document.documentElement.clientHeight
	)

	var fixedElements = document.querySelectorAll('.fixed')
	var header = document.querySelector('header')
	var headerHeight = header.offsetHeight

	if (scrollPosition + windowHeight >= documentHeight - footerHeight) {
		var totalHeight =
			windowHeight - headerHeight - windowHeight * 0.1 - footerHeight

		footer.style.transition = 'transform 0.3s ease-in-out'
		footer.style.transform = 'translateY(0)'
	} else {
		var totalHeight = windowHeight - headerHeight - windowHeight * 0.1

		footer.style.transition = 'transform 0.3s ease-in-out'
		footer.style.transform = 'translateY(' + footerHeight + 'px)'
	}

	fixedElements.forEach(function(element) {
		element.style.height = totalHeight + 'px'
	})
}

window.addEventListener('scroll', moveFooter)

function setFixedElementHeight() {
	var fixedElements = document.querySelectorAll('.fixed')
	var header = document.querySelector('header')
	var windowHeight = window.innerHeight
	var headerHeight = header.offsetHeight

	var totalHeight = windowHeight - headerHeight - windowHeight * 0.1

	fixedElements.forEach(function(element) {
		element.style.height = totalHeight + 'px'
	})
}


// 게시글 공유
function shareLink() {
	var url = window.location.href
	var shareUrl

	if (navigator.share) {
		// 웹 공유 API를 지원하는 경우
		shareUrl = url
		navigator
			.share({
				url: shareUrl,
			})
			.then(() => console.log('링크 공유 성공'))
			.catch((error) => console.error('링크 공유 실패', error))
	} else {
		// 지원하지 않는 경우
		shareUrl = url
		var tempInput = document.createElement('input')
		tempInput.setAttribute('value', shareUrl)
		document.body.appendChild(tempInput)
		tempInput.select()
		document.execCommand('copy')
		document.body.removeChild(tempInput)
		alert('링크가 복사되었습니다.')
	}
}


function setCookie(cookie_name, value, days) {
	var exdate = new Date()
	exdate.setDate(exdate.getDate() + days)
	// 설정 일수만큼 현재시간에 만료값으로 지정

	var cookie_value =
		escape(value) +
		(days == null ? '' : '; expires=' + exdate.toUTCString())
	document.cookie = cookie_name + '=' + cookie_value + '; path=/'
}

function getCookie(cookie_name) {
	var x, y
	var val = document.cookie.split(';')

	for (var i = 0; i < val.length; i++) {
		x = val[i].substr(0, val[i].indexOf('='))
		y = val[i].substr(val[i].indexOf('=') + 1)
		x = x.replace(/^\s+|\s+$/g, '') // 앞과 뒤의 공백 제거하기
		if (x == cookie_name) {
			return unescape(y) // unescape로 디코딩 후 값 리턴
		}
	}
}