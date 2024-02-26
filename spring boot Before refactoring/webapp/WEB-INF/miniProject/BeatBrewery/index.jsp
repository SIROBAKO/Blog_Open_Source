<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!-- <link
    rel="stylesheet"
    href="../../../../../resources/static/Css/beatBrewery.css"
/>
<script
    type="text/javascript"
    src="../../../../../resources/static/JS/beatBrewery.js"
></script> -->

<link rel="stylesheet" href="/Css/beatBrewery.css" />
<script type="text/javascript" src="/JS/beatBrewery.js"></script>

<div class="upload-area" id="uploadArea">
	<p class="title">음원을 이곳에 드래그하여 업로드하세요.</p>
</div>

<audio controls id="audioPlayer" style="display: none">
	<source id="audioSource" src="" type="audio/mpeg" />
	브라우저가 오디오 태그를 지원하지 않습니다.
</audio>

<!-- 추가된 창들 -->
<div id="function">
	<div>
		<div id="pitchSction">
			<p>피치 조절</p>
			<input type="range" id="pitchControl" min="-6" max="6" step="1"
				value="0" /> <input type="text" id="pitch" value="N/A"
				style="display: none" />
		</div>
		<div id="pitchMonitor">N/A</div>
	</div>

	<div>
		<div id="bpmSection">
			<p>bpm 조절</p>
			<input type="range" id="bpmControl" min="-20" max="20" step="1"
				value="0" /> <input type="text" id="bpm" value="N/A"
				style="display: none" />
		</div>
		<div id="bpmMonitor">N/A</div>
	</div>

	<div>
		<div id="metronomeSection">
			<p>메트로놈 삽입</p>
			<input type="range" id="metronomeControl" min="0" max="6" step="1"
				value="0" class="slider" />
		</div>
		<div id="metronomeMonitor">0/0</div>
	</div>
	<div id="four-four" class="beatSection" style="display: none">
		<button class="beat1">Λ</button>
		<button class="beat2"></button>
		<button class="beat3"></button>
		<button class="beat4"></button>
	</div>
	<div id="three-four" class="beatSection" style="display: none">
		<button class="beat1">Λ</button>
		<button class="beat2"></button>
		<button class="beat3"></button>
	</div>
	<div id="two-four" class="beatSection" style="display: none">
		<button class="beat1">Λ</button>
		<button class="beat2"></button>
	</div>
	<div id="one-four" class="beatSection" style="display: none">
		<button class="beat1">Λ</button>
	</div>
	<div id="three-eight" class="beatSection" style="display: none">
		<button class="beat1">Λ</button>
		<button class="beat2"></button>
		<button class="beat3"></button>
	</div>
	<div id="six-eight" class="beatSection" style="display: none">
		<button class="beat1">Λ</button>
		<button class="beat2"></button>
		<button class="beat3"></button>
		<button class="beat4">Λ</button>
		<button class="beat5"></button>
		<button class="beat6"></button>
	</div>




</div>


<div class="upload-area" id="downloadArea">
	<p class="title">음원을 이곳에 드래그하여 업로드하세요.</p>
</div>

<audio controls id="audioPlayer" style="display: none">
	<source id="downloadSource" src="" type="audio/mpeg" />
	브라우저가 오디오 태그를 지원하지 않습니다.
</audio>