// cellModal 열기
async function openCellModal(essId, moduleId) {
    const modal = document.getElementById("cellModal");
    const modalBody = document.getElementById("cellModalBody");

    // 모달 표시
    modal.style.display = 'block';

    console.log("essId=", essId, "moduleId=", moduleId);

    try {
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

    } catch (error) {
        console.error('Cell 정보 조회 실패:', error);
    }
}

// cellModal 닫기
function closeCellModal() {
    const modal = document.getElementById('cellModal');
    modal.style.display = 'none';
}

//
// function switchAlertTab(tabName, clickedButton) {
//     // 모든 버튼 비활성화
//     document.querySelectorAll('.tab-button').forEach(function (button) {
//         button.classList.remove('active');
//     });
//     // 선택한 버튼 활성화
//     clickedButton.classList.add('active');
//
//     // 모든 탭내용 비활성화
//     document.querySelectorAll('.tab-content').forEach(function (content) {
//         content.classList.remove('active');
//     });
//
//     // 선택한 탭 내용 활성화
//     document.getElementById(tabName + 'Content').classList.add('active');
// }


function switchAlertTab(className, clickedButton) {
//    ㅋㅡㄹ릭한 버튼이 event_1이면
    document.querySelectorAll(className).forEach(function (content) {
        content.classList.remove('active');
        content.classList.remove('active');
    });

    document.querySelectorAll(className).forEach(function (button) {
        button.classList.remove('active');
        button.classList.remove('active');
    });



}
