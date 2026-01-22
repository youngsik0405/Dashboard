// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function () {
    // 주기적 업데이트 (5초마다)
    setInterval(updateDashboard, 5000);
})


// 데이터 갱신
function updateDashboard() {

    const params = new URLSearchParams(location.search).get("essId");
    const essId = params ? params : 7;

    axios.get("/api/updateDashboard", { params: { essId } })
        .then(response => {
            const data = response.data;

            // 받은 데이터로 각 영역의 내용만 업데이트
            updateEventHistory(data.eventHistory);
            updateModule(data.moduleInfo);
            updateRackStatus(data.rackStatusInfo);
            updateFireStatus(data.fireStatusInfo);
        })
        .catch(error => {
            console.error('갱신 실패', error);
        })
}


// 이벤트 히스토리 업데이트
function updateEventHistory(eventHistory) {
    const tbody = document.querySelector('#eventContent tbody');
    if (!tbody) {
        return;
    }

    const rows = tbody.querySelectorAll('tr');

    // eventHitory가 없거나 비어있을 경우
    if (!eventHistory || eventHistory.length === 0) {
        rows.forEach(tr => {
            tr.innerHTML = '<td colspan="3">-</td>';
        });
        return;
    }

    eventHistory.forEach((events, index) => {
        if (rows[index]) {
            const td = rows[index].querySelectorAll('td');

            td[0].textContent = index + 1;
            td[1].textContent = events.eventDt ? formatData(events.eventDt) : '-';
            td[2].textContent = events.eventDesc || '-';
        }
    });

    // 데이터의 개수가 rows보다 적은 경우 남은 rows는 '-' 채움
    for (let i = eventHistory.length; i < rows.length; i++) {
        if (rows[i]) {
            rows[i].innerHTML = '<td colspan="3">-</td>';
        }
    }
}


// 랙 상태 업데이트
function updateRackStatus (rackStatusInfo) {
    const statusDiv = document.querySelector('.status');
    if (!statusDiv) {
        return;
    }

    // .status 안의 모든 div
    const div = statusDiv.querySelectorAll('div');

    // 화면 표시값들을 배열에 넣기
    const values = [
        rackStatusInfo != null && rackStatusInfo.mbmsStatus ? rackStatusInfo.mbmsStatus : '-',
        rackStatusInfo != null && rackStatusInfo.rackSoc != null ? rackStatusInfo.rackSoc + '%' : '-',
        rackStatusInfo != null && rackStatusInfo.rackDcVoltage != null ? rackStatusInfo.rackDcVoltage + 'V' : '-',
        rackStatusInfo != null && rackStatusInfo.rackTemperature != null ? rackStatusInfo.rackTemperature + '˚C' : '-',
        rackStatusInfo != null && rackStatusInfo.rackCurrent != null ? rackStatusInfo.rackCurrent + 'A' : '-'
    ];

    // 각 div 안의 span:last-child 에 값 넣기
    values.forEach((value, index) => {
        if (div[index]) {
            div[index].querySelector('span:last-child').textContent = value;
        }
    })

    // 알람 표시
    if (div[5]) {
        if (rackStatusInfo != null && rackStatusInfo.hasAlarm === true) {
            div[5].querySelector('i').classList.add('alarm-active');
        } else {
            div[5].querySelector('i').classList.remove('alarm-active');
        }
    }
}


// 화재감지 상태 업데이트
function updateFireStatus(fireStatusInfo) {
    const statusDiv = document.querySelector('.status');
    if (!statusDiv) {
        return;
    }

    const div = statusDiv.querySelectorAll('div');

    if (div[6]) {
        if (fireStatusInfo != null && fireStatusInfo.fireStatus === 1) {
            div[6].querySelector('i').classList.add('fire-active');
        } else {
            div[6].querySelector('i').classList.remove('fire-active');
        }
    }

}


// 모듈 데이터 업데이트
function updateModule(moduleInfo) {
    const tbody = document.getElementById("moduleTableBody");
    if (!tbody) {
        return;
    }

    const rows = tbody.querySelectorAll('tr');

    // moduleInfo가 없거나 비어있을 경우 모든 행 '-'
    if (!moduleInfo || moduleInfo.length === 0 ) {
        // 모든 행을 빈 데이터로
        rows.forEach(row => {
            row.innerHTML = '<td colspan="7">-</td>';
        });
        return;
    }

    // 데이터가 있는 행은 업데이트
    moduleInfo.forEach((module, index) => {
        if (rows[index]) {
            const td = rows[index].querySelectorAll('td');

            // 각각 값을 넣기
            td[0].textContent = module.moduleId != null ? module.moduleId : '-';
            td[1].textContent = module.batteryModuleStatus || '-';
            td[2].textContent = module.moduleDcVoltage != null ? module.moduleDcVoltage : '-';
            td[3].textContent = module.maxCellVoltage != null ? module.maxCellVoltage : '-';
            td[4].textContent = module.minCellVoltage != null ? module.minCellVoltage : '-';
            td[5].textContent = module.avgModuleTemperature != null ? module.avgModuleTemperature : '-';

            // 알람 유무로 아이콘 표시하거나 '-' 표시
            if (module.hasAlarm === true) {
                td[6].innerHTML = '<i class="fa-solid fa-triangle-exclamation"></i>';
            } else {
                td[6].innerHTML = '<span>-</span>';
            }
        }
    })

    // 데이터의 개수가 rows보다 적은 경우 남은 rows는 '-' 채움
    for (let i = moduleInfo.length; i < rows.length; i++) {
        if (rows[i]) {
            rows[i].innerHTML = '<td colspan="7">-</td>';
        }
    }
}


// 셀 데이터 로드
function loadCellData(essId, moduleId) {
    axios.get("/api/cellModal",
        {
            params: {
                essId : essId,
                moduleId : moduleId
            }
        }).then(function (response) {
            const tbody = document.getElementById("cellTableBody");
            if (!tbody) {
                return;
            }

            const cellInfo = response.data.cellInfo;

            //tbody의 기존 행을 전부 제거(초기화)
            while (tbody.firstChild) {
                tbody.removeChild(tbody.firstChild);
            }

            // cellInfo가 있으면 행 생성
            if (cellInfo && cellInfo.length > 0) {
                cellInfo.forEach(cell => {
                    const tr = document.createElement("tr");

                    const values = [
                        cell.moduleId != null ? cell.moduleId : '-',
                        cell.cellId != null ? cell.cellId : '-',
                        cell.voltage != null ? cell.voltage : '-'
                    ];

                    // td를 생성해서 값을 넣고 tr에 넣기
                    values.forEach(value => {
                        const td = document.createElement('td');
                        td.textContent = value;
                        tr.appendChild(td);
                    });

                    tbody.appendChild(tr);
                });
            }

            // 최소 6개의 행
            // 데이터가 부족하면 나머지는 '-'로
            while (tbody.children.length < 6) {
                const tr = document.createElement("tr");
                const td = document.createElement("td");
                td.colSpan = 3;
                td.textContent = '-';
                tr.appendChild(td);
                tbody.appendChild(tr);
            }
        }).catch(function (error) {
            console.error("셀 조회 실패", error);
        });

    //if => 모달이 열려있는지 확인 -> 열려있으면 setInterval / 닫혀잇으면 return
    setInterval(function () {
        loadCellData(essId, moduleId)
    }, 5000);
}


// 모달 열기
function openCellModal(essId, moduleId) {
    const modal = document.getElementById("cellModal");

    // 이전 인터벌이 있으면 제거
    // modal.dataset.intervalId -> <div id="cellModal" data-interval-id = "??"></div> 이런식으로 HTML이 변함
    // 모든 값은 문자열로 저장됨 -> Number() 변환 필요
    // 해당 인터벌을 멈추고 후에 새로운 인터벌을 주기 위해서
    // if (modal.dataset.intervalId) {
    //     clearInterval(Number(modal.dataset.intervalId));
    // }
    modal.classList.add("active");

    loadCellData(essId, moduleId);

    // 새 인터벌 생성
    // 모달이 열려있는 동안에만 5초마다 데이터 갱신
    // setInterval(함수, 시간)
    // modal.dataset.intervalId = setInterval(function () {
    //     loadCellData(essId, moduleId)
    // }, 5000);


}


// 모달 닫기
function closeCellModal() {
    document.getElementById("cellModal").classList.remove("active");
}


// 탭 전환
function switchAlertTab(className) {
    // 이미 클릭한 탭이 active면 아무 것도 안함
    if (document.querySelector(`.tab-button.${className}`).classList.contains('active')) {
        return;
    }

    if (className == 'event_1') {
        document.querySelectorAll('.event_2').forEach(function (element) {
           element.classList.remove('active');
        });
        document.querySelectorAll('.event_1').forEach(function (element) {
            element.classList.add('active');
        });
    } else {
        document.querySelectorAll('.event_1').forEach(function (element) {
            element.classList.remove('active');
        });
        document.querySelectorAll('.event_2').forEach(function (element) {
            element.classList.add('active');
        });
    }
}

// 날짜 포맷 변환
function formatData(eventDt) {
    // 서버에서 받은 eventDt를 Date 객체로 변환
    // 서버에서는 문자열로 넘어오기 때문에
   const date = new Date(eventDt);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    // YYYY-MM-DD HH:mm:ss 형태로 변환
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}


