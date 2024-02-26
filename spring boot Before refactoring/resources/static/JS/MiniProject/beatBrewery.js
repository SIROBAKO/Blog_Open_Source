let audioCtx = null

window.onload = function() {
	// 파일 업로드 부분
	const uploadArea = document.getElementById('uploadArea') // uploadArea 변수에 할당

	uploadArea.addEventListener('dragover', (e) => {
		e.preventDefault() // 기본 동작 방지
		uploadArea.style.backgroundColor = '#f0f0f0' // 드래그 중 배경색 변경
	})

	uploadArea.addEventListener('dragleave', () => {
		uploadArea.style.backgroundColor = 'transparent' // 드래그 떠날 때 배경색 초기화
	})

	uploadArea.addEventListener('drop', (e) => {
		e.preventDefault() // 기본 동작 방지
		uploadArea.style.backgroundColor = 'transparent' // 배경색 초기화

		const file = e.dataTransfer.files[0] // 첫 번째 파일만 선택
		handleFile(file) // 파일 처리

		const formData = new FormData() // FormData 생성
		formData.append('audioFile', file) // 파일을 FormData에 추가

		
	})


	uploadArea.addEventListener('click', () => {
		const fileInput = document.createElement('input')

		fileInput.type = 'file'
		fileInput.style.display = 'none'

		fileInput.addEventListener('change', (event) => {
			const File = event.target.files[0]

			handleFile(File)
			// document.body.removeChild(fileInput)

			const formData = new FormData()
			formData.append('audioFile', File) // 'File' 변수에는 실제 오디오 파일이 담겨 있어야 합니다.

			
		})

		document.body.appendChild(fileInput)
		fileInput.click()
	})

	
	function handleFile(file) {
		const allowedTypes = [
			'audio/mpeg',
			'audio/mp3',
			'audio/x-m4a',
			'audio/wav',
		]

		if (allowedTypes.includes(file.type)) {
			const reader = new FileReader()
			reader.onload = function(event) {
				const audioURL = event.target.result
				const audio = new Audio(audioURL)
				audio.controls = true

				uploadArea.innerHTML = '' // 기존 오디오 삭제
				uploadArea.appendChild(audio) // 새 오디오 추가

				// 파일 이름으로 제목 변경
				const title = document.createElement('p')
				title.textContent = file.name
				title.classList.add('title')
				uploadArea.appendChild(title)

				// 긴 제목의 경우 추가 클래스 부여
				if (file.name.length > 20) {
					title.classList.add('long')
				}
			}
			reader.readAsDataURL(file) // 파일을 읽음
		} else {
			console.log('지원되지 않는 파일 형식입니다.')
		}
	}

	// 피치 조절 부분

	const pitchControl = document.getElementById('pitchControl')
	const pitchMonitor = document.getElementById('pitchMonitor')
	const pitchInput = document.getElementById('pitch')

	pitchControl.addEventListener('input', function() {
		if (pitchInput.value !== 'N/A') {
			if (pitchControl.value > 0) {
				pitchMonitor.textContent = '+' + parseInt(pitchControl.value)
			} else {
				pitchMonitor.textContent = parseInt(pitchControl.value)
			}
		} else {
			pitchControl.value = 0
		}
	})

	// bpm 조절부 부분

	const bpmControl = document.getElementById('bpmControl')
	const bpmMonitor = document.getElementById('bpmMonitor')
	const bpmInput = document.getElementById('bpm')

	bpmControl.addEventListener('input', function() {
		if (bpmInput.value !== 'N/A') {
			bpmMonitor.textContent =
				parseInt(bpmInput.value) + parseInt(bpmControl.value)
		} else {
			bpmControl.value = 0
		}
	})

	// 메트로놈 삽입 부분
	const metronomeControl = document.getElementById('metronomeControl')
	const metronomeMonitor = document.getElementById('metronomeMonitor')
	const beatSections = document.querySelectorAll('.beatSection')

	metronomeControl.addEventListener('input', (e) => {
		const value = parseInt(e.target.value)

		// 숫자 변경
		metronomeMonitor.textContent = value ? getBeatIdNum(value) : '0/0'

		// 모든 비트 선택창 숨기기
		beatSections.forEach((section) => {
			section.style.display = 'none'
		})

		// 슬라이드 값에 맞는 비트 선택창 보이기
		const selectedBeat = document.getElementById(getBeatId(value))
		if (selectedBeat) {
			selectedBeat.style.display = 'flex'
		}
	})

	// 슬라이드 값에 따른 비트 ID 반환
	function getBeatId(value) {
		switch (value) {
			case 1:
				return 'one-four'
			case 2:
				return 'two-four'
			case 3:
				return 'three-four'
			case 4:
				return 'four-four'
			case 5:
				return 'three-eight'
			case 6:
				return 'six-eight'
			default:
				return ''
		}
	}

	function getBeatIdNum(value) {
		switch (value) {
			case 1:
				return '1/4'
			case 2:
				return '2/4'
			case 3:
				return '3/4'
			case 4:
				return '4/4'
			case 5:
				return '3/8'
			case 6:
				return '6/8'
			default:
				return '0/0'
		}
	}
}
