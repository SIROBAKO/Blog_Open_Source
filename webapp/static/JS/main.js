window.onload = function() {

	if (getCookie("color-theme") == null) {
		setCookie('color-theme', 'light', 3)
	} else {
		setCookie('color-theme', getCookie("color-theme"), 3)
	}

	if (getCookie("color-theme") == 'light') {
		changeLight()
	} else {

		changeDark()
	}
	document.documentElement.setAttribute('color-theme', getCookie("color-theme"))


}
function DarkModeChange() {
	mode = document.documentElement.getAttribute('color-theme')

	if (mode == 'dark') {
		setCookie('color-theme', 'light', 3)
		document.documentElement.setAttribute('color-theme', 'light')
		document.getElementById('logo').src = '/image/logo/HAK-logo.png'

		changeLight()
	} else {
		setCookie('color-theme', 'dark', 3)
		document.documentElement.setAttribute('color-theme', 'dark')
		document.getElementById('logo').src = '/image/logo/HAK-logo-white.png'

		changeDark()
	}
}

function changeDark() {
	document.getElementById('logo').src = '/image/logo/HAK-logo-white.png'
	document.querySelector('label[for="Mode-Console"]').innerHTML = 'Light'
	document.getElementById('Mode-Console').className = 'icon-sun'

	document.getElementById('mobile-logo').src =
		'/image/logo/HAK-logo-white.png'
	document.querySelector('label[for="mobile-Mode-Console"]').innerHTML =
		'Light'
	document.getElementById('mobile-Mode-Console').className = 'icon-sun'
}
function changeLight() {
	document.getElementById('logo').src = '/image/logo/HAK-logo.png'
	document.querySelector('label[for="Mode-Console"]').innerHTML = 'Dark'
	document.getElementById('Mode-Console').className = 'icon-moon'

	document.getElementById('mobile-logo').src = '/image/logo/HAK-logo.png'
	document.querySelector('label[for="mobile-Mode-Console"]').innerHTML =
		'Dark'
	document.getElementById('mobile-Mode-Console').className = 'icon-moon'
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
	window.scrollTo(0, 0)
}

function setCookie(cookie_name, value, days) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + days);
	// 설정 일수만큼 현재시간에 만료값으로 지정

	var cookie_value = escape(value) + ((days == null) ? '' : '; expires=' + exdate.toUTCString());
	document.cookie = cookie_name + '=' + cookie_value;
}

function getCookie(cookie_name) {
	var x, y;
	var val = document.cookie.split(';');

	for (var i = 0; i < val.length; i++) {
		x = val[i].substr(0, val[i].indexOf('='));
		y = val[i].substr(val[i].indexOf('=') + 1);
		x = x.replace(/^\s+|\s+$/g, ''); // 앞과 뒤의 공백 제거하기
		if (x == cookie_name) {
			return unescape(y); // unescape로 디코딩 후 값 리턴
		}
	}
}







function AddComment(num, id, recomment) {



	if (recomment == 0) {
		comment = document.getElementById('comment').value
		user_name = document.getElementById('user-name').value
		pwd = document.getElementById('pwd').value

		comment_fus = document.getElementById('comment')
		user_name_fus = document.getElementById('user-name')
		pwd_fus = document.getElementById('pwd')
	} else if (recomment == 1) {
		comment = document.getElementById('re-comment').value
		user_name = document.getElementById('re-user-name').value
		pwd = document.getElementById('re-pwd').value

		user_name_fus = document.getElementById('re-user-name')
		comment_fus = document.getElementById('re-comment')
		pwd_fus = document.getElementById('re-pwd')
	}
	//닉네임 필수 입력
	if (user_name == null || user_name == "") {
		alert("이름을 입력하세요.");
		user_name_fus.focus();
	}
	else if (user_name.length < 2 || user_name.length > 20) {

		alert("이름은 1~20자 이내로 간결하게 정해주세요.");
		user_name_fus.focus();

	} else if (pwd == null || pwd == "") {

		alert("비밀번호를 입력하세요.");
		pwd_fus.focus();

	} else if (pwd.length < 2 || pwd.length > 20) {

		alert("비밀번호는 2~20자 이내로 정해주세요.");
		pwd_fus.focus();

	} else if (comment == null || comment == "") {

		alert("내용을 입력하세요.");
		comment_fus.focus();

	} else {

		const form = {
			num: num,
			id: id,
			comment: comment,
			user_name: user_name,
			pwd: pwd,
			doit: 'insert',
		}

		const option = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(form),
		}

		fetch('/Comment', option)
			.then(res => res.text())
			.then(text => {
				result = text.replace(/[^0-9]/g, '')
				if (result == 1) {
					alert("댓글이 등록되었습니다.")
					location.reload()
				} else if (result == 2) {
					alert("등록할 수 없는 이름 입니다.")

				} else {
					alert("댓글 등록에 실패하였습니다.")

				}
			}
			);
	}
}



function DelComment(id) {
	if (confirm('삭제 하시겠습니까?')) {

		pwd = prompt("비밀번호를 입력하세요", "");
		if (pwd == "") {
			alert("비밀번호를 입력하세요")
		} else {
			const form = {
				pwd: pwd,
				id: id,
				doit: "del",
			}

			const option = {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(form),
			}

			fetch('/Comment', option)
				.then(res => res.text())
				.then(text => {

					result = text.replace(/[^0-9]/g, '')
					if (result == 1) {
						document.getElementById(id).remove()
						alert("댓글이 삭제되었습니다.")
					} else if (result == 0) {
						alert("댓글이 삭제에 실패하였습니다.")
					} else if (result == 2) {
						alert("비밀번호가 틀립니다.")
					} else if (result == 3) {

						document.getElementById("user-name" + id).innerHTML = "NULL"
						document.getElementById("comment" + id).innerHTML = "삭제된 댓글 입니다."
						document.getElementById("edit" + id).remove()
						alert("댓글이 삭제되었습니다.")
					}


				});
		}
	}

}


function UpdateComment(id) {

	comment = document.getElementById('update-comment').value
	user_name = document.getElementById('update-user-name').value
	pwd = document.getElementById('update-pwd').value

	user_name_fus = document.getElementById('update-user-name')
	comment_fus = document.getElementById('update-comment')
	pwd_fus = document.getElementById('update-pwd')

	//닉네임 필수 입력
	if (user_name == null || user_name == "") {
		alert("이름을 입력하세요.");
		user_name_fus.focus();
	}
	else if (user_name.length < 2 || user_name.length > 20) {

		alert("이름은 1~20자 이내로 간결하게 정해주세요.");
		user_name_fus.focus();

	} else if (pwd == null || pwd == "") {

		alert("비밀번호를 입력하세요.");
		pwd_fus.focus();

	} else if (comment == null || comment == "") {

		alert("내용을 입력하세요.");
		comment_fus.focus();

	} else {

		const form = {
			id: id,
			comment: comment,
			user_name: user_name,
			pwd: pwd,
			doit: 'update',
		}

		const option = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(form),
		}

		fetch('/Comment', option)
			.then(res => res.text())
			.then(text => {
				result = text.replace(/[^0-9]/g, '')
				if (result == 1) {
					document.getElementById("comment" + id).innerText = comment
					document.getElementById("user-name" + id).innerText = user_name
					document.getElementById("recomment").remove()
					alert("댓글이 수정되었습니다.")

				} else if (result == 0) {
					alert("댓글이 수정에 실패하였습니다.")
				} else if (result == 2) {
					alert("비밀번호가 틀립니다.")
					pwd_fus.focus();
				} else if (result == 3) {
					alert("수정 할 수 없는 이름 입니다.")

				} else {
					alert("댓글 수정에 실패하였습니다.")

				}
			}
			);
	}
}

function ReComment(num, id, pos, update) {



	if (update == 0) {

		if (confirm('대댓글을 작성하시겠습니까?')) {
			var input_console = '<div id="recomment" class="comment-input">'
			input_console += '<textarea id="re-comment"> </textarea>'
			input_console += '<p>'
			input_console += '이름 : <input type="text" id="re-user-name" /> '
			input_console += '암호 : <input type="text" id="re-pwd" />'
			input_console += '<input id="comment-submit" type="button" style="display: none" />'
			input_console += '<input id="comment-submit" type="button" style="display: none" />'
			input_console += '<label for="comment-submit"><i class="icon-paper-plane" onclick="AddComment(' + num + ', ' + id + ',1)"></i></label>'
			input_console += '</p>'
			input_console += '</div>'

			if (document.getElementById("recomment")) {
				document.getElementById("recomment").remove()
			}

			document.getElementById(pos).insertAdjacentHTML("beforeend", input_console)
		}
	} else {
		if (confirm('댓글을 수정 하시겠습니까?')) {
			user_name = document.getElementById("user-name" + pos).innerText
			comment = document.getElementById("comment" + pos).innerText
			var input_console = '<div id="recomment" class="comment-input">'
			input_console += '<textarea id="update-comment" >' + comment + ' </textarea>'
			input_console += '<p>'
			input_console += '이름 : <input type="text" id="update-user-name" value="' + user_name + '" /> '
			input_console += '암호 : <input type="text" id="update-pwd" />'
			input_console += '<input id="comment-submit" type="button" style="display: none" />'
			input_console += '<input id="comment-submit" type="button" style="display: none" />'
			input_console += '<label for="comment-submit"><i class="icon-paper-plane" onclick="UpdateComment(' + pos + ')"></i></label>'
			input_console += '</p>'
			input_console += '</div>'

			if (document.getElementById("recomment")) {
				document.getElementById("recomment").remove()
			}

			document.getElementById(pos).insertAdjacentHTML("beforeend", input_console)

			document.getElementById("update-comment").focus()
		}
	}
}


var page = 1
function AddList() {
	page += 1


	const form = {
		page: page,

	}


	const option = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(form),
	}

	fetch('/AddList', option)
		.then(res => res.text())
		.then(text => {


			text = JSON.parse(text)

			if (parseInt(text.result) == 0) {
				alert("더 이상 게시물이 존재하지 않습니다.")
			} else if (parseInt(text.result) == 1) {

				for (i = 0; i < text.list.length; i++) {

					input_console = '<li>'
					input_console += '<a href="/detail?num=' + text.list[i].num + '">'
					input_console += '<img src="/image/fileupload/' + text.list[i].title + '/thumbnail/thumbnail.PNG" onerror="this.src=\'/image/logo/black.png\'">'
					input_console += '<h3>' + text.list[i].title + '</h3></a>'
					input_console += '<p>' + text.list[i].category + '</p></li>'

					document.getElementById("index-list").insertAdjacentHTML("beforeend", input_console)
				}
			}

		})
}

