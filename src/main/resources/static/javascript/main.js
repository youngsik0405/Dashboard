// 셀 업데이트 인터벌 저장
let cellModalInterval = null;

// cellModal 열기
async function openCellModal(essId, moduleId) {
    const modal = document.getElementById("cellModal");
    const modalBody = document.getElementById("cellModalBody");

    // 모달 표시
    modal.style.display = 'block';

    console.log("essId=", essId, "moduleId=", moduleId);


    async function loadCellData() {
        try {
            // 현재 스크롤 위치 저장
            const scrollContainer = modalBody.querySelector('.cell-modal-scroll');
            const scrollTop = scrollContainer ? scrollContainer.scrollTop : 0


            // 서버에 essId, moduleId 전탈하여 fragment 가져오기
            const response = await axios.get('/api/cellModal', {
                params: {
                    essId: essId,
                    moduleId: moduleId
                }
            });

            console.log("status:", response.status);
            console.log("content-type:", response.headers["content-type"]);
            console.log("data preview:", (response.data || "").toString().substring(0, 200));

            // 서버에서 가져온 데이터를 삽입
            modalBody.innerHTML = response.data;

            // 스크롤 위치 복원
            const newScrollContainer = modalBody.querySelector('.cell-modal-scroll');
            if (newScrollContainer) {
                newScrollContainer.scrollTop = scrollTop;
            }

        } catch (error) {
            console.error('Cell 정보 조회 실패:', error);
        }
    }

    // 초기 데이터 로드
    await loadCellData();

    // 기존 인터벌이 있으면 제거
    if (cellModalInterval) {
        clearInterval(cellModalInterval);
    }

    // 5초마다 셀 데이터 업데이트
    cellModalInterval = setInterval(loadCellData, 5000);
}

// cellModal 닫기
function closeCellModal() {
    const modal = document.getElementById('cellModal');
    const modalBody = document.getElementById('cellModalBody')
    modal.style.display = 'none';

    // 모달 닫을때 인터벌 제거
    if (cellModalInterval) {
        clearInterval(cellModalInterval);
        cellModalInterval = null;
    }

    // 모달 내용 비우기 (스크롤 초기화)
    modalBody.innerHTML = '';
}


// 데이터 갱신
async function updateDashboard() {

    const params = new URLSearchParams(location.search).get("essId");
    const essId = params ? params : 7;

    try {
        const [resStatus, resModules, resEvents] = await Promise.all([
            axios.get("/api/statusFragment", { params: { essId } }),
            axios.get("/api/moduleFragment", { params: { essId } }),
            axios.get("/api/eventHistoryFragment", { params : { essId } })
        ]);

        if (resStatus.data) {
            document.getElementById("statusContainer").innerHTML = resStatus.data;
        }
        if (resModules.data) {
            document.getElementById("moduleFragment").innerHTML = resModules.data;
        }
        if (resEvents.data) {
            document.getElementById("eventContent").innerHTML = resEvents.data;
        }

    } catch (error) {
        console.error('갱신 실패', error);
    }
}

// 초기 실행 및 주기적 업데이트
updateDashboard();
setInterval(updateDashboard, 5000);


// 탭 전환
function switchAlertTab(className) {

    document.querySelectorAll('.event_1, .event_2').forEach(function (element) {
        element.classList.remove('active');
    });

    document.querySelectorAll('.' + className).forEach(function (element) {
        element.classList.add('active');
    });
}




