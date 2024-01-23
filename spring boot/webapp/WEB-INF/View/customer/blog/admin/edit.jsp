<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- editor -->


<div id="edit-space" class="editor-space">

	<form method="post" action="/admin/reg" enctype="multipart/form-data"
		style="max-width: 800px; margin: 0 auto;">
		<h3>제목</h3>
		<input type="text" name="title" />
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
		<input type="text" name="description" oninput="countCharacters()" />

		<h4>썸네일</h4>
		<input type="file" name="thumbnail" />
		<h4>카테고리</h4>
		<select name="category">
			<tag:forEach var="x" items="${categorys}">
				<tag:if test="${!x.query.equals('') && !x.purpose.equals('비활성') }">
					<option value="${x.query}">${x.category}</option>
				</tag:if>
			</tag:forEach>
		</select>
		<textarea id="summernote" name="content"></textarea>
		<input type="submit" name="doit" value="임시저장"> <input
			type="submit" name="doit" value="저장">
	</form>

</div>

<script>
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


</script>