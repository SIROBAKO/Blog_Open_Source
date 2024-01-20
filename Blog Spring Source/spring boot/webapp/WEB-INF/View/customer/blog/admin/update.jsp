<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="editor-space">


	<form method="post" action="/admin/del_update"
		enctype="multipart/form-data" onsubmit="return confirm('수정 하시겠습니까?')"
		style="max-width: 800px; margin: 0 auto;">
		<h3>제목</h3>
		<input id = "title" type="text" name="title" value="${board.title}" />

		<script>
		$(document).ready(function() {
	        $('input[name="description"]').on('input', function() {
	          var input = $(this).val();
	          var count = input.length;
	          $('#count_description').text('글자 수: ' + count);
	        });
	      });
		</script>

		<h4>
			부 제목
			<h5 id="count_description"></h5>
		</h4>
		<input type="text" name="description" oninput="countCharacters()"
			value="${board.description}" />

		<h4>썸네일</h4>
		<input type="file" name="thumbnail" />
		<h4>카테고리</h4>
		<select name="category">
			<tag:forEach var="x" items="${categorys}">
				<tag:if test="${!x.query.equals('') && !x.purpose.equals('비활성') }">
					<option value="${x.query}"
						<tag:if test="${x.query.equals(board.category)}">
						selected
					</tag:if>>${x.category}</option>
				</tag:if>
			</tag:forEach>
		</select>


		<div id="temp" style="display: none">${board.contents}</div>


		<textarea id="summernote" name="content"></textarea>

		<input type="submit" name="doit" value="비공개"> <input
			type="text" name="num" style="display: none;" value="${param.num }">
		<input type="submit" name="doit" value="업데이트"> <input
			type="submit" name="doit " value="삭제"
			onclick="return confirm('삭제하시겠습니까?')">
	</form>

</div>

<script>	
// 처음 실행시 
window.onload = function() {
    
	$('#summernote').summernote('code', document.getElementById("temp").innerHTML );
  };

$(document).ready(function () {
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
                ['para', [ 'paragraph']],
                ['height', ['height']],
                ['insert', ['picture', 'link', 'hr','video']],
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

		        onImageUpload: function (files, editor, welEditable) {
		            // 파일 업로드(다중업로드를 위해 반복문 사용)
		            for (var i = files.length - 1; i >= 0; i--) {
		            	 var fileName = files[i].name;
		     	        var caption = prompt('이미지 설명 :', fileName);
		     	        if(caption== ""){
		     	        	caption  = "이미지";
		     	        }
		                uploadSummernoteImageFile(files[i], this, caption)
		            }
		        },   onMediaDelete: function($target, editor, $editable) {
		        	if (confirm("이미지를 삭제 하시겠습니까?")) {
      				    var deletedImageUrl = $target.attr('src').split('/').pop();
      				   
      				    // ajax 함수 호출
      				    deleteSummernoteImageFile(deletedImageUrl);
      				}
		          },
		          onInit: function() {
		                $('.note-editable').css('font-size', '16px'); // 기본 글씨 크기 설정
		                $('.note-editable').css('line-height', '1.8'); // 행간 설정
		            }
		     	 
            }   
        },
    )
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
        success: function (data) {
            $(el).summernote('editor.insertImage', data.url, function ($image) {
                $image.attr('alt', caption); // 캡션 정보를 이미지의 alt 속성에 설정
            });
        },
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

document.addEventListener('keydown', function (event) {
    // 사용자가 'S' 키를 누르고 Ctrl 키가 눌렸는지 (또는 Mac의 경우 Command 키) 확인합니다.
    if ((event.ctrlKey || event.metaKey) && event.key === 's') {
        event.preventDefault(); // 브라우저 기본 저장 동작을 막습니다.

        // 데이터 저장 로직을 처리하는 함수를 호출합니다.
        saveData();
    }
});


function saveData() {
    // 폼이나 데이터가 저장된 위치에서 데이터를 가져옵니다.
    var num = ${param.num};
    var content = $('#summernote').summernote('code');
    var category = $('select[name="category"]').val();
    var description = $('input[name="description"]').val();
    var title = $('input[id="title"]').val();

    // JSON 데이터를 생성합니다.
    var jsonData = {
        num: num,
        content: content,
        category: category,
        description: description,
        title: title
    };

    // 필요한 데이터를 가져온 후에는 서버로 전송하기 위한 AJAX 요청을 보냅니다.
    $.ajax({
        type: 'POST',
        url: '/admin/updatePost',
        data: JSON.stringify(jsonData),
        contentType: 'application/json',  // JSON 형식으로 데이터를 전송할 때 필수
        processData: false,
        dataType: 'text', 
        success: function (data) {
            alert(data);
        },
        error: function (error) {
            console.error('데이터 저장 중 에러 발생:', error);
        }
    });
}





</script>