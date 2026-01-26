let previousEventHistory = null; // 이전 데이터와 현재 데이터 비교를 위한 저장 변수

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
    if (JSON.stringify(previousEventHistory) === JSON.stringify(eventHistory)) {
        console.log('이벤트 데이터 동일 - 갱신 안함');
        return;
    }

    console.log('새로운 이벤트 데이터 - 갱신 시작');

    const tbody = document.querySelector('#eventContent tbody');

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
            const count = rows[index].querySelector('.count');
            const eventHistoryDt = rows[index].querySelector('.event-dt');
            const eventHistoryDesc = rows[index].querySelector('.event-desc');

            if (count) {
                count.textContent = index + 1;
            }
            if (eventHistoryDt) {
                eventHistoryDt.textContent = events.eventDt ? formatData(events.eventDt) : '-';
            }
            if (eventHistoryDesc) {
                eventHistoryDesc.textContent = events.eventDesc || '-';
            }
        }
    });

    // 데이터의 개수가 rows보다 적은 경우 남은 rows는 '-' 채움
    for (let i = eventHistory.length; i < rows.length; i++) {
        if (rows[i]) {
            rows[i].innerHTML = '<td colspan="3">-</td>';
        }
    }

    previousEventHistory = eventHistory;
}


// 랙 상태 업데이트
function updateRackStatus (rackStatusInfo) {
    const status = document.querySelector('.mbms-status');
    const soc = document.querySelector('.rack-soc');
    const voltage = document.querySelector('.rack-dc-voltage');
    const temperature = document.querySelector('.rack-temperature');
    const current = document.querySelector('.rack-current');
    const alarmIcon = document.querySelector('.fa-triangle-exclamation');

    if (rackStatusInfo != null) {
        if (status) {
            status.textContent = rackStatusInfo.mbmsStatus || '-';
        }
        if (soc) {
            soc.textContent = rackStatusInfo.rackSoc != null ? rackStatusInfo.rackSoc + '%' : '-';
        }
        if (voltage) {
            voltage.textContent = rackStatusInfo.rackDcVoltage != null ? rackStatusInfo.rackDcVoltage + 'V' : '-';
        }
        if (temperature) {
            temperature.textContent = rackStatusInfo.rackTemperature != null ? rackStatusInfo.rackTemperature + '˚C' : '-';
        }
        if (current) {
            current.textContent = rackStatusInfo.rackCurrent != null ? rackStatusInfo.rackCurrent + 'A' : '-';
        }
    } else {
        status.textContent = '-';
        soc.textContent = '-';
        voltage.textContent = '-';
        temperature.textContent = '-';
        current.textContent = '-';
    }

    if (alarmIcon) {
        if (rackStatusInfo != null && rackStatusInfo.hasAlarm === true) {
            alarmIcon.classList.add('alarm-active');
        } else {
            alarmIcon.classList.remove('alarm-active');
        }
    }

}


// 화재감지 상태 업데이트
function updateFireStatus(fireStatusInfo) {
    const fire = document.querySelector('.fa-fire');

    if (fire) {
        if (fireStatusInfo != null && fireStatusInfo.fireStatus === 1) {
            fire.classList.add('fire-active');
        } else {
            fire.classList.remove('fire-active');
        }
    }
}


// 모듈 데이터 업데이트
function updateModule(moduleInfo) {
    const tbody = document.getElementById("moduleTableBody");

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
            const moduleIdTd = rows[index].querySelector('.module_id');
            const statusTd = rows[index].querySelector('.battery_module_status');
            const moduleDcVoltageTd = rows[index].querySelector('.module_dc_voltage');
            const maxCellVoltageTd = rows[index].querySelector('.max_cell_voltage');
            const minCellVoltageTd = rows[index].querySelector('.min_cell_voltage');
            const avgModuleTemperatureTd = rows[index].querySelector('.avg_module_temperature');
            const alarmIcon = rows[index].querySelector('.alarm_icon');

            // 각각 값을 넣기
            if (moduleIdTd) {
                moduleIdTd.textContent = module.moduleId != null ? module.moduleId : '-';
            }
            if (statusTd) {
                statusTd.textContent = module.batteryModuleStatus || '-';
            }
            if (moduleDcVoltageTd) {
                moduleDcVoltageTd.textContent = module.moduleDcVoltage != null ? module.moduleDcVoltage : '-';
            }
            if (maxCellVoltageTd) {
                maxCellVoltageTd.textContent = module.maxCellVoltage != null ? module.maxCellVoltage : '-';
            }
            if (minCellVoltageTd) {
                minCellVoltageTd.textContent = module.minCellVoltage != null ? module.minCellVoltage : '-';
            }
            if (avgModuleTemperatureTd) {
                avgModuleTemperatureTd.textContent = module.avgModuleTemperature != null ? module.avgModuleTemperature : '-';
            }

            // 알람 유무로 아이콘 표시하거나 '-' 표시
            if (alarmIcon) {
                if (module.hasAlarm === true) {
                    alarmIcon.innerHTML = '<i class="fa-solid fa-triangle-exclamation"></i>';
                } else {
                    alarmIcon.innerHTML = '<span>-</span>';
                }
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
}


// 모달 열기
function openCellModal(essId, moduleId) {
    const modal = document.getElementById("cellModal");

    modal.classList.add('active');

    loadCellData(essId, moduleId);

    // 새 인터벌 생성
    // 모달이 열려있는 동안에만 5초마다 데이터 갱신
    interval = setInterval(function () {
        loadCellData(essId, moduleId)
    }, 5000);
}


// 모달 닫기
function closeCellModal() {
    document.getElementById("cellModal").classList.remove("active");

    if (interval) {
        clearInterval(interval);
    }
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
   const offset = 1000 * 60 * 60 * 9;
   const korDate = new Date(date.getTime() + offset);

   const dateFormat = korDate.toISOString().replace('T', ' ').replace('Z', '').split('.')[0];

   // console.log(dateFormat);

   return dateFormat;
}


function showDetail(row) {
    const popover = document.getElementById("popover");
    let eventHistoryDt;
    let eventHistoryDesc;

    if (row.querySelector('.event-dt')) {
        eventHistoryDt = row.querySelector('.event-dt').textContent;
    } else {
        eventHistoryDt = '-';
    }

    if (row.querySelector('.event-desc')) {
        eventHistoryDesc = row.querySelector('.event-desc').textContent;
    } else {
        eventHistoryDesc = '-';
    }

    document.getElementById('eventDt').textContent = eventHistoryDt;
    document.getElementById('eventDesc').textContent = eventHistoryDesc;

    const rect = row.getBoundingClientRect();

    popover.style.top = `${rect.top - (rect.height/2)}px`;
    popover.style.left = `${rect.right + 10}px`;

    popover.showPopover();
}


Highcharts.setOptions({
    time: {
        useUTC: false
    }
});

Highcharts.chart('chart', {
    chart: {
        type: 'line',
        zoomType: 'xy'
    },
    title: {
        text: 'Rack 그래프',
        margin: 30,
        style: {
            fontSize: '24px',
        }
    },
    subtitle: {
        text: Highcharts.dateFormat('%Y-%m-%d', Date.now()),
        align: 'right',
        style: {
            fontSize: '18px'
        },
        y: 20
    },

    xAxis: {
        type: 'datetime',
        min: Date.now() - (6 * 60 * 60 * 1000),
        max: Date.now(),
        tickInterval: 30 * 60 * 1000,
        labels: {
            format: '{value:%H:%M}'
        }
    },
    yAxis: [{
        title: {
            text: ''
        },
        labels: {
            style: {
                color: '#2caffe'
            },
            format: '{value} V'
        },
        tickInterval: 10,
        tickAmount: 4,
        min: 0
    }, {
        title: {
            text: ''
        },
        labels: {
            style: {
                color: '#544fc5'
            },
            format: '{value} A'
        },
        opposite: true,
        tickInterval: 15,
        tickAmount: 4,
        min: 0
    }, {
        title: {
            text: ''
        },
        labels: {
            style: {
                color: '#5BD75B'
            },
            format: '{value} ˚C'
        },
        opposite: true,
        tickInterval: 15,
        tickAmount: 4,
        min: 0
    }],
    tooltip: {
        xDateFormat:'%H:%M:%S',
        pointFormat: '{point.y}'
    },
    series: [{
        name: '전압(V)',
        data: [
            [Date.now() - (6 * 60 * 60 * 1000), 10],
            [Date.now() - (5.5 * 60 * 60 * 1000), 15],
            [Date.now() - (5 * 60 * 60 * 1000), 8],
            [Date.now() - (4.5 * 60 * 60 * 1000), 20],
            [Date.now() - (4 * 60 * 60 * 1000), 30],
            [Date.now() - (3.5 * 60 * 60 * 1000), 45],
            [Date.now() - (3 * 60 * 60 * 1000), 50],
            [Date.now() - (2.5 * 60 * 60 * 1000), 55],
            [Date.now() - (2 * 60 * 60 * 1000), 51],
            [Date.now() - (1.5 * 60 * 60 * 1000), 52],
            [Date.now() - (1 * 60 * 60 * 1000), 33],
            [Date.now() - (0.5 * 60 * 60 * 1000), 32],
            [Date.now(), 33]],
        yAxis: 0
    }, {
        name: '전류(A)',
        data: [
            [Date.now() - (6 * 60 * 60 * 1000), 20],
            [Date.now() - (5.5 * 60 * 60 * 1000), 15],
            [Date.now() - (5 * 60 * 60 * 1000), 11],
            [Date.now() - (4.5 * 60 * 60 * 1000), 20],
            [Date.now() - (4 * 60 * 60 * 1000), 40],
            [Date.now() - (3.5 * 60 * 60 * 1000), 45],
            [Date.now() - (3 * 60 * 60 * 1000), 20],
            [Date.now() - (2.5 * 60 * 60 * 1000), 15],
            [Date.now() - (2 * 60 * 60 * 1000), 11],
            [Date.now() - (1.5 * 60 * 60 * 1000), 12],
            [Date.now() - (1 * 60 * 60 * 1000), 23],
            [Date.now() - (0.5 * 60 * 60 * 1000), 32],
            [Date.now(), 31]],
        yAxis: 1
    }, {
        name: '온도(˚C)',
        data: [
            [Date.now() - (6 * 60 * 60 * 1000), 11],
            [Date.now() - (5.5 * 60 * 60 * 1000), 12],
            [Date.now() - (5 * 60 * 60 * 1000), 13],
            [Date.now() - (4.5 * 60 * 60 * 1000), 14],
            [Date.now() - (4 * 60 * 60 * 1000), 15],
            [Date.now() - (3.5 * 60 * 60 * 1000), 16],
            [Date.now() - (3 * 60 * 60 * 1000), 17],
            [Date.now() - (2.5 * 60 * 60 * 1000), 18],
            [Date.now() - (2 * 60 * 60 * 1000), 19],
            [Date.now() - (1.5 * 60 * 60 * 1000), 20],
            [Date.now() - (1 * 60 * 60 * 1000), 21],
            [Date.now() - (0.5 * 60 * 60 * 1000), 22],
            [Date.now(), 23]],
        yAxis: 2
    }],
    credits: {
        enabled: false
    }
});

function chart(essId) {
    axios.get("/api/chart", {
        params: { essId }
    }).then(response => {
        const data = response.data;

        const categories = data.map(d => d.createdAt);
    })
}




