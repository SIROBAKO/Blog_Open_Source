function AddComment(ref, ref_comment, recomment) {
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

	var name_space = document.getElementById("user_name");

	if (name_space.innerText != "") {
		user_name = name_space.innerText.split(" ")[0]
		pwd = "HAKO_DEV_USER"
	}

	//닉네임 필수 입력
	if (user_name == null || user_name == '') {
		alert('이름을 입력하세요.')
		user_name_fus.focus()
	} else if (user_name.length < 2 || user_name.length > 20) {
		alert('이름은 1~20자 이내로 간결하게 정해주세요.')
		user_name_fus.focus()
	} else if (pwd == null || pwd == '') {
		alert('비밀번호를 입력하세요.')
		pwd_fus.focus()
	} else if (pwd.length < 2 || pwd.length > 20) {
		alert('비밀번호는 2~20자 이내로 정해주세요.')
		pwd_fus.focus()
	} else if (comment == null || comment == '') {
		alert('내용을 입력하세요.')
		comment_fus.focus()
	} else {
		var email = ''

		if (user_name !== 'HAKO' && confirm('대댓글 알림을 받으시겠습니까?')) {
			var input = ""
			if (name_space.innerText == "") {
				input = prompt('이메일을 입력해주세요.', '')
			}
			if (input !== null) {
				email = input
			}
		}
		const form = {
			ref: ref,
			ref_comment: ref_comment,
			comment: comment,
			user_name: user_name,
			pwd: pwd,
			email: email
			
		}

		const option = {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(form),
		}

		fetch('/comment', option)
			.then((res) => res.text())
			.then((text) => {
				result = text.replace(/[^0-9]/g, '')
				if (result == 1) {
					alert('댓글이 등록되었습니다.')
					sendEmail(ref, ref_comment, comment, user_name, email)
					location.reload()
				} else if (result == 2) {
					alert('등록할 수 없는 이름 입니다.')
				} else {
					alert('댓글 등록에 실패하였습니다.')
				}
			})
	}
}


function DelComment(num, ref) {
	if (confirm('삭제 하시겠습니까?')) {
		var pwd = "HAKO_DEV_USER"
		var name_space = document.getElementById("user_name");

		if (name_space.innerText == "") {
			pwd = prompt('비밀번호를 입력하세요', '')
		} else {

			var user_name = name_space.innerText.split(" ")[0]
			pwd += user_name
		}
		if (pwd == '') {
			alert('비밀번호를 입력하세요')
		} else {
			const form = {
				num: num,
				ref: ref,
				pwd: pwd
			}

			const option = {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(form),
			}

			fetch('/del-comment', option)
				.then((res) => res.text())
				.then((text) => {
					result = text.replace(/[^0-9]/g, '')
					if (result == 1) {
						document.getElementById('comment_area' + num).remove()
						alert('댓글이 삭제되었습니다.')
					} else if (result == 0) {
						alert('댓글이 삭제에 실패하였습니다.')
					} else if (result == 2) {
						alert('비밀번호가 틀립니다.')
					} else if (result == 3) {
						document.getElementById('user-name' + num).innerHTML =
							'NULL'
						document.getElementById('comment' + num).innerHTML =
							'삭제된 댓글 입니다.'
						document.getElementById('edit' + num).remove()
						alert('댓글이 삭제되었습니다.')
					}
				})
		}
	}
}

function UpdateComment(num) {
	comment = document.getElementById('update-comment').value
	user_name = document.getElementById('update-user-name').value
	pwd = document.getElementById('update-pwd').value

	user_name_fus = document.getElementById('update-user-name')
	comment_fus = document.getElementById('update-comment')
	pwd_fus = document.getElementById('update-pwd')

	//닉네임 필수 입력
	if (user_name == null || user_name == '') {
		alert('이름을 입력하세요.')
		user_name_fus.focus()
	} else if (user_name.length < 2 || user_name.length > 20) {
		alert('이름은 1~20자 이내로 간결하게 정해주세요.')
		user_name_fus.focus()
	} else if (pwd == null || pwd == '') {
		alert('비밀번호를 입력하세요.')
		pwd_fus.focus()
	} else if (comment == null || comment == '') {
		alert('내용을 입력하세요.')
		comment_fus.focus()
	} else {
		const form = {
			num: num,
			comment: comment,
			user_name: user_name,
			pwd: pwd
		}

		const option = {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(form),
		}

		fetch('/comment', option)
			.then((res) => res.text())
			.then((text) => {
				result = text.replace(/[^0-9]/g, '')
				if (result == 1) {
					document.getElementById('comment' + num).innerText = comment
					document.getElementById('user-name' + num).innerText =
						user_name
					document.getElementById('recomment').remove()
					alert('댓글이 수정되었습니다.')
				} else if (result == 0) {
					alert('댓글이 수정에 실패하였습니다.')
				} else if (result == 2) {
					alert('비밀번호가 틀립니다.')
					pwd_fus.focus()
				} else if (result == 3) {
					alert('수정 할 수 없는 이름 입니다.')
				} else {
					alert('댓글 수정에 실패하였습니다.')
				}
			})
	}
}

function ReComment(ref, ref_comment, pos, update) {


	var name_space = document.getElementById("user_name");
	if (name_space.innerText != "") {

	} else {

		var user_name = name_space.innerText.split(" ")[0]
		pwd += user_name
	}
	if (update == 0) {

		if (confirm('대댓글을 작성하시겠습니까?')) {
			var input_console = '<div id="recomment" class="comment-input">'
			input_console += '<textarea id="re-comment"> </textarea>'
			input_console += '<p>'

			if (name_space.innerText== "") {
				input_console += '이름 : <input type="text" id="re-user-name" /> '
				input_console += '암호 : <input type="text" id="re-pwd" />'

			} else {
				var user_name = name_space.innerText.split(" ")[0]

				input_console += '이름 : <input type="text" id="re-user-name" value ="' + user_name + '" readonly/> '
				input_console += '암호 : <input type="password" id="re-pwd" value ="HAKO_DEV_USER" readonly/>'
			}
			input_console +=
				'<input id="comment-submit" type="button" style="display: none" />'
			input_console +=
				'<input id="comment-submit" type="button" style="display: none" />'
			input_console +=
				'<label for="comment-submit"><i class="icon-paper-plane" onclick="AddComment(' +
				ref +
				', ' +
				ref_comment +
				',1)"></i></label>'
			input_console += '</p>'
			input_console += '</div>'

			if (document.getElementById('recomment')) {
				document.getElementById('recomment').remove()
			}

			document
				.getElementById('comment_area' + pos)
				.insertAdjacentHTML('beforeend', input_console)
		}
	} else {
		if (confirm('댓글을 수정 하시겠습니까?')) {
			user_name = document.getElementById('user-name' + pos).innerText
			comment = document.getElementById('comment' + pos).innerText
			var input_console = '<div id="recomment" class="comment-input">'
			input_console +=
				'<textarea id="update-comment" >' + comment + ' </textarea>'
			input_console += '<p>'
			if (name_space.innerText == "") {
				input_console +=
					'이름 : <input type="text" id="update-user-name" value="' +
					user_name +
					'" /> '
				input_console += '암호 : <input type="password" id="update-pwd" />'

			} else {
				var user_name = name_space.innerText.split(" ")[0]

				input_console += '이름 : <input type="text" id="update-user-name" value ="' + user_name + '" readonly/> '
				input_console += '암호 : <input type="password" id="update-pwd" value ="HAKO_DEV_USER" readonly/>'
			}


			input_console +=
				'<input id="comment-submit" type="button" style="display: none" />'
			input_console +=
				'<input id="comment-submit" type="button" style="display: none" />'
			input_console +=
				'<label for="comment-submit"><i class="icon-paper-plane" onclick="UpdateComment(' +
				pos +
				')"></i></label>'
			input_console += '</p>'
			input_console += '</div>'

			if (document.getElementById('recomment')) {
				document.getElementById('recomment').remove()
			}

			document
				.getElementById('comment_area' + pos)
				.insertAdjacentHTML('beforeend', input_console)

			document.getElementById('update-comment').focus()
		}
	}
}


function sendEmail(ref, ref_comment, comment, user_name, email) {
	const form = {
		ref: ref,
		ref_comment: ref_comment,
		comment: comment,
		user_name: user_name,
		email: email,
	}

	const option = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(form),
	}

	fetch('/SendEmail', option)
		.then((res) => res.text())
		.then((text) => { })
}