const PASSWORD_MISMATCH_MESSAGE = "비밀번호가 일치하지 않습니다.";
const RESET_SUCCESS_MESSAGE = "비밀번호가 재설정 되었습니다.";
const NO_ACCOUNT_MESSAGE = "해당하는 계정이 없습니다.";


const FIND_ID_ENDPOINT = '/user/find';
const NO_ID_MESSAGE = '해당하는 아이디가 없습니다.';


const LOGIN_SUCCESS_MESSAGE = '환영합니다.';
const LOGIN_FAILURE_MESSAGE = '아이디 또는 비밀번호가 틀립니다.';
const NETWORK_ERROR_MESSAGE = 'Network response was not ok';
const LOGOUT_SUCCESS_MESSAGE = '로그아웃에 성공했습니다.';
const REGISTRATION_SUCCESS_MESSAGE = '회원가입에 성공했습니다.';
const ID_INVALID_MESSAGE = 'ID값을 확인해주세요.';
const NAME_INVALID_MESSAGE = '닉네임값을 확인해주세요.';
const EMAIL_INVALID_MESSAGE = '이메일값을 확인해주세요.';
const PASSWORD_INVALID_MESSAGE = '비밀번호값을 확인해주세요.';
const UPDATE_SUCCESS_MESSAGE = '회원 정보수정 성공했습니다.';
const DELETION_CONFIRM_MESSAGE = '회원탈퇴 하시겠습니까?';
const DELETION_SUCCESS_MESSAGE = '회원탈퇴에 성공했습니다.';
const UNAUTHORIZED_REQUEST_MESSAGE = '잘못된 요청입니다.';
const SERVER_ERROR_MESSAGE = '서버 오류 발생\n 관리자에게 문의해주세요.';


const EMPTY_VALUE_MESSAGE = '';
const ID_VALID_MESSAGE = '사용가능한 아이디 입니다.';
const ID_INVALID_LENGTH_MESSAGE = '8~15자 이내 영문,숫자만';
const ID_DUPLICATE_MESSAGE = '중복된 아이디 입니다.';
const PASSWORD_VALID_MESSAGE = '사용가능한 비밀번호 입니다.';
const PASSWORD_INVALID_LENGTH_MESSAGE = '8~15자 이내 영문,숫자,기호만';
const PASSWORD_UNMATCHED_MESSAGE = '비밀번호가 일치하지 않습니다.';
const NAME_VALID_MESSAGE = '사용가능한 닉네임 입니다.';
const NAME_INVALID_LENGTH_MESSAGE = '2~10자 이내 영문,숫자,한글만';
const NAME_DUPLICATE_MESSAGE = '중복된 닉네임 입니다.';
const EMAIL_VALID_MESSAGE = '사용가능한 이메일 입니다.';
const EMAIL_INVALID_FORMAT_MESSAGE = '이메일 형식을 확인해주세요.';
const EMAIL_DUPLICATE_MESSAGE = '중복된 이메일 입니다.';
const PASSWORD_PATTERN = /^[a-zA-Z0-9!@#$%^&*()_+\-={}[\]:"'<>,.?/\\|]{8,15}$/;
const EMPTY_VALUE = '';

window.onload = function() {
	var currentURL = window.location.href;

	// 회원 가입 페이지 피드백 이벤트
	if (currentURL.includes("join")) {
		if (currentURL.includes("kakao")) {
			handleKakaoJoin();
		} else {
			handleRegularJoin();
		}
	} else if (currentURL.includes("find")) {
		handleFindPage();
	} else if (currentURL.includes("mypage")) {
		handleMypage();
	} else {
		user_info();
	}

	if (currentURL.includes("detail") || currentURL.includes("index") || currentURL.includes("list")) {
		sessionStorage.setItem('referrer', window.location.pathname);
	}
}

function handleKakaoJoin() {
	const id_input = document.getElementById("user_id");
	const name_input = document.getElementById("user_name_form");
	const email_input = document.getElementById("user_email_form");

	id_input.style.display = 'none';
	const idLabel = document.querySelector('label[for="user_id"]');
	idLabel.style.display = 'none';

	name_input.addEventListener("input", checkNameValid);
	email_input.addEventListener("input", checkEmailValid);

	const kakaoUserId = window.location.href.split("kakao")[1];
	id_input.value = kakaoUserId;
	id_input.setAttribute("readonly", true);
	id_input.value = kakaoUserId;
	id_input.setAttribute("readonly", true);

	const defaultPwd = 'kakaoUser';
	document.getElementById("user_pwd").value = defaultPwd;
	document.getElementById("user_pwd_re_enter").value = defaultPwd;
	document.getElementById("user_pwd").setAttribute("readonly", true);
	document.getElementById("user_pwd").style.display = 'none';
	document.getElementById("user_pwd_re_enter").style.display = 'none';
	document.querySelector("[for=user_pwd]").style.display = 'none';
	document.querySelector("[for=user_pwd_re_enter]").style.display = 'none';
	
	
}

function handleRegularJoin() {
	const id_input = document.getElementById("user_id");
	const pwd_input = document.getElementById("user_pwd");
	const pwd_re_enter_input = document.getElementById("user_pwd_re_enter");
	const name_input = document.getElementById("user_name_form");
	const email_input = document.getElementById("user_email_form");

	id_input.addEventListener("input", checkIdValid);
	pwd_input.addEventListener("input", checkPwdValid);
	pwd_re_enter_input.addEventListener("input", checkPwdValid);
	name_input.addEventListener("input", checkNameValid);
	email_input.addEventListener("input", checkEmailValid);
}

function handleFindPage() {
	findHandler();
	const pwd_input = document.getElementById("user_pwd");
	const pwd_re_enter_input = document.getElementById("user_pwd_re_enter");
	pwd_input.addEventListener("input", checkPwdValid);
	pwd_re_enter_input.addEventListener("input", checkPwdValid);
}

function handleMypage() {
	const name_input = document.getElementById("user_name_form");
	const email_input = document.getElementById("user_email_form");
	name_input.addEventListener("input", checkNameValid);
	email_input.addEventListener("input", checkEmailValid);
	user_info();
}

function checkIdValid() {
	const id_value = document.getElementById("user_id").value
	const idMessage = document.getElementById("id_message")

	if (id_value == EMPTY_VALUE) {
		idMessage.textContent = EMPTY_VALUE;
	} else {
		const form = {
			id: id_value
		}
		const options = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(form),
		}

		fetch('/check-id', options)
			.then(response => {

				switch (response.status) {
					case 200:
						idMessage.textContent = ID_VALID_MESSAGE;
						idMessage.style.color = 'var(--success-message) ';
						break;
					case 400:
						idMessage.textContent = ID_INVALID_LENGTH_MESSAGE;
						idMessage.style.color = 'var(--error-message)';
						break;
					case 409:
						idMessage.textContent = ID_DUPLICATE_MESSAGE;
						idMessage.style.color = 'var(--error-message)';
						break;
				}
			})
	}
}

function checkPwdValid() {
	const pwd_value = document.getElementById("user_pwd").value
	const pwd_re_enter_value = document.getElementById("user_pwd_re_enter").value
	const pwdMessage = document.getElementById("pwd_message")

	if (!PASSWORD_PATTERN.test(pwd_value)) {
		pwdMessage.textContent = PASSWORD_INVALID_LENGTH_MESSAGE;
		pwdMessage.style.color = 'var(--error-message) ';
	} else if ((pwd_value != pwd_re_enter_value) && pwd_re_enter_value != "") {
		pwdMessage.textContent = PASSWORD_MISMATCH_MESSAGE;
		pwdMessage.style.color = 'var(--error-message) ';
	} else {
		pwdMessage.textContent = PASSWORD_VALID_MESSAGE;
		pwdMessage.style.color = 'var(--success-message) ';
	}
}

function checkNameValid() {
	const name_value = document.getElementById("user_name_form").value
	const nameMessage = document.getElementById("name_message")

	if (name_value == EMPTY_VALUE) {
		nameMessage.textContent = EMPTY_VALUE;
	} else {
		const form = {
			name: name_value,
		}

		const options = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(form),
		}

		fetch('/check-name', options)
			.then(response => {

				switch (response.status) {
					case 200:
						nameMessage.textContent = NAME_VALID_MESSAGE;
						nameMessage.style.color = 'var(--success-message) ';
						break;
					case 400:
						nameMessage.textContent = NAME_INVALID_LENGTH_MESSAGE;
						nameMessage.style.color = 'var(--error-message)';
						break;
					case 409:
						nameMessage.textContent = NAME_DUPLICATE_MESSAGE;
						nameMessage.style.color = 'var(--error-message)';
						break;
				}
			})
	}
}

function checkEmailValid() {
	const email_value = document.getElementById("user_email_form").value
	const emailMessage = document.getElementById("email_message")

	if (email_value == '') {
		emailMessage.textContent = '';
	} else {
		const form = {
			email: email_value
		}

		const options = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(form),
		}

		fetch('/check-email', options)
			.then(response => {

				switch (response.status) {
					case 200:
						emailMessage.textContent = EMAIL_VALID_MESSAGE;
						emailMessage.style.color = 'var(--success-message) ';
						break;
					case 400:
						emailMessage.textContent = EMAIL_INVALID_FORMAT_MESSAGE;
						emailMessage.style.color = 'var(--error-message)';
						break;
					case 409:
						emailMessage.textContent = EMAIL_DUPLICATE_MESSAGE;
						emailMessage.style.color = 'var(--error-message)';
						break;
				}
			})
	}
}


// 로그인
function user_login() {
	var user_id = document.getElementById("user_id").value
	var user_pwd = document.getElementById("user_pwd").value

	const form = {
		id: user_id,
		pwd: user_pwd
	}

	const options = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(form),
	}

	fetch('/token', options)
		.then(response => {
			switch (response.status) {
				case 200:
					alert(LOGIN_SUCCESS_MESSAGE);
					location.href = sessionStorage.getItem('referrer');
					break;
				case 401:
					alert(LOGIN_FAILURE_MESSAGE);
					break;
				default:
					throw new Error(NETWORK_ERROR_MESSAGE);
			}
		})
		.catch(error => {
			console.error('There was a problem with the fetch operation:', error);
		});
}

// 유저 정보 반환
function user_info() {
	const user_name = document.getElementById("user_name")
	const user_login = document.getElementById("user_login")
	const user_logout = document.getElementById("user_logout")

	const user_name_header = document.getElementById("user_name_header")
	const user_login_header = document.getElementById("user_login_header")
	const user_logout_header = document.getElementById("user_logout_header")

	const options = {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json',
		}
	}

	fetch('/user', options)
		.then(response => {
			if (!response.ok) {
				sessionStorage.setItem('loginState', false);
				throw new Error(NETWORK_ERROR_MESSAGE);

			}
			return response.json();
		})
		.then(data => {
			user_name.style.display = "block"
			user_name_header.style.display = "block"
			user_name.innerHTML = '<a href="/blog-mypage">' + data.user_name + ' 님</a>'
			user_name_header.innerHTML = '<a href="/blog-mypage">' + data.user_name + ' 님</a>'

			user_login.style.display = "none"
			user_login_header.style.display = "none"
			user_logout.style.display = "block"
			user_logout_header.style.display = "block"

			sessionStorage.setItem('loginState', true);

			if (location.href.includes("detail")) {

				const comment_name = document.getElementById("user-name")
				const comment_password = document.getElementById("pwd")

				comment_name.value = data.user_name
				comment_password.value = "HAKO_DEV_USER"
				comment_name.setAttribute("readonly", true)
				comment_password.setAttribute("readonly", true)
			}
			if (location.href.includes("mypage")) {
				document.getElementById("user_name_form").value = data.user_name
				document.getElementById("user_email_form").value = data.user_email
			}
		})
		.catch(error => {
			user_login.style.display = "block"
			console.error('There was a problem with the fetch operation:', error);
		});
}

// 로그아웃
function user_logout() {
	const options = {
		method: 'DELETE',
		headers: {
			'Content-Type': 'application/json',
		}
	}

	fetch('/token', options)
		.then(response => {
			if (!response.ok) {
				throw new Error(NETWORK_ERROR_MESSAGE);
			} else {

				const user_name = document.getElementById("user_name")
				const user_login = document.getElementById("user_login")
				const user_logout = document.getElementById("user_logout")

				user_name.style.display = "none"
				user_login.style.display = "block"
				user_logout.style.display = "none"

				alert(LOGOUT_SUCCESS_MESSAGE);
				goToReferrerPage()
			}
		})
		.catch(error => {
			console.error('There was a problem with the fetch operation:', error);
		});
}

// 회원가입
function user_join() {
	const user_pwd = document.getElementById("user_pwd")
	const user_name = document.getElementById("user_name_form")
	const user_email = document.getElementById("user_email_form")

	const pwd_value = document.getElementById("user_pwd").value
	const pwd_re_enter_value = document.getElementById("user_pwd_re_enter").value

	if (pwd_value != pwd_re_enter_value) {
		alert(PASSWORD_UNMATCHED_MESSAGE)
	} else {
		const form = {
			id: user_id.value,
			pwd: user_pwd.value,
			name: user_name.value,
			email: user_email.value
		}

		const options = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(form),
		}

		fetch('/user', options)
			.then(response => {
				switch (response.status) {
					case 200:
						alert(REGISTRATION_SUCCESS_MESSAGE);
						window.location.href = 'blog-login'
						break;
					case 400:
						switch (response.headers.get('invalidValue')) {
							case 'ID':
								alert(ID_INVALID_MESSAGE);
								document.getElementById("user_id").focus();
								break;
							case 'NAME':
								alert(NAME_INVALID_MESSAGE);
								document.getElementById("user_name_form").focus();
								break;
							case 'EMAIL':
								alert(EMAIL_INVALID_MESSAGE);
								document.getElementById("user_email_form").focus();
								break;
							case 'PWD':
								alert(PASSWORD_INVALID_MESSAGE);
								document.getElementById("user_pwd").focus();
								break;
						}
						break;
				}
			})
			.catch(error => {
				console.error('There was a problem with the fetch operation:', error);
			});
	}
}

// 회원정보 수정
function user_update() {
	const user_name = document.getElementById("user_name_form")
	const user_email = document.getElementById("user_email_form")

	const form = {
		name: user_name.value,
		email: user_email.value
	}

	const options = {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(form),
	}

	fetch('/user', options)
		.then(response => {
			switch (response.status) {
				case 200:
					alert(UPDATE_SUCCESS_MESSAGE);
					user_info()
					break;
				case 400:
					switch (response.headers.get('invalidValue')) {
						case 'NAME':
							alert(NAME_INVALID_MESSAGE);
							user_name.focus();
							break;
						case 'EMAIL':
							alert(EMAIL_INVALID_MESSAGE);
							user_email.focus();
							break;
					}
					break;
				default:
					alert(SERVER_ERROR_MESSAGE);
					break;
			}
		})
		.catch(error => {
			console.error('There was a problem with the fetch operation:', error);
		});
}

// 회원탈퇴
function user_delete() {
	if (confirm(DELETION_CONFIRM_MESSAGE)) {
		const options = {
			method: 'DELETE',
			headers: {
				'Content-Type': 'application/json',
			},
		}

		fetch('/user', options)
			.then(response => {
				switch (response.status) {
					case 200:
						alert(DELETION_SUCCESS_MESSAGE);
						goToReferrerPage()
						break;
					case 401:
						alert(UNAUTHORIZED_REQUEST_MESSAGE);
						location.reload();
						break;
					default:
						alert(SERVER_ERROR_MESSAGE);
						break;
				}
			})
			.catch(error => {
				console.error('There was a problem with the fetch operation:', error);
			});
	}
}


function toggleElementDisplay(element, displayValue) {
	element.style.display = displayValue;
}

function findHandler() {
	var selectedValue = document.getElementById("find-handler").value;
	const id_input = document.getElementById("user_id");
	const idLabel = document.querySelector('label[for="user_id"]');
	const pwd_input = document.getElementById("user_pwd");
	const pwdLabel = document.querySelector('label[for="user_pwd"]');
	const pwd_re_enter_input = document.getElementById("user_pwd_re_enter");
	const pwdReLabel = document.querySelector('label[for="user_pwd_re_enter"]');
	const find_button = document.getElementById("find_button");
	const reset_button = document.getElementById("reset_button");
	const result_area = document.getElementById("result_area");

	if (selectedValue == 'id') {
		toggleElementDisplay(id_input, DISPLAY_NONE);
		toggleElementDisplay(pwd_input, DISPLAY_NONE);
		toggleElementDisplay(pwd_re_enter_input, DISPLAY_NONE);
		toggleElementDisplay(idLabel, DISPLAY_NONE);
		toggleElementDisplay(pwdLabel, DISPLAY_NONE);
		toggleElementDisplay(pwdReLabel, DISPLAY_NONE);
		toggleElementDisplay(reset_button, DISPLAY_NONE);
		toggleElementDisplay(find_button, DISPLAY_BLOCK);
		toggleElementDisplay(result_area, DISPLAY_BLOCK);
	} else {
		toggleElementDisplay(id_input, DISPLAY_BLOCK);
		toggleElementDisplay(pwd_input, DISPLAY_BLOCK);
		toggleElementDisplay(pwd_re_enter_input, DISPLAY_BLOCK);
		toggleElementDisplay(idLabel, DISPLAY_BLOCK);
		toggleElementDisplay(pwdLabel, DISPLAY_BLOCK);
		toggleElementDisplay(pwdReLabel, DISPLAY_BLOCK);
		toggleElementDisplay(find_button, DISPLAY_NONE);
		toggleElementDisplay(reset_button, DISPLAY_BLOCK);
		toggleElementDisplay(result_area, DISPLAY_NONE);
	}
}


function findId() {
	const userEmail = document.getElementById("user_email_form").value;

	const form = {
		email: userEmail
	};

	const options = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(form),
	};

	fetch(FIND_ID_ENDPOINT, options)
		.then(response => {
			switch (response.status) {
				case 200:
					return response.text();
				case 204:
					alert(NO_ID_MESSAGE);
					break;
				default:
					alert(SERVER_ERROR_MESSAGE);
					break;
			}
		})
		.then(data => {
			result_area.innerHTML = "아이디 : " + data;
		})
		.catch(error => {
			console.error('오류 발생: ', error);
		});
}


function resetPassword() {
	const userIdElement = document.getElementById("user_id");
	const userEmailElement = document.getElementById("user_email_form");
	const userPwdElement = document.getElementById("user_pwd");
	const userPwdReEnterElement = document.getElementById("user_pwd_re_enter");

	const userId = userIdElement.value;
	const userEmail = userEmailElement.value;
	const userPwd = userPwdElement.value;
	const userPwdReEnter = userPwdReEnterElement.value;


	const isValidPassword = userPwd === userPwdReEnter;

	if (isValidPassword) {
		const formData = {
			id: userId,
			email: userEmail,
			pwd: userPwd
		};

		const options = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(formData),
		};

		fetch('/user/reset', options)
			.then(response => {
				switch (response.status) {
					case 200:
						alert(RESET_SUCCESS_MESSAGE);
						window.location.href = "blog-login";
						break;
					case 204:
						alert(NO_ACCOUNT_MESSAGE);
						break;
					default:
						alert(SERVER_ERROR_MESSAGE);
						break;
				}
			});
	} else {
		alert(PASSWORD_UNMATCHED_MESSAGE);
	}
}


function kakaoLogin() {

	// 현재 페이지의 URL을 가져옵니다.
	var currentURL = window.location.href;

	// URL에서 프로토콜, 호스트, 포트 등을 가져오기 위해 URL 객체를 생성합니다.
	var urlObject = new URL(currentURL);

	// 프로토콜을 가져옵니다.
	var protocol = urlObject.protocol;

	// 도메인을 가져옵니다.
	var domain = urlObject.hostname;

	// 포트를 가져옵니다. 포트가 없을 경우에는 빈 문자열로 처리합니다.
	var port = urlObject.port || '';

	// 프로토콜, 도메인, 포트를 합쳐서 URL을 구성합니다.
	var fullURL = protocol + '//' + domain + (port ? ':' + port : '');

	window.location.href = "https://kauth.kakao.com/oauth/authorize?response_type=code&" +
		"client_id=54df3b87400b7947ea7a60537e18d767&redirect_uri=" + fullURL + "/kakao/callback"

}

function goToReferrerPage() {
	if (sessionStorage.getItem('referrer') != null) {
		window.location.href = sessionStorage.getItem('referrer');
	} else {
		window.location.href = 'index'
	}
}