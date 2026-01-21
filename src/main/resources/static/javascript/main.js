// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function () {
    // 주기적 업데이트
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
            // updateEventHistory(data.eventHistory);
            updateModule(data.moduleInfo);
            updateRackStatus(data.rackStatusInfo);
            updateFireStatus(data.fireStatusInfo);
        })
        .catch(error => {
            console.error('갱신 실패', error);
        })
}

function updateEventHistory(eventHistory) {
    const tbody = document.querySelector('#eventContent tbody');
    if (!tbody) {
        return;
    }

    const rows = tbody.querySelectorAll('tr');

    if (!eventHistory || eventHistory.length === 0) {
        rows.forEach(row => {
            row.innerHTML = '<td colspan="3">-</td>';
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

    for (let i = events.length; i < rows.length; i++) {
        if (rows[i]) {
            rows[i].innerHTML = '<td colspan="3">-</td>';
        }
    }
}

function updateRackStatus (rackStatusInfo) {
    const statusDiv = document.querySelector('.status');
    if (!statusDiv) {
        return;
    }

    const div = statusDiv.querySelectorAll('div');

    const values = [
        rackStatusInfo != null && rackStatusInfo.mbmsStatus ? rackStatusInfo.mbmsStatus : '-',
        rackStatusInfo != null && rackStatusInfo.rackSoc != null ? rackStatusInfo.rackSoc + '%' : '-',
        rackStatusInfo != null && rackStatusInfo.rackDcVoltage != null ? rackStatusInfo.rackDcVoltage + 'V' : '-',
        rackStatusInfo != null && rackStatusInfo.rackTemperature != null ? rackStatusInfo.rackTemperature + '˚C' : '-',
        rackStatusInfo != null && rackStatusInfo.rackCurrent != null ? rackStatusInfo.rackCurrent + 'A' : '-'
    ];

    values.forEach((value, index) => {
        if (div[index]) {
            div[index].querySelector('span:last-child').textContent = value;
        }
    })

    if (div[5]) {
        if (rackStatusInfo != null && rackStatusInfo.hasAlarm === true) {
            div[5].querySelector('i').classList.add('alarm-active');
        } else {
            div[5].querySelector('i').classList.remove('alarm-active');
        }
    }
}

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


function updateModule(moduleInfo) {
    const tbody = document.getElementById("moduleTableBody");
    if (!tbody) {
        return;
    }

    const rows = tbody.querySelectorAll('tr');

    if (!moduleInfo || moduleInfo.length === 0 ) {
        // 모든 행을 빈 데이터로
        rows.forEach(row => {
            row.innerHTML = '<td colspan="7">-</td>';
            row.onclick = null;
        });
        return;
    }

    // 데이터가 있는 행은 업데이트
    moduleInfo.forEach((module, index) => {
        if (rows[index]) {
            const td = rows[index].querySelectorAll('td');

            td[0].textContent = module.moduleId != null ? module.moduleId : '-';
            td[1].textContent = module.batteryModuleStatus || '-';
            td[2].textContent = module.moduleDcVoltage != null ? module.moduleDcVoltage : '-';
            td[3].textContent = module.maxCellVoltage != null ? module.maxCellVoltage : '-';
            td[4].textContent = module.minCellVoltage != null ? module.minCellVoltage : '-';
            td[5].textContent = module.avgModuleTemperature != null ? module.avgModuleTemperature : '-';

            if (module.hasAlarm === true) {
                td[6].innerHTML = '<i class="fa-solid fa-triangle-exclamation"></i>';
            } else {
                td[6].innerHTML = '<span>-</span>';
            }
        }
    })

}

function loadCellData(essId, moduleId) {
    const modal = document.getElementById("cellModal");
    if (!modal.classList.contains("active")) {
        return;
    }

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

            const rows = Math.max(6, response.data.cellInfo.length);

            tbody.innerHTML = "";

            for (let i = 0; i < rows; i++) {
                const tr = document.createElement("tr");

                if (i < response.data.cellInfo.length) {
                    tr.innerHTML = `
                        <td>${response.data.cellInfo[i].moduleId != null ? response.data.cellInfo[i].moduleId : '-'}</td>
                        <td>${response.data.cellInfo[i].cellId != null ? response.data.cellInfo[i].cellId : '-'}</td>
                        <td>${response.data.cellInfo[i].voltage != null ? response.data.cellInfo[i].voltage : '-'}</td>
                      `;
                } else {
                    tr.innerHTML = `<td colspan="3">-</td>`;
                }

                tbody.appendChild(tr);
            }
        }).catch(function (error) {
            console.error("셀 조회 실패", error);
        });

}

function openCellModal(essId, moduleId) {
    const modal = document.getElementById("cellModal");
    modal.classList.add("active");

    loadCellData(essId, moduleId);

    setInterval(function () {
        loadCellData(essId, moduleId)
    }, 5000);
}


function closeCellModal() {
    document.getElementById("cellModal").classList.remove("active");
    document.getElementById("cellTableBody").innerHTML = "";
}


// // 셀 업데이트 인터벌 저장
// let cellModalInterval = null;
//
// // cellModal 열기
// function openCellModal(essId, moduleId) {
//
//     const modal = document.getElementById("cellModal");
//     const modalBody = document.getElementById("cellModalBody");
//
//     function loadCellData() {
//         try {
//             // 현재 스크롤 위치 저장
//             const scrollContainer = document.querySelector('.cell-modal-scroll');
//             const scrollTop = scrollContainer ? scrollContainer.scrollTop : 0;
//
//             // 서버에 essId, moduleId 전탈하여 fragment 가져오기
//             axios.get('/api/cellModal', { params: { essId: essId, moduleId: moduleId } })
//                 .then(response => {
//                     const data = response.data;
//
//                     updateCellModal(data.cellInfo);
//
//                     modal.style.display = 'block';
//                     // class -> active : actvie 일 때 display block
//
//                     // 새로 생성된 .cell-modal-scroll 다시 찾음
//                     // 스크롤 위치를 다시 저장
//                     document.querySelector('.cell-modal-scroll').scrollTop = scrollTop;
//                 });
//         } catch (error) {
//             console.error('Cell 정보 조회 실패:', error);
//         }
//     }
//
//     // 초기 데이터 로드
//     loadCellData();
//
//     // 기존 인터벌이 있으면 제거
//     if (cellModalInterval) {
//         clearInterval(cellModalInterval);
//     }
//
//     // 5초마다 셀 데이터 업데이트
//     cellModalInterval = setInterval(loadCellData, 5000);
// }
//
// // cellModal 닫기
// function closeCellModal() {
//     const modal = document.getElementById('cellModal');
//     const modalBody = document.getElementById('cellModalBody');
//     modal.style.display = 'none';
//
//     // 모달 닫을때 인터벌 제거
//     if (cellModalInterval) {
//         clearInterval(cellModalInterval);
//         cellModalInterval = null;
//     }
//
//     // 모달 내용 비우기 (스크롤 초기화 위해서)
//     modalBody.innerHTML = '';
// }
//
// function updateCellModal() {
//     const tbody = document.getElementById("cellModalBody tbody");
//     if (!tbody) {
//         return;
//     }
// }

// 탭 전환
function switchAlertTab(className) {

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

function formatData(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}


