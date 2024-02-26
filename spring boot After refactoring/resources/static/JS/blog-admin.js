
$(document).ready(function() {



	$('input[name="description"]').on('input', function() {
		var input = $(this).val();
		var count = input.length;
		$('#count_description').text('글자 수: ' + count);
	});

	$('#summernote').summernote(
		{
			codeviewFilter: false,
			codeviewIframeFilter: false,
			styleWithSpan: false,

			height: 500, // 에디터 높이
			minHeight: null, // 최소 높이
			maxHeight: null, // 최대 높이
			focus: true, // 에디터 로딩후 포커스를 맞출지 여부
			lang: 'ko-KR', // 한글 설정

			toolbar: [
				// 글자 크기 설정
				['style', ['style']],
				['fontsize', ['fontsize']],
				['font', ['bold', 'underline', 'clear']],
				['color', ['color']],
				['table', ['table']],
				['para', ['paragraph']],
				['height', ['height']],
				['insert', ['picture', 'link', 'hr', 'video']],
				['view', ['codeview', 'fullscreen', 'help']],

			],

			fontSizes: [
				'8',
				'9',
				'10',
				'11',
				'12',
				'14',
				'16',
				'18',
				'20',
				'22',
				'24',
				'28',
				'30',
				'36',
				'50',
				'72',
			],
			styleTags: [
				'p',

				{
					title: 'Blockquote',
					tag: 'blockquote',
					className: 'blockquote',
					value: 'blockquote',
				},
				'pre',
				{
					title: 'code_light',
					tag: 'pre',
					className: 'code_light',
					value: 'pre',
				},
				{
					title: 'code_dark',
					tag: 'pre',
					className: 'code_dark',
					value: 'pre',
				},
				'h1',
				'h2',
				'h3',
				'h4',
				'h5',
				'h6',

			],

			callbacks: {

				onImageUpload: function(files, editor, welEditable) {
					// 파일 업로드(다중업로드를 위해 반복문 사용)
					for (var i = files.length - 1; i >= 0; i--) {
						var fileName = files[i].name;
						var caption = prompt('이미지 설명 :', fileName);
						if (caption == "") {
							caption = "이미지";
						}
						uploadSummernoteImageFile(files[i], this, caption)
					}
				},
				onMediaDelete: function($target, editor, $editable) {
					// 삭제된 이미지의 파일 이름을 알기위해 split 활용
					if (confirm("이미지를 삭제 하시겠습니까?")) {
						var deletedImageUrl = $target.attr('src').split('/').pop();

						// ajax 함수 호출
						deleteSummernoteImageFile(deletedImageUrl);
					}

				},
				onInit: function() {
					$('.note-editable').css('font-size', '16px'); // 기본 글씨 크기 설정
					$('.note-editable').css('line-height', '1.8'); // 행간 설정
					$('.note-toolbar').css('background-color', 'var(--background-color-code)');
					$('.note-toolbar *').css('color', 'black');
					$('.note-editable').css('background-color', 'var(--background-color)');

				}

			}
		},
	)

	$('#summernote').summernote('code', document.getElementById("temp").innerHTML);

})



function uploadSummernoteImageFile(file, el, caption) {
	data = new FormData()
	data.append('file', file)
	$.ajax({
		data: data,
		type: 'POST',
		url: 'uploadSummernoteImageFile',
		contentType: false,
		enctype: 'multipart/form-data',
		processData: false,
		success: function(response) {

			$(el).summernote('editor.insertImage', response, function($image) {
				$image.attr('alt', caption); // 캡션 정보를 이미지의 alt 속성에 설정
			});
		}
	})
}

function deleteSummernoteImageFile(imageName) {
	data = new FormData()
	data.append('file', imageName)
	$.ajax({
		data: data,
		type: 'POST',
		url: 'deleteSummernoteImageFile',
		contentType: false,
		enctype: 'multipart/form-data',
		processData: false
	})
}


document.addEventListener('keydown', function(event) {
	// 사용자가 'S' 키를 누르고 Ctrl 키가 눌렸는지 (또는 Mac의 경우 Command 키) 확인합니다.
	if ((event.ctrlKey || event.metaKey) && event.key === 's') {
		event.preventDefault(); // 브라우저 기본 저장 동작을 막습니다.

		// 데이터 저장 로직을 처리하는 함수를 호출합니다.
		// 현재 URL에서 쿼리 매개변수 가져오기
		var queryString = window.location.search;
		var urlParams = new URLSearchParams(queryString);

		// 'num' 매개변수의 값 가져오기
		var boardNum = urlParams.get('num');

		if (boardNum == null) {
			insertBoard("Y");
		} else {
			updateBoard();
		}
	}
});

const ERROR_TEST_USER_FUNCTIONALITY = "테스트 유저는 사용할 수 없는 기능입니다.";
const ERROR_DATA_SAVE = "데이터 저장 중 에러 발생:";
const ERROR_DATA_UPDATE = "데이터 수정 중 에러 발생:";
const ERROR_DATA_DELETE = "데이터 삭제 중 에러 발생:";
const SUCCESS_BOARD_INSERT = "게시글 작성 성공";
function handleAjaxError(xhr, status, error) {
	if (xhr.status == 405) {
		alert(ERROR_TEST_USER_FUNCTIONALITY);
	} else {
		console.error(error);
	}
}

function insertBoard(hidden) {

	// 데이터 수집
	var title = $('input[id="title"]').val();
	var description = $('input[name="description"]').val();
	var contents = $('#summernote').summernote('code');
	var category = $('select[name="category"]').val();


	// JSON 데이터 생성
	var jsonData = {
		contents: contents,
		category: category,
		description: description,
		title: title,
		hidden: hidden
	};

	// FormData 객체 생성
	var formData = new FormData();
	formData.append('thumbnail', $('input[name=thumbnail]')[0].files[0]); // 파일 추가
	formData.append('jsonData', JSON.stringify(jsonData)); // JSON 데이터 추가



	// 필요한 데이터를 가져온 후에는 서버로 전송하기 위한 AJAX 요청을 보냅니다.
	$.ajax({
		type: 'POST',
		url: '/admin/board',
		data: formData,
		contentType: false,
		processData: false,
		dataType: 'text',
		success: function(data, textStatus, xhr) {
			if (data.includes("script")) {
				alert(ERROR_TEST_USER_FUNCTIONALITY)
			} else {
				alert(SUCCESS_BOARD_INSERT);
				window.location.href = "/admin/update?num=" + xhr.getResponseHeader('boardNum')
			}
		},
		error: function(error) {
			console.error(ERROR_DATA_SAVE, error);
		}
	});
}


function updateBoard(hidden) {

	var queryString = window.location.search;
	var urlParams = new URLSearchParams(queryString);

	// 'num' 매개변수의 값 가져오기
	var boardNum = urlParams.get('num');

	// 데이터 수집
	var title = $('input[id="title"]').val();
	var description = $('input[name="description"]').val();
	var contents = $('#summernote').summernote('code');
	var category = $('select[name="category"]').val();

	// JSON 데이터 생성
	var jsonData = {
		num: boardNum,
		contents: contents,
		category: category,
		description: description,
		title: title,
		hidden: hidden

	};

	// FormData 객체 생성
	var formData = new FormData();
	formData.append('thumbnail', $('input[name=thumbnail]')[0].files[0]); // 파일 추가
	formData.append('jsonData', JSON.stringify(jsonData)); // JSON 데이터 추가

	// 필요한 데이터를 가져온 후에는 서버로 전송하기 위한 AJAX 요청을 보냅니다.
	$.ajax({
		type: 'PUT',
		url: '/admin/board',
		data: formData,
		contentType: false,
		processData: false,
		dataType: 'text',
		async: false,
		success: function(data, textStatus, xhr) {
			if (xhr.status == 200) {
				alert("게시글이 업데이트 되었습니다.")
			}

		},
		error: function(xhr, status, error) {
			handleAjaxError(xhr, status, error);
		}
	});
}



function deleteBoard() {
	if (confirm('삭제하시겠습니까?')) {
		// 현재 URL에서 쿼리 매개변수 가져오기
		var queryString = window.location.search;
		var urlParams = new URLSearchParams(queryString);

		// 'num' 매개변수의 값 가져오기
		var boardNum = urlParams.get('num');

		// 필요한 데이터를 가져온 후에는 서버로 전송하기 위한 AJAX 요청을 보냅니다.
		$.ajax({
			type: 'DELETE',
			url: '/admin/board?boardNum=' + boardNum,
			contentType: false,
			processData: false,
			dataType: 'text',
			success: function(data, textStatus, xhr) {

				if (xhr.status == 200) {
					alet("게시글이 삭제되었습니다.")
					window.location.href = "/admin/list"
				}

			},
			error: function(xhr, status, error) {
				handleAjaxError(xhr, status, error);
			}
		});
	}
}


