var currentURL = window.location.href

if (!currentURL.includes("blog")) {

	user_info()
}


// 회원 가입 페이지 피드백 이벤트
if (currentURL.includes("join")) {

	window.onload = function() {

		if (currentURL.includes("kakao")) {
			const id_input = document.getElementById("user_id")
			const pwd_input = document.getElementById("user_pwd")
			const pwd_re_enter_input = document.getElementById("user_pwd_re_enter")
			const idLabel = document.querySelector('label[for="user_id"]')
			const pwdLabel = document.querySelector('label[for="user_pwd"]')
			const pwdReLabel = document.querySelector('label[for="user_pwd_re_enter"]')
			id_input.style.display = 'none'
			pwd_input.style.display = 'none'
			pwd_re_enter_input.style.display = 'none'
			idLabel.style.display = 'none'
			pwdLabel.style.display = 'none'
			pwdReLabel.style.display = 'none'

			const name_input = document.getElementById("user_name_form")
			const email_input = document.getElementById("user_email_form")
			name_input.addEventListener("input", checkName(''))
			email_input.addEventListener("input", checkEmail(''))

			const user_id = document.getElementById("user_id")
			const user_pwd = document.getElementById("user_pwd")

			user_id.value = currentURL.split("kakao")[1]
			user_id.setAttribute("readonly", true);
			user_pwd.value = 'kakaoUser'
			user_pwd.setAttribute("readonly", true);

		} else {
			const id_input = document.getElementById("user_id")
			const pwd_input = document.getElementById("user_pwd")
			const pwd_re_enter_input = document.getElementById("user_pwd_re_enter")
			const name_input = document.getElementById("user_name_form")
			const email_input = document.getElementById("user_email_form")

			// 이벤트 등록 
			id_input.addEventListener("input", checkId)
			pwd_input.addEventListener("input", checkPwd)
			pwd_re_enter_input.addEventListener("input", checkPwd)
			name_input.addEventListener("input", checkName(''))
			email_input.addEventListener("input", checkEmail(''))
		}
	}
}

if (currentURL.includes("find")) {

	window.onload = function() {
		findHandler()
		const pwd_input = document.getElementById("user_pwd")
		const pwd_re_enter_input = document.getElementById("user_pwd_re_enter")
		pwd_input.addEventListener("input", checkPwd)
		pwd_re_enter_input.addEventListener("input", checkPwd)
	}
}

// 회원 가입 페이지 피드백 이벤트
if (currentURL.includes("mypage")) {
	window.onload = function() {

		const name_input = document.getElementById("user_name_form")
		const email_input = document.getElementById("user_email_form")

		// 이벤트 등록 
		name_input.addEventListener("input", checkName('user'))
		email_input.addEventListener("input", checkEmail('user'))

		user_info()
	}
}

// 아이디 형식 및 중복 체크 
function checkId() {

	const id_value = document.getElementById("user_id").value
	const idMessage = document.getElementById("id_message")

	if (id_value == '') {
		idMessage.textContent = ''
	} else {
		const form = {
			user_id: id_value
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
				return response.json()
			})
			.then(data => {
				if (data.purpose == "Success") {
					idMessage.textContent = "사용가능한 아이디 입니다."
					idMessage.style.color = 'var(--success-message) '
				} else {
					idMessage.textContent = data.message
					idMessage.style.color = 'var(--error-message)'
				}
			})
	}
}


// 비밀번호 형식 체크
function checkPwd() {

	const pwd_value = document.getElementById("user_pwd").value
	const pwd_re_enter_value = document.getElementById("user_pwd_re_enter").value
	const pwdMessage = document.getElementById("pwd_message")
	const passwordPattern = /^[a-zA-Z0-9!@#$%^&*()_+\-={}[\]:"'<>,.?/\\|]{8,15}$/

	if (passwordPattern.test(pwd_value) == 0) {
		pwdMessage.textContent = "8~15자 이내 영문,숫자,기호만"
		pwdMessage.style.color = 'var(--error-message) '
	} else if ((pwd_value != pwd_re_enter_value) && pwd_re_enter_value != "") {
		pwdMessage.textContent = "비밀번호가 일치하지 않습니다."
		pwdMessage.style.color = 'var(--error-message) '
	} else {
		pwdMessage.textContent = "사용가능한 비밀번호 입니다."
		pwdMessage.style.color = 'var(--success-message) '
	}
}

// 이름 형식 및 중복 확인
function checkName(user_id) {
	return function(event) {

		const name_value = document.getElementById("user_name_form").value
		const nameMessage = document.getElementById("name_message")

		if (name_value == '') {
			nameMessage.textContent = ''
		} else {
			const form = {
				user_name: name_value,
				user_id: user_id
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
					return response.json()
				})
				.then(data => {
					nameMessage.textContent = data.message
					if (data.purpose == "Success") {

						nameMessage.style.color = 'var(--success-message) '
					} else {
						nameMessage.style.color = 'var(--error-message)'
					}
				})
		}
	}
}


// 이름 형식 및 중복 확인
function checkEmail(user_id) {
	return function(event) {

		const email_value = document.getElementById("user_email_form").value
		const emailMessage = document.getElementById("email_message")

		if (email_value == '') {
			emailMessage.textContent = ''
		} else {
			const form = {
				user_email: email_value,
				user_id: user_id
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
					return response.json()
				})
				.then(data => {
					emailMessage.textContent = data.message
					if (data.purpose == "Success") {

						emailMessage.style.color = 'var(--success-message) '
					} else {

						emailMessage.style.color = 'var(--error-message)'
					}
				})
		}
	}
}


// 로그인
function user_login() {
	var user_id = document.getElementById("user_id").value
	var user_pwd = document.getElementById("user_pwd").value

	const form = {
		user_id: user_id,
		user_pwd: user_pwd
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
			return response.json()
		})
		.then(data => {
			if (data.purpose == "Success") {
				window.history.back()
			} else {
				alert(data.message)
			}

		})

}


// 유저 정보 반환
function user_info() {

	const options = {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json',
		}
	}

	fetch('/user', options)
		.then(response => {
			return response.json()
		})
		.then(data => {

			const user_name = document.getElementById("user_name")
			const user_login = document.getElementById("user_login")
			const user_logout = document.getElementById("user_logout")

			const user_name_header = document.getElementById("user_name_header")
			const user_login_header = document.getElementById("user_login_header")
			const user_logout_header = document.getElementById("user_logout_header")

			if (data.purpose == "Success") {



				user_name.style.display = "block"
				user_name_header.style.display = "block"
				user_name.innerHTML = '<a href="/blog-mypage">' + data.user_name + ' 님</a>'
				user_name_header.innerHTML = '<a href="/blog-mypage">' + data.user_name + ' 님</a>'

				user_login.style.display = "none"
				user_login_header.style.display = "none"
				user_logout.style.display = "block"
				user_logout_header.style.display = "block"

				if (window.location.href.includes("detail")) {

					window.onload = function() {
						const comment_name = document.getElementById("user-name")
						const comment_password = document.getElementById("pwd")

						comment_name.value = data.user_name
						comment_password.value = "HAKO_DEV_USER"
						comment_name.setAttribute("readonly", true)
						comment_password.setAttribute("readonly", true)
					}

				}
				if (window.location.href.includes("mypage")) {
					document.getElementById("user_name_form").value = data.user_name
					document.getElementById("user_email_form").value = data.user_email
				}
			} else if (data.purpose == "Validation Failed") {
				user_login.style.display = "block"
				user_token_ref(user_info)
			} else {
				user_login.style.display = "block"
			}


		})
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
			return response.json()
		})
		.then(data => {
			if (data.purpose == "Success") {

				const user_name = document.getElementById("user_name")
				const user_login = document.getElementById("user_login")
				const user_logout = document.getElementById("user_logout")



				user_name.style.display = "none"
				user_login.style.display = "block"
				user_logout.style.display = "none"

				alert("로그아웃에 성공했습니다.")
				if (currentURL.includes("blog")) {
					window.history.go(-1)
				} else {
					user_info()
				}
			}

		})
}


// 토큰 재발급 후 함수 재실행
function user_token_ref(callback) {
	const options = {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json',
		}
	}

	fetch('/token', options)
		.then(response => {
			return response.json()
		})
		.then(data => {
			if (data.purpose == "Success") {
				callback()
			}
		})
}


// 회원가입
function user_join() {

	const user_id = document.getElementById("user_id")
	const user_pwd = document.getElementById("user_pwd")
	const user_name = document.getElementById("user_name_form")
	const user_email = document.getElementById("user_email_form")

	const pwd_value = document.getElementById("user_pwd").value
	const pwd_re_enter_value = document.getElementById("user_pwd_re_enter").value

	if ((pwd_value == pwd_re_enter_value) || currentURL.includes("kakao")) {
		const form = {
			user_id: user_id.value,
			user_pwd: user_pwd.value,
			user_name: user_name.value,
			user_email: user_email.value
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
				return response.json()
			})
			.then(data => {
				if (data.purpose == "Success") {
					alert('회원가입에 성공했습니다.')
					window.history.back()
				} else {

					alert(data.message)

					if (data.message.includes("아이디")) {
						user_id.focus()
					} else if (data.message.includes("비밀번호")) {
						user_pwd.focus()
					} else if (data.message.includes("닉네임")) {
						user_name.focus()
					} else if (data.message.includes("이메일")) {
						user_email.focus()
					}
				}

			})
	} else {
		alert("비밀번호가 일치하지 않습니다.")
	}
}

// 회원정보 수정
function user_update() {


	const user_name = document.getElementById("user_name_form")
	const user_email = document.getElementById("user_email_form")


	const form = {
		user_name: user_name.value,
		user_email: user_email.value
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
			return response.json()
		})
		.then(data => {
			if (data.purpose == "Success") {
				alert('회원정보 수정에 성공했습니다.')
				user_info()

			} else if (data.purpose == "Validation Failed") {

				user_token_ref(user_update)
			} else {

				alert(data.message)
				if (data.message.includes("닉네임")) {
					user_name.focus()
				} else if (data.message.includes("이메일")) {
					user_email.focus()
				}
			}

		})

}

// 회원탈퇴
function user_delete() {

	if (confirm("회원탈퇴 하시겠습니까?")) {
		const options = {
			method: 'DELETE',
			headers: {
				'Content-Type': 'application/json',
			},

		}

		fetch('/user', options)
			.then(response => {
				return response.json()
			})
			.then(data => {
				if (data.purpose == "Success") {
					alert('회원탈퇴에 성공했습니다.')
					window.history.back()
				} else if (data.purpose == "Validation Failed") {

					user_token_ref(user_delete)
				} else {
					alert(data.message)
				}

			})
	}

}

function findHandler() {
	var selectedValue = document.getElementById("find-handler").value;
	const id_input = document.getElementById("user_id")
	const idLabel = document.querySelector('label[for="user_id"]')
	const pwd_input = document.getElementById("user_pwd")
	const pwdLabel = document.querySelector('label[for="user_pwd"]')
	const pwd_re_enter_input = document.getElementById("user_pwd_re_enter")
	const pwdReLabel = document.querySelector('label[for="user_pwd_re_enter"]')
	const find_button = document.getElementById("find_button")
	const reset_button = document.getElementById("reset_button")
	const result_area = document.getElementById("result_area")
	if (selectedValue == 'id') {
		id_input.style.display = 'none'
		pwd_input.style.display = 'none'
		pwd_re_enter_input.style.display = 'none'


		idLabel.style.display = 'none'
		pwdLabel.style.display = 'none'
		pwdReLabel.style.display = 'none'
		reset_button.style.display = 'none'
		find_button.style.display = 'block'
		result_area.style.display = 'block'
	} else {

		id_input.style.display = 'block'
		pwd_input.style.display = 'block'
		pwd_re_enter_input.style.display = 'block'

		idLabel.style.display = 'block'
		pwdLabel.style.display = 'block'
		pwdReLabel.style.display = 'block'
		find_button.style.display = 'none'
		reset_button.style.display = 'block'
		result_area.style.display = 'none'
	}

}


function find_id() {
	const user_email = document.getElementById("user_email_form").value
	const result_area = document.getElementById("result_area").innerHTML
	const form = {
		user_email: user_email
	}

	const options = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(form),
	}

	fetch('/user/find', options)
		.then(response => {
			return response.json()
		})
		.then(data => {

			if (data.purpose == "Success") {
				result_area = data.message
			} else {
				alert(data.message)
			}
		})

}

function reset_pwd() {
	const user_id = document.getElementById("user_id").value
	const user_email = document.getElementById("user_email_form").value
	const pwd_value = document.getElementById("user_pwd").value
	const pwd_re_enter_value = document.getElementById("user_pwd_re_enter").value

	if (pwd_value == pwd_re_enter_value) {

		const form = {
			user_id: user_id,
			user_email: user_email,
			user_pwd: pwd_value
		}

		const options = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(form),
		}

		fetch('/user/reset', options)
			.then(response => {
				return response.json()
			})
			.then(data => {
				alert(data.message)

			})

	}
	else {
		alert("비밀번호가 일치하지 않습니다.")
	}

}


function kakaoLogin() {
	window.location.href = "https://kauth.kakao.com/oauth/authorize?response_type=code&" +
		"client_id=54df3b87400b7947ea7a60537e18d767&redirect_uri=https://sirobako.co.kr/kakao/callback"
	/*
		window.location.href = "https://kauth.kakao.com/oauth/authorize?response_type=code&" +
			"client_id=54df3b87400b7947ea7a60537e18d767&redirect_uri=http://localhost:8080/kakao/callback"*/

}