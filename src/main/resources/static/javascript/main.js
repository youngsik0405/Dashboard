function switchAlertTab(tabName, clickedButton) {
    // 모든 버튼 비활성화
    document.querySelectorAll('.tab-button').forEach(function (button) {
        button.classList.remove('active');
    });
    // 선택한 버튼 활성화
    clickedButton.classList.add('active');

    // 모든 탭내용 비활성화
    document.querySelectorAll('.tab-content').forEach(function (content) {
        content.classList.remove('active');
    });

    // 선택한 탭 내용 활성화
    document.getElementById(tabName + 'Content').classList.add('active');
}
