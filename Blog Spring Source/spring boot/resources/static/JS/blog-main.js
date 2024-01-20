// 초기 실행
document.addEventListener('DOMContentLoaded', () => {
    setFixedElementHeight()

    if (getCookie('color-theme') == null) {
        setCookie('color-theme', 'light', 3)
    } else {
        setCookie('color-theme', getCookie('color-theme'), 3)
    }

    var cookiesEnabled = navigator.cookieEnabled

    if (getCookie('color-theme') == 'light' || !cookiesEnabled) {
        changeLight()
    } else {
        changeDark()
    }
})

function scrollIndicator() {
    const windowHeight = window.innerHeight
    const documentHeight = document.documentElement.scrollHeight
    const scrollableDistance = documentHeight - windowHeight
    const scrolled = (window.pageYOffset / scrollableDistance) * 100
    const scrollIndicator = document.getElementById('scrollIndicator')
    scrollIndicator.style.width = scrolled + '%'
}

function DarkModeChange() {
    mode = document.documentElement.getAttribute('color-theme')

    if (mode == 'dark') {
        setCookie('color-theme', 'light', 3)

        changeLight()
    } else {
        setCookie('color-theme', 'dark', 3)

        changeDark()
    }
}

function changeDark() {
    document.documentElement.setAttribute('color-theme', 'dark')
    // DOM 요소 참조를 가져오는 코드

    var logo = document.getElementById('logo')
    var modeConsoleLabel = document.querySelector('label[for="Mode-Console"]')
    var modeConsoleIcon = document.getElementById('Mode-Console')
    var mobileLogo = document.getElementById('mobile-logo')
    var mobileModeConsoleLabel = document.querySelector(
        'label[for="mobile-Mode-Console"]'
    )
    var mobileModeConsoleIcon = document.getElementById('mobile-Mode-Console')

    // DOM 요소 수정 코드
    logo.src = '/image/logo/HAK-logo-white.png'
    modeConsoleLabel.innerHTML = 'Light'
    modeConsoleIcon.className = 'icon-sun'
    mobileLogo.src = '/image/logo/HAK-logo-white.png'
    mobileModeConsoleLabel.innerHTML = 'Light'
    mobileModeConsoleIcon.className = 'icon-sun'

    // 'detail' 페이지인 경우 코드 스타일 변경
    if (window.location.href.includes('detail')) {
        var codeDarkElements = document.querySelectorAll('.code_dark')
        var codeLightElements = document.querySelectorAll('.code_light')

        codeDarkElements.forEach(function (dark) {
            dark.style.display = 'block'
        })

        codeLightElements.forEach(function (light) {
            light.style.display = 'none'
        })
    }
}

function changeLight() {
    document.documentElement.setAttribute('color-theme', 'light')
    // DOM 요소 참조를 가져오는 코드

    var logo = document.getElementById('logo')
    var modeConsoleLabel = document.querySelector('label[for="Mode-Console"]')
    var modeConsoleIcon = document.getElementById('Mode-Console')
    var mobileLogo = document.getElementById('mobile-logo')
    var mobileModeConsoleLabel = document.querySelector(
        'label[for="mobile-Mode-Console"]'
    )
    var mobileModeConsoleIcon = document.getElementById('mobile-Mode-Console')

    // DOM 요소 수정 코드
    logo.src = '/image/logo/HAK-logo.png'
    modeConsoleLabel.innerHTML = 'Dark'
    modeConsoleIcon.className = 'icon-moon'
    mobileLogo.src = '/image/logo/HAK-logo.png'
    mobileModeConsoleLabel.innerHTML = 'Dark'
    mobileModeConsoleIcon.className = 'icon-moon'

    // 'detail' 페이지인 경우 코드 스타일 변경
    if (window.location.href.includes('detail')) {
        var codeDarkElements = document.querySelectorAll('.code_dark')
        var codeLightElements = document.querySelectorAll('.code_light')

        codeDarkElements.forEach(function (dark) {
            dark.style.display = 'none'
        })

        codeLightElements.forEach(function (light) {
            light.style.display = 'block'
        })
    }
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
    window.scroll({
        top: 0,
        left: 0,
        behavior: 'smooth',
    })
}

function showInfo() {
    check = document.getElementById('blog-info').style.display

    if (check == 'block') {
        closeInfo()
    } else {
        window.removeEventListener('scroll', closeInfo)
        document.getElementById('blog-info').style.display = 'block'
    }
    window.scroll({
        top: document.body.scrollHeight,
        left: 0,
        behavior: 'smooth',
    })

    setTimeout(function () {
        window.addEventListener('scroll', closeInfo)
    }, 1000)
}

function closeInfo() {
    document.getElementById('blog-info').style.display = 'none'

    setFixedElementHeight()
}

function setCookie(cookie_name, value, days) {
    var exdate = new Date()
    exdate.setDate(exdate.getDate() + days)
    // 설정 일수만큼 현재시간에 만료값으로 지정

    var cookie_value =
        escape(value) +
        (days == null ? '' : '; expires=' + exdate.toUTCString())
    document.cookie = cookie_name + '=' + cookie_value + '; path=/'
}

function getCookie(cookie_name) {
    var x, y
    var val = document.cookie.split(';')

    for (var i = 0; i < val.length; i++) {
        x = val[i].substr(0, val[i].indexOf('='))
        y = val[i].substr(val[i].indexOf('=') + 1)
        x = x.replace(/^\s+|\s+$/g, '') // 앞과 뒤의 공백 제거하기
        if (x == cookie_name) {
            return unescape(y) // unescape로 디코딩 후 값 리턴
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
        .then((res) => res.text())
        .then((text) => {
            text = JSON.parse(text)

            if (parseInt(text.result) == 0) {
                alert('더 이상 게시물이 존재하지 않습니다.')
            } else if (parseInt(text.result) == 1) {
                for (i = 0; i < text.list.length; i++) {
                    input_console = '<li>'
                    input_console +=
                        '<a href="/detail/' + text.list[i].num + '">'
                    input_console +=
                        '<img src="/upload_image/image/fileupload/' +
                        text.list[i].num +
                        '/thumbnail/thumbnail.PNG" onerror="this.src=\'/image/logo/black.png\'">'
                    input_console += '<h3>' + text.list[i].title + '</h3></a>'
                    input_console += '<p>' + text.list[i].category + '</p></li>'
                    document
                        .getElementById('index-list')
                        .insertAdjacentHTML('beforeend', input_console)
                }
            }
        })
}

function moveFooter() {
    var footer = document.querySelector('footer')
    var footerHeight = footer.offsetHeight
    var scrollPosition =
        window.pageYOffset ||
        document.documentElement.scrollTop ||
        document.body.scrollTop
    var windowHeight = window.innerHeight
    var documentHeight = Math.max(
        document.body.scrollHeight,
        document.documentElement.scrollHeight,
        document.body.offsetHeight,
        document.documentElement.offsetHeight,
        document.body.clientHeight,
        document.documentElement.clientHeight
    )

    var fixedElements = document.querySelectorAll('.fixed')
    var header = document.querySelector('header')
    var headerHeight = header.offsetHeight

    if (scrollPosition + windowHeight >= documentHeight - footerHeight) {
        var totalHeight =
            windowHeight - headerHeight - windowHeight * 0.1 - footerHeight

        footer.style.transition = 'transform 0.3s ease-in-out'
        footer.style.transform = 'translateY(0)'
    } else {
        var totalHeight = windowHeight - headerHeight - windowHeight * 0.1

        footer.style.transition = 'transform 0.3s ease-in-out'
        footer.style.transform = 'translateY(' + footerHeight + 'px)'
    }

    fixedElements.forEach(function (element) {
        element.style.height = totalHeight + 'px'
    })
}

window.addEventListener('scroll', moveFooter)

function setFixedElementHeight() {
    var fixedElements = document.querySelectorAll('.fixed')
    var header = document.querySelector('header')
    var windowHeight = window.innerHeight
    var headerHeight = header.offsetHeight

    var totalHeight = windowHeight - headerHeight - windowHeight * 0.1

    fixedElements.forEach(function (element) {
        element.style.height = totalHeight + 'px'
    })
}

// 화면 크기 변환 시 실행
window.addEventListener('resize', setFixedElementHeight)

// 게시글 공유
function shareLink() {
    var url = window.location.href
    var shareUrl

    if (navigator.share) {
        // 웹 공유 API를 지원하는 경우
        shareUrl = url
        navigator
            .share({
                url: shareUrl,
            })
            .then(() => console.log('링크 공유 성공'))
            .catch((error) => console.error('링크 공유 실패', error))
    } else {
        // 지원하지 않는 경우
        shareUrl = url
        var tempInput = document.createElement('input')
        tempInput.setAttribute('value', shareUrl)
        document.body.appendChild(tempInput)
        tempInput.select()
        document.execCommand('copy')
        document.body.removeChild(tempInput)
        alert('링크가 복사되었습니다.')
    }
}
