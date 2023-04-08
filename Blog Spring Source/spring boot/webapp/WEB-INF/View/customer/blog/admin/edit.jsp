<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<!-- editor -->
<!-- include libraries(jQuery, bootstrap) -->
<link
	href="https://stackpath.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css"
	rel="stylesheet" />
<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script
	src="https://stackpath.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>

<!-- include summernote css/js -->
<link href="/Css/summernote/summernote.css" rel="stylesheet" />
<script src="/JS/summernote/summernote-ko-KR.js"></script>
<script src="/JS/summernote/summernote.js"></script>

<div class="editor-space">

	<form method="post" action="/admin/reg"
		enctype="multipart/form-data">
		<h3>제목</h3>
		<input type="text" name="title" />
		<h4>썸네일</h4>
		<input type="file" name="thumbnail" />
		<h4>카테고리</h4>
		<select name="category">
			<option value="Blog 제작">Blog 제작</option>
			<option value="드럼머신">드럼머신</option>
		</select>
		<textarea id="summernote" name="content"></textarea>
		<input type="submit">
	</form>

</div>

<script>
$(document).ready(function () {
    $('#summernote').summernote(
        {
        	codeviewFilter: false,
        	codeviewIframeFilter: true
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
                ['color', ['forecolor', 'color']],
                ['table', ['table']],
                ['para', ['ul', 'ol', 'paragraph']],
                ['height', ['height']],
                ['insert', ['picture', 'link', 'video']],
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
                'h1',
                'h2',
                'h3',
                'h4',
                'h5',
                'h6',
            ],

            callbacks: {
                onImageUpload: function (files, editor, welEditable) {
                    // 파일 업로드(다중업로드를 위해 반복문 사용)
                    for (var i = files.length - 1; i >= 0; i--) {
                        uploadSummernoteImageFile(files[i], this)
                    }
                },
            },
        },
     
    )
})

function uploadSummernoteImageFile(file, el) {
    data = new FormData()
    data.append('file', file)
    $.ajax({
        data: data,
        type: 'POST',
        url: 'uploadSummernoteImageFile',
        contentType: false,
        enctype: 'multipart/form-data',
        processData: false,
        success: function (data) {
            $(el).summernote('editor.insertImage', data.url)
        },
    })
}

</script>