
function AddComment(board_num, ref_comment, recomment) {
	// 각 경우에 따라 필드를 선택
	const commentField = (recomment === 0) ? document.getElementById('comment') : document.getElementById('re-comment');
	const userNameField = (recomment === 0) ? document.getElementById('user-name') : document.getElementById('re-user-name');
	const pwdField = (recomment === 0) ? document.getElementById('pwd') : document.getElementById('re-pwd');

	// 각 경우에 따라 값을 선택
	const commentValue = commentField.value;
	const userNameValue = userNameField.value;
	const pwdValue = pwdField.value;

	var email;
	var feedback = 'N';
	if (confirm('대댓글 알림을 받으시겠습니까?')) {
		feedback = 'Y'
		if (!isBlogUser()) {
			email = prompt('이메일을 입력해주세요.', '')
		}
	}
	const data = {
		board_num: board_num,
		ref_comment: ref_comment,
		comment: commentValue,
		user_name: userNameValue,
		pwd: pwdValue,
		email: email

	}

	const option = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'FEEDBACK': feedback
		},
		body: JSON.stringify(data),
	}

	fetch('/comment', option)
		.then(response => {
			switch (response.status) {
				case 200:
					alert('댓글이 등록되었습니다.')
					commentField.value = ''

					return response.text()
				case 400:
					alert(response.headers.get('invalidValue') + '값을 확인해주세요.')
					throw new Error();;
				case 401:
					alert('등록할 수 없는 이름 입니다.')
					throw new Error();;
				default:
					alert('댓글 등록에 실패하였습니다.')

			}
		})
		.then(data => {

			// 현재 URL을 가져옵니다.
			var currentURL = window.location.href;

			// URL에서 마지막 '/' 뒤의 문자열을 가져옵니다.
			var boardNum = currentURL.substr(currentURL.lastIndexOf('/') + 1);


			if (ref_comment == 0) {
				var commentHtml = `<li id="comment_area${data}">
					<p>
						<b id="user-name${data}">${userNameValue}</b> <span>${getCurrentDateTime()}</span>
					</p>
					<p id="comment${data}">${commentValue}</p>
					<p id="edit${data}">
						<fn onclick="ReComment(${boardNum},${data},${data},0)">대댓글 </fn>
						<fn onclick="ReComment(${boardNum},${data},${data},1)">수정</fn>
						<fn onclick="DelComment(${boardNum}, ${data})">삭제</fn>
					</p>
				</li>`;

				var newElement = document.createElement('div');
				newElement.innerHTML = commentHtml;

				var parentElement = document.getElementById('comment_area');
				var firstChild = parentElement.firstChild;

				parentElement.insertBefore(newElement.firstChild, firstChild);

			} else {
				document.getElementById('recomment').remove();
				
				var commentHtml = `<li id="comment_area${data}" class="recomment">
					<i class="icon-terminal"></i>
                    <p>
                        <b id="user-name${data}">${userNameValue}</b> <span>${getCurrentDateTime()}</span>
                    </p>
                    <p id="comment${data}">${commentValue}</p>
                    <p id="edit${data}">
                        <fn onclick="ReComment(${boardNum},${ref_comment},${data},1)">수정</fn>
                        <fn onclick="DelComment(${boardNum}, ${data})">삭제</fn>
                    </p>
                </li>`;

				var tempElement = document.createElement('div');
				tempElement.innerHTML = commentHtml;

				var commentAreaElement = document.getElementById('comment_area' + ref_comment);
				var parentElement = commentAreaElement.parentNode;
				var nextSibling = commentAreaElement.nextSibling;

				// comment_area + ref_comment 요소 다음에 class="recomment"를 가지지 않은 첫 번째 요소 앞에 추가
				while (nextSibling) {
					if (nextSibling.nodeType === 1 && !nextSibling.classList.contains('recomment')) {
						break;
					}
					nextSibling = nextSibling.nextSibling;
				}

				parentElement.insertBefore(tempElement.firstChild, nextSibling);
			}

		})

}


function DelComment(ref, num) {
	if (confirm('삭제 하시겠습니까?')) {
		let pwd = '';

		if (!isBlogUser()) {
			pwd = prompt('비밀번호를 입력하세요', '');
		} else {
			pwd = "BLOG_USER"
		}

		if (pwd == '') {
			alert('비밀번호를 입력하세요');
		} else {
			const form = {
				num: num,
				board_num: ref,
				pwd: pwd
			};

			const option = {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(form)
			};

			fetch('/del-comment', option)
				.then(response => {
					switch (response.status) {
						case 200:
							alert('댓글이 삭제되었습니다.');
							return response.text();
						case 403:
							if (isBlogUser()) {
								alert('본인 댓글이 아닙니다.');
							} else {
								alert('비밀번호가 틀립니다.');

							}
							throw new Error();
						default:
							alert('댓글 삭제에 실패하였습니다.');
					}
				})
				.then((data) => {
					if (data == 1) {
						document.getElementById('comment_area' + num).remove();
					} else {
						document.getElementById('user-name' + num).innerHTML = 'NULL';
						document.getElementById('comment' + num).innerHTML = '삭제된 댓글 입니다.';
						document.getElementById('edit' + num).remove();
					}
				});
		}
	}
}

function UpdateComment(num) {
	const comment = document.getElementById('update-comment').value;
	const userName = document.getElementById('update-user-name').value;
	const pwd = document.getElementById('update-pwd').value;

	const form = {
		num: num,
		comment: comment,
		user_name: userName,
		pwd: pwd
	};

	const option = {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(form)
	};

	fetch('/comment', option)
		.then(response => {
			switch (response.status) {
				case 200:
					document.getElementById('comment' + num).innerText = comment;
					document.getElementById('user-name' + num).innerText = userName;
					document.getElementById('recomment').remove();
					alert('댓글이 수정되었습니다.');
					break;
				case 400:
					alert(response.headers.get('invalidValue') + '값을 확인해주세요.');
					break;
				case 401:
					alert('수정 할 수 없는 이름 입니다.');
					break;
				default:
					alert('댓글 수정에 실패하였습니다.');
			}
		});
}

function ReComment(ref, ref_comment, pos, update) {
	const name_space = document.getElementById("user_name");
	let user_name;

	if (name_space.innerText !== "") {
		user_name = name_space.innerText.split(" ")[0];
	}

	let pwd = "HAKO_DEV_USER";

	if (update === 0) {
		if (confirm('대댓글을 작성하시겠습니까?')) {
			let input_console = '<div id="recomment" class="comment-input">';
			input_console += '<textarea id="re-comment"> </textarea>';
			input_console += '<p>';

			if (name_space.innerText === "") {
				input_console += '이름 : <input type="text" id="re-user-name" /> ';
				input_console += '암호 : <input type="text" id="re-pwd" />';
			} else {
				input_console += '이름 : <input type="text" id="re-user-name" value ="' + user_name + '" readonly/> ';
				input_console += '암호 : <input type="password" id="re-pwd" value ="HAKO_DEV_USER" readonly/>';
			}

			input_console += '<input id="comment-submit" type="button" style="display: none" />';
			input_console += '<input id="comment-submit" type="button" style="display: none" />';
			input_console += '<label for="comment-submit"><i class="icon-paper-plane" onclick="AddComment(' + ref + ', ' + ref_comment + ', 1)"></i></label>';
			input_console += '</p>';
			input_console += '</div>';

			if (document.getElementById('recomment')) {
				document.getElementById('recomment').remove();
			}

			document.getElementById('comment_area' + pos).insertAdjacentHTML('beforeend', input_console);
		}
	} else {
		if (confirm('댓글을 수정 하시겠습니까?')) {
			user_name = document.getElementById('user-name' + pos).innerText;
			const comment = document.getElementById('comment' + pos).innerText;
			let input_console = '<div id="recomment" class="comment-input">';
			input_console += '<textarea id="update-comment" >' + comment + ' </textarea>';
			input_console += '<p>';

			if (name_space.innerText === "") {
				input_console += '이름 : <input type="text" id="update-user-name" value="' + user_name + '" /> ';
				input_console += '암호 : <input type="password" id="update-pwd" />';
			} else {
				input_console += '이름 : <input type="text" id="update-user-name" value ="' + user_name + '" readonly/> ';
				input_console += '암호 : <input type="password" id="update-pwd" value ="HAKO_DEV_USER" readonly/>';
			}

			input_console += '<input id="comment-submit" type="button" style="display: none" />';
			input_console += '<input id="comment-submit" type="button" style="display: none" />';
			input_console += '<label for="comment-submit"><i class="icon-paper-plane" onclick="UpdateComment(' + pos + ')"></i></label>';
			input_console += '</p>';
			input_console += '</div>';

			if (document.getElementById('recomment')) {
				document.getElementById('recomment').remove();
			}

			document.getElementById('comment_area' + pos).insertAdjacentHTML('beforeend', input_console);
			document.getElementById('update-comment').focus();
		}
	}
}


function getCurrentDateTime() {
	var now = new Date();

	var year = now.getFullYear();

	var month = now.getMonth() + 1;
	month = month < 10 ? '0' + month : month;

	var day = now.getDate();
	day = day < 10 ? '0' + day : day;

	var hours = now.getHours();
	hours = hours < 10 ? '0' + hours : hours;

	var minutes = now.getMinutes();
	minutes = minutes < 10 ? '0' + minutes : minutes;

	var seconds = now.getSeconds();
	seconds = seconds < 10 ? '0' + seconds : seconds;

	var formattedDateTime = year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds;

	return formattedDateTime;
}


function isBlogUser() {
	return sessionStorage.getItem('loginState') == 'true'
}