let previousEventHistory = null; // 이전 데이터와 현재 데이터 비교를 위한 저장 변수

// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function () {
    // URL 쿼리 파라미터에서 essId와 rackDeviceId 추출
    const params = new URLSearchParams(location.search);
    const essId = params.get("essId") ? params.get("essId") : 7;
    const rackDeviceId = params.get("rackDeviceId") ? params.get("rackDeviceId") : 1;

    // 초기 차트 그리기
    loadChart(essId, rackDeviceId);

    // 대시보드 갱신
    updateDashboard();

    // 차트 갱신
    lastRackStatusPoint(essId, rackDeviceId);
})

// 데이터 갱신
function updateDashboard() {
    const params = new URLSearchParams(location.search).get("essId");
    const essId = params ? params : 7;

    axios.get("/api/updateDashboard", {
        params: {
            essId : essId
        } })
        .then(response => {
            const data = response.data;

            // 받은 데이터로 각 영역의 내용만 업데이트
            updateEventHistory(data.eventHistory);
            updateModule(data.moduleInfo);
            updateRackStatus(data.rackStatusInfo);
            updateFireStatus(data.fireStatusInfo);

            setTimeout(updateDashboard, 1000);

        })
        .catch(error => {
            console.error('갱신 실패', error);
        })
}

// 이벤트 히스토리 업데이트
function updateEventHistory(eventHistory) {
    // 데이터가 이전과 동일하면 리턴
    if (JSON.stringify(previousEventHistory) === JSON.stringify(eventHistory)) {
        // console.log('이벤트 데이터 동일 - 갱신 안함');
        return;
    }
    // console.log('새로운 이벤트 데이터 - 갱신 시작');

    const tbody = document.querySelector('#eventContent tbody');
    const rows = tbody.querySelectorAll('tr');

    // 데이터가 없는 경우 테이블 (- 표시)
    if (!eventHistory || eventHistory.length === 0) {
        rows.forEach(tr => {
            tr.innerHTML = '<td colspan="3">-</td>';
            tr.classList.remove('has-data');
            tr.onclick = null; // 클릭 이벤트 제거
        });
        return;
    }

    // 데이터 채우기
    eventHistory.forEach((events, index) => {
        if (rows[index]) {
            const count = rows[index].querySelector('.count');
            const eventHistoryDt = rows[index].querySelector('.event-dt');
            const eventHistoryDesc = rows[index].querySelector('.event-desc');

            // 데이터 유무에 따른 행 효과 활성화/비활성화
            if (events) {
                rows[index].classList.add('has-data');
            } else {
                rows[index].classList.remove('has-data');
                rows[index].onclick = null;
            }

            if (count) {
                count.textContent = index + 1;
            }
            if (eventHistoryDt) {
                eventHistoryDt.textContent = events.eventDt ? formatDate(events.eventDt) : '-';
            }
            if (eventHistoryDesc) {
                eventHistoryDesc.textContent = events.eventDesc || '-';
            }

            // 상세 보기를 위한 데이터셋 저장
            rows[index].dataset.eventId = events.id || '';
            rows[index].dataset.eventType = events.eventType || '';
            rows[index].dataset.eventDt = events.eventDt || '';
        }
    });

    // 데이터의 개수가 rows보다 적은 경우 남은 rows는 '-' 채움
    for (let i = eventHistory.length; i < rows.length; i++) {
        if (rows[i]) {
            rows[i].innerHTML = '<td colspan="3">-</td>';
            rows[i].classList.remove('has-data');
            rows[i].onclick = null;
        }
    }

    // 현재 데이터를 이전데이터로 저장
    previousEventHistory = eventHistory;
}


// 이벤트 상세
function showDetail(row, eventId) {
    const popover = document.getElementById("popover");

    // 팝오버 위치 계산
    const rect = row.getBoundingClientRect(); // 클릭 행의 뷰포트기준 위치정보
    const offsetTop = rect.top + window.scrollY; // 문서 전체기준 상단 위치 (+ 스크롤한 양)
    const offsetLeft = rect.left + window.scrollX;
    const offsetBottom = offsetTop + row.offsetHeight;
    const offsetBottomTo = document.body.scrollHeight - offsetBottom + 20;

    popover.style.left = (offsetLeft + 590) + "px";
    popover.style.bottom = offsetBottomTo + "px";
    popover.style.top = `auto`;

    const eventType = row.dataset.eventType;
    const eventDt = row.dataset.eventDt;

    // 이벤트 타입 변환
    let eventTypeText = "";

    switch (eventType) {
        case "WARNING" :
            eventTypeText = "배터리 주의";
            break;
        case "FAULT" :
            eventTypeText = "배터리 경보";
            break;
        case "MODE" :
            eventTypeText = "운전 모드";
            break;
        case "COMM ERROR" :
            eventTypeText = "통신 오류";
            break;
        case "FIRE SIGNAL" :
            eventTypeText = "화재";
            break;
        default :
            eventTypeText = "기타";
            break;
    }

    // 팝오버 내용 채우기
    document.getElementById('eventType').textContent = eventTypeText;

    if (eventDt) {
        document.getElementById('eventDt').textContent = formatDate(eventDt);
    } else {
        document.getElementById('eventDt').textContent = '알수없음';
    }

    axios.get("/api/eventDetail", {
        params: {
            eventId: eventId
        }
    }).then(response => {

        const eventDetail = response.data.eventDetail || '없음';

        if (eventDetail.includes('/')) {
            document.getElementById('eventDetail').textContent = eventDetail.split('/').join('\n');
        } else {
            document.getElementById('eventDetail').textContent = eventDetail;
        }

        openPopover();

    }).catch(error => {
        console.error('이벤트 상세 조회 실패', error);
    });
}

// 팝오버 열기
function openPopover() {
    document.getElementById("popover").style.display = 'block';

}

// 팝오버 닫기
function closePopover() {
    document.getElementById("popover").style.display = 'none';
}


// 팝오버 이외 영역 클릭시 닫기
document.addEventListener('click', function(e) {
    const popover = document.getElementById("popover");

    if (popover && popover.style.display === 'block') {
        // 팝오버 자체를 클릭했거나, 테이블 행을 클릭한 경우는 제외
        const clickedRow = e.target.closest('.event_1 tbody tr');
        const isTableRow = clickedRow && clickedRow.hasAttribute('onclick');

        if (!popover.contains(e.target) && !isTableRow) {
            closePopover();
        }
    }
});



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
            status.textContent = rackStatusInfo.mbmsStatus || '-'; // 빈 문자열도 '-' 로 처리
        }
        if (soc) {
            soc.textContent = rackStatusInfo.rackSoc != null ? rackStatusInfo.rackSoc + '%' : '-';
        }
        // 임계값에 따른 색상 변화
        if (voltage) {
            const voltageDiv = voltage.closest('.status div');
            voltage.textContent = rackStatusInfo.rackDcVoltage != null ? rackStatusInfo.rackDcVoltage + 'V' : '-';

            voltage.classList.remove('threshold-warning', 'threshold-fault');
            voltageDiv.classList.remove('threshold-warning', 'threshold-fault');

            if (rackStatusInfo.rackDcVoltage != null) {
                if (rackStatusInfo.rackDcVoltage > 65 || rackStatusInfo.rackDcVoltage < 45) {
                    voltage.classList.add('threshold-fault');
                    voltageDiv.classList.add('threshold-fault');
                } else if (rackStatusInfo.rackDcVoltage > 60 || rackStatusInfo.rackDcVoltage < 50) {
                    voltage.classList.add('threshold-warning');
                    voltageDiv.classList.add('threshold-warning');
                }
            }
        }
        if (temperature) {
            const temperatureDiv = temperature.closest('.status div');
            temperature.textContent = rackStatusInfo.rackTemperature != null ? rackStatusInfo.rackTemperature + '˚C' : '-';

            temperature.classList.remove('threshold-warning', 'threshold-fault');
            temperatureDiv.classList.remove('threshold-warning', 'threshold-fault');

            if (rackStatusInfo.rackTemperature != null) {
                if (rackStatusInfo.rackTemperature > 35 || rackStatusInfo.rackTemperature < 15) {
                    temperature.classList.add('threshold-fault');
                    temperatureDiv.classList.add('threshold-fault');
                } else if (rackStatusInfo.rackTemperature > 30 || rackStatusInfo.rackTemperature < 20) {
                    temperature.classList.add('threshold-warning');
                    temperatureDiv.classList.add('threshold-warning');
                }
            }
        }
        if (current) {
            const currentDiv = current.closest('.status div');
            current.textContent = rackStatusInfo.rackCurrent != null ? rackStatusInfo.rackCurrent + 'A' : '-';

            current.classList.remove('threshold-warning', 'threshold-fault');
            currentDiv.classList.remove('threshold-warning', 'threshold-fault');

            if (rackStatusInfo.rackCurrent != null) {
                if (rackStatusInfo.rackCurrent > 2.5) {
                    current.classList.add('threshold-fault');
                    currentDiv.classList.add('threshold-fault');
                } else if (rackStatusInfo.rackCurrent > 2.0) {
                    current.classList.add('threshold-warning');
                    currentDiv.classList.add('threshold-warning');
                }
            }
        }
    } else {
        // 데이터가 없는 경우
        status.textContent = '-';
        soc.textContent = '-';
        voltage.textContent = '-';
        temperature.textContent = '-';
        current.textContent = '-';

        if (voltage) {
            voltage.classList.remove('threshold-warning', 'threshold-fault');
        }
        if (temperature) {
            temperature.classList.remove('threshold-warning', 'threshold-fault');
        }
        if (current) {
            current.classList.remove('threshold-warning', 'threshold-fault');
        }
    }

    // 알람 처리
    if (alarmIcon) {
        const alarmBox = alarmIcon.closest('.status div');

        if (rackStatusInfo != null && rackStatusInfo.hasAlarm === true) {
            alarmIcon.classList.add('alarm-active');
            if (alarmBox) {
                alarmBox.classList.add('alarm-background-active');
            }
        } else {
            alarmIcon.classList.remove('alarm-active');
            if (alarmBox) {
                alarmBox.classList.remove('alarm-background-active');
            }
        }
    }

}


// 화재감지 상태 업데이트
function updateFireStatus(fireStatusInfo) {
    const fire = document.querySelector('.fa-fire');

    if (fire) {
        const fireBox = fire.closest('.status div');

        if (fireStatusInfo != null && fireStatusInfo.fireStatus === 1) {
            fire.classList.add('fire-active');
            if (fireBox) {
                fireBox.classList.add('fire-background-active');
            }
        } else {
            fire.classList.remove('fire-active');
            if (fireBox) {
                fireBox.classList.remove('fire-background-active');
            }
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
            row.classList.remove('has-data');
            row.onclick = null;
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

            if (module) {
                rows[index].classList.add('has-data');
            } else {
                rows[index].classList.remove('has-data');
                rows[index].onclick = null;
            }

            // 각각 값을 넣기
            if (moduleIdTd) {
                moduleIdTd.textContent = module.moduleId != null ? module.moduleId : '-';
            }
            if (statusTd) {
                statusTd.textContent = module.batteryModuleStatus || '-';
            }

            // 임계값 처리
            if (moduleDcVoltageTd) {
                moduleDcVoltageTd.textContent = module.moduleDcVoltage != null ? module.moduleDcVoltage : '-';

                moduleDcVoltageTd.classList.remove('threshold-warning', 'threshold-fault');
                if (module.moduleDcVoltage != null) {
                    if (module.moduleDcVoltage > 35 || module.moduleDcVoltage < 10) {
                        moduleDcVoltageTd.classList.add('threshold-fault');
                    } else if (module.moduleDcVoltage > 30 || module.moduleDcVoltage < 20) {
                        moduleDcVoltageTd.classList.add('threshold-warning');
                    }
                }
            }
            if (maxCellVoltageTd) {
                maxCellVoltageTd.textContent = module.maxCellVoltage != null ? module.maxCellVoltage : '-';

                maxCellVoltageTd.classList.remove('threshold-warning', 'threshold-fault');
                if (module.maxCellVoltage != null) {
                    if (module.maxCellVoltage > 4.06 || module.maxCellVoltage < 3.5) {
                        maxCellVoltageTd.classList.add('threshold-fault');
                    } else if (module.maxCellVoltage > 4.055 || module.maxCellVoltage < 4.0) {
                        maxCellVoltageTd.classList.add('threshold-warning');
                    }
                }
            }
            if (minCellVoltageTd) {
                minCellVoltageTd.textContent = module.minCellVoltage != null ? module.minCellVoltage : '-';

                minCellVoltageTd.classList.remove('threshold-warning', 'threshold-fault');
                if (module.minCellVoltage != null) {
                    if (module.minCellVoltage > 4.06 || module.minCellVoltage < 3.5) {
                        minCellVoltageTd.classList.add('threshold-fault');
                    } else if (module.minCellVoltage > 4.055 || module.minCellVoltage < 4.0) {
                        minCellVoltageTd.classList.add('threshold-warning');
                    }
                }
            }
            if (avgModuleTemperatureTd) {
                avgModuleTemperatureTd.textContent = module.avgModuleTemperature != null ? module.avgModuleTemperature : '-';

                avgModuleTemperatureTd.classList.remove('threshold-warning', 'threshold-fault');
                if (module.avgModuleTemperature != null) {
                    if (module.avgModuleTemperature > 35 || module.avgModuleTemperature < 15) {
                        avgModuleTemperatureTd.classList.add('threshold-fault');
                    } else if (module.avgModuleTemperature > 30 || module.avgModuleTemperature < 20) {
                        avgModuleTemperatureTd.classList.add('threshold-warning');
                    }
                }
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
            rows[i].classList.remove('has-data');
            rows[i].onclick = null;
        }
    }
}


// 셀 데이터 로드
function loadCellData(essId, moduleId) {
    return axios.get("/api/cellModal",
        {
            params: {
                essId : essId,
                moduleId : moduleId
            }
        }).then(function (response) {
            const tbody = document.getElementById("cellTableBody");

            const cellInfo = response.data.cellInfo;

            //tbody의 기존 내용을 전부 제거(초기화)
            tbody.innerHTML = "";

            // cellInfo가 있으면 행 생성
            if (cellInfo && cellInfo.length > 0) {
                cellInfo.forEach(cell => {
                    const tr = document.createElement("tr");
                    const moduleTd = document.createElement('td');
                    const cellTd = document.createElement('td');
                    const voltageTd = document.createElement('td');

                    moduleTd.textContent = cell.moduleId != null ? cell.moduleId : '-';
                    cellTd.textContent = cell.cellId != null ? cell.cellId : '-';
                    voltageTd.textContent = cell.voltage != null ? cell.voltage : '-';

                    // 임계값 처리
                    if (cell.voltage != null) {
                        if (cell.voltage > 4.06 || cell.voltage < 3.5) {
                            voltageTd.classList.add('threshold-fault');
                        } else if (cell.voltage > 4.055 || cell.voltage < 4.0) {
                            voltageTd.classList.add('threshold-warning');
                        }
                    }

                    tr.appendChild(moduleTd);
                    tr.appendChild(cellTd);
                    tr.appendChild(voltageTd);

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
    const tbody = document.getElementById("moduleTableBody");

    // 더블 클릭 금지 - 중복 실행을 방지하기 위해서
    if (tbody.style.pointerEvents === 'none') {
        return;
    }

    tbody.style.pointerEvents = 'none';

    loadCellData(essId, moduleId).then(() => {
        document.getElementById("cellModal").classList.add('active');
        // 새 인터벌 생성
        // 모달이 열려있는 동안에만 1초마다 데이터 갱신
        interval = setInterval(function () {
            loadCellData(essId, moduleId)
        }, 1000);
    });
}

// 모달 닫기
function closeCellModal() {
    document.getElementById("cellModal").classList.remove("active");

    const tbody = document.getElementById("moduleTableBody");
    if (tbody) {
        tbody.style.pointerEvents = 'auto'; // 다시 클릭 가능하게 복구
    }

    // 인터벌 초기화
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

// 차트 데이터 불러오기
function loadChart(essId, rackDeviceId) {
    axios.get("/api/chart", {
        params : {
            essId : essId,
            rackDeviceId : rackDeviceId
        }})
        .then(response => {
            const data = response.data;

            // 각 데이터의 배열이 없을 경우 빈 배열로 초기화
            const xAxis = data.xAxis || [];
            const voltageData = data.voltageData || [];
            const currentData = data.currentData || [];
            const temperatureData = data.temperatureData || [];

            // Highcharts 형식인 [시간, 값] 형태로 변환
            const voltage = xAxis.map((time, i) => [time, voltageData[i]]);
            const current = xAxis.map((time, i) => [time, currentData[i]]);
            const temperature = xAxis.map((time, i) => [time, temperatureData[i]]);

            // 차트 생성
            drawGraph(voltage, current, temperature, false);

            // 차트 객체에 마지막 시간 저장
            const rackChart = Highcharts.charts.find(chart =>
                chart && chart.renderTo.id === 'chart'
            );

            if (rackChart) {
                // 업데이트에서 중복 데이터를 방지하기 위해서 차트가 가진 마지막 시간을 저장
                rackChart.lastCreatedAtMillis = xAxis.length > 0 ? xAxis[xAxis.length - 1] : 0;

                // 1분 뒤 최신 데이터 업데이트 시작
                setTimeout(() => lastRackStatusPoint(essId, rackDeviceId), 60000);
            }

    }).catch(error => {
        console.error('차트 데이터 조회 실패',error);
        // 에러 시에는 빈 차트 표시
        drawGraph([], [], [], true);
    });
}

// 차트 업데이트
function lastRackStatusPoint(essId, rackDeviceId) {
    // 기존 차트 객체 찾기
    const rackChart = Highcharts.charts.find(chart =>
        chart && chart.renderTo.id === 'chart'
    );

    // 차트가 없거나 lastCreatedAtMillis가 없으면 업데이트 불가
    if (!rackChart || !rackChart.lastCreatedAtMillis) {
        return;
    }

    // 차트가 기억하는 마지막 데이터의 시간 가져오기
    const lastCreatedAt = formatDate(rackChart.lastCreatedAtMillis);

    // 최신 데이터 조회 API 호출
    axios.get("/api/chart/latest", {
        params: {
            essId : essId,
            rackDeviceId : rackDeviceId,
            lastCreatedAt: lastCreatedAt
        }
    }).then(response => {
        const latestRackStatus = response.data;

        // console.log(latestRackStatus);

        // 받아온 데이터가 없거나 xAxis가 비어있으면 종료
        if (!latestRackStatus || !latestRackStatus.xAxis || latestRackStatus.xAxis.length === 0) {
            return;
        }

        const now = Date.now();
        const threeHoursAgo = now - (3 * 60 * 60 * 1000); // 3시간전

        // 각 데이터의 배열이 없을 경우 빈 배열로 초기화
        const xAxis = latestRackStatus.xAxis || [];
        const voltageData = latestRackStatus.voltageData || [];
        const currentData = latestRackStatus.currentData || [];
        const temperatureData = latestRackStatus.temperatureData || [];

        // 받아온 리스트를 순차적으로 차트에 추가
        xAxis.forEach((time, i) => {
           // 중복 방지
            if (time <= rackChart.lastCreatedAtMillis) {
                return;
            }

            // 차트의 마지막 시간 갱신
            rackChart.lastCreatedAtMillis = time;

            // 데이터가 있고, 데이터가 3시간을 지났으면 shift 처리
            const shiftPoint = rackChart.series[0].data.length > 0 && (rackChart.series[0].data[0].x < threeHoursAgo);

            // 데이터 추가
            rackChart.series[0].addPoint([time, voltageData[i] ?? null], false, shiftPoint);
            rackChart.series[1].addPoint([time, currentData[i] ?? null], false, shiftPoint);
            rackChart.series[2].addPoint([time, temperatureData[i] ?? null], false, shiftPoint);
        });

        // 줌 상태가 아닐 때 x축 업데이트
        if (!rackChart.isZoomed) {
            rackChart.xAxis[0].setExtremes(threeHoursAgo, now, false);
        }
        rackChart.redraw();

    }).catch(error => {
        console.error("최신 차트 데이터 조회 실패", error);
    }).finally(() => {
        // 1분후 다시 갱신
      setTimeout(() => lastRackStatusPoint(essId, rackDeviceId), 60000);
    });
}

// 그래프 그리기
function drawGraph(voltageData, currentData, temperatureData, isError) {
    const now = Date.now();
    const threeHoursAgo = now - (3 * 60 * 60 * 1000);
    // 데이터가 하나라도 있으면 범례 표시
    const hasData = voltageData.length > 0 || currentData.length > 0 || temperatureData.length > 0;

    // 색상 정의
    const colors = {
        voltage: '#22C55E',
        current: '#A855F7',
        temperature: '#3B82F6',
        warning: '#FB923C',
        fault: '#EF4444'
    };

    // 임계치 정의
    const ranges = {
        voltage: {
            min: 50,
            max: 60,
            warning: {
                min: 45,
                max: 65
            }},
        current: {
            min: 0,
            max: 2.0,
            warning: {
                max: 2.5
            }},
        temperature: {
            min: 20,
            max: 30,
            warning: {
                min: 15,
                max: 35
            }}
    };


    // 툴팁에서 시리즈별 기본 색상
    const seriesConfig = {
        '전압(V)': { color: colors.voltage },
        '전류(A)': { color: colors.current },
        '온도(˚C)': { color: colors.temperature }
   };

    // 정상 범위 밴드 정의
    const plotBandMap = {
        voltage : {
            id: 'voltage-band',
            from: ranges.voltage.min,
            to: ranges.voltage.max,
            color: 'rgba(34, 197, 94, 0.15)'
        },
        current : {
            id: 'current-band',
            from: ranges.current.min,
            to: ranges.current.max,
            color: 'rgba(168, 85, 247, 0.12)'
        },
        temperature : {
            id: 'temperature-band',
            from: ranges.temperature.min,
            to: ranges.temperature.max,
            color: 'rgba(59, 130, 246, 0.12)'
        }
    };

   // 차트 우측 상단 정상범위 가이드 정의
   const rangeGuides = [
       { text: '전압 정상범위 (50~60V)', color: 'rgba(34, 197, 94, 0.2)'},
       { text: '전류 정상범위 (0~2A)', color: 'rgba(168, 85, 247, 0.2)'},
       { text: '온도 정상범위 (20~30˚C)', color: 'rgba(59, 130, 246, 0.2)'}
   ]

   // 값에 따라 색상 결정 하는 함수
    function getValueColor(value, seriesName) {
        if (seriesName === '전압(V)') {
            if (value > ranges.voltage.warning.max || value < ranges.voltage.warning.min) {
                return colors.fault; // fault
            } else if (value > ranges.voltage.max || value < ranges.voltage.min) {
                return colors.warning; // warning
            }
        } else if (seriesName === '전류(A)') {
            if (value > ranges.current.warning.max) {
                return colors.fault; // fault
            } else if (value > ranges.current.max) {
                return colors.warning; // warning
            }
        } else if (seriesName === '온도(˚C)') {
            if (value > ranges.temperature.warning.max || value < ranges.temperature.warning.min) {
                return colors.fault; // fault
            } else if (value > ranges.temperature.max || value < ranges.temperature.min) {
                return colors.warning; // warning
            }
        }

        return null; // 기본색
    }

    // 범례 클릭시 범위도 함께 활성/비활성화 하는 함수
    function togglePlotBand(series, plotBand) {
        const axis = series.yAxis;
        if (series.visible) {
            axis.addPlotBand(plotBand);
        } else {
            axis.removePlotBand(plotBand.id);
        }
    }

    // 차트 우측 상단에 정상범위 가이드 그리는 함수
    function renderRangeGuides(chart) {
        // 가이드 시작 좌표 설정
        const totalGuideWidth = 450;
        let currentX = chart.chartWidth - totalGuideWidth;
        const startY = 60;

        // 데이터가 있을 때만 가이드 표시
        if (hasData) {
            rangeGuides.forEach((guide, index) => {
                // 색상 박스 그리기
                chart.renderer.rect(currentX, startY, 12, 12, 2)
                    .attr({
                        fill: guide.color
                    })
                    .add();

                // 설명 텍스트
                const textElement = chart.renderer.text(guide.text, currentX + 20, startY + 10)
                    .css({
                        fontSize: '12px',
                        fontWeight: '600'
                    })
                    .add();

                // 텍스트의 실제 폭을 측정해서 다음 항목의 시작 위치를 계산
                // 폭 + 30 만큼 띄워서 다음 항목 표시
                currentX += textElement.getBBox().width + 30;
            });
        }
    }

    // 시리즈 생성 함수
    function createSeries(name, data, plotBand, zones) {
       return {
           name,
           data,
           connectNulls: false,             // null 포인트는 선을 끊어서 표시
           showInLegend: hasData,           // 데이터가 있을때만 범례 표시
           color: seriesConfig[name].color, // 기본색
           events: {
               // 범례를 숨김 처리하면 밴드도 숨김
               hide() { togglePlotBand(this, plotBand); },
               // 범례를 보이면 밴드도 보임
               show() { togglePlotBand(this, plotBand); }
           },
           zones
       };
    }

    Highcharts.setOptions({
        time: {
            useUTC: false
        },
        lang: {
            // 데이터가 없는 경우 표시
            noData: (!hasData)
                ? (isError ? '데이터를 불러오는 중 오류가 발생했습니다.' : '최근 3시간동안 수집한 데이터가 없습니다.')
                : ''
        }
    });

   // 차트 옵션
    const chartOptions = {
        chart: {
            type: 'line',
            zoomType: 'xy',
            events: {
                selection: function (event) {
                    // reset zoom 버튼을 눌렀을때
                    if (event.resetSelection) {
                        const now = Date.now();
                        const threeHoursAgo = now - (3 * 60 * 60 * 1000);

                        // x축/y축 초기화면으로 복귀
                        this.xAxis[0].setExtremes(threeHoursAgo, now, true);
                        this.yAxis[0].setExtremes(null, null, true);

                        // zoom 상태 false
                        this.isZoomed = false;

                        // reset 버튼이 남아있으면 제거
                        if (this.resetZoomButton) {
                            this.resetZoomButton.destroy();
                            this.resetZoomButton = null;
                        }
                        return false;
                    }

                    // 줌이 발생했을 때
                    if (event.xAxis || event.yAxis) {
                        // 차트 객체에 줌 상태 저장
                        this.isZoomed = true;
                        return true;
                    }
                },
                load: function () {
                    // 차트 우측 상단에 정상범위 가이드 표시
                    renderRangeGuides(this);
                }
            }
        },
        title: {
            text: 'rack 그래프',
            margin: 30,
            style: {
                fontSize: '24px'
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
            min: threeHoursAgo, // 3시간전
            max: now,
            tickInterval: 15 * 60 * 1000, // 15분 단위
            labels: {
                format: '{value:%H:%M}'
            },
            showEmpty: false,
            events: {
                afterSetExtremes: function(e) {
                    // resetZoomButton이 사라진 상태면 reset 판단
                    if (e.trigger === 'zoom' && this.chart.resetZoomButton == null) {
                        this.chart.isZoomed = false;
                    }
                }
            }
        },
        yAxis: [{
            title: {
                text: ''
            },
            // 정상범위 밴드
            plotBands: Object.values(plotBandMap),
            labels: {
                format: '{value}'
            },
            tickAmount: 5,
            min: 0,
            showEmpty: false
        }],
        tooltip: {
            shared: true,
            crosshairs: true,
            xDateFormat: '%H:%M:%S',
            style: {
                fontSize: '16px',
                fontWeight: 'bold'
            },
            useHTML: true,
            // 툴팁에 각 시리즈 값을 출력
            pointFormatter: function () {
                const seriesName = this.series.name;                    // 현재 포인트의 명
                const baseColor = seriesConfig[seriesName].color;       // 시리즈 기본색
                const valueColor = getValueColor(this.y, seriesName);   // 값에 따른 색(warning, fault)

                return `<span style="color:${baseColor}">\u25CF</span> ${seriesName}: ` +
                       `<b style="color:${valueColor}">${this.y !== null ? this.y : '-'}</b></br>`;
            }
        },
        noData: {
            position: {
                top: 0,
                left: 0,
                align: 'center',
                verticalAlign: 'middle'
            },
            style: {
                fontSize: '16px',
                fontWeight: 'bold',
                color: isError ? '#FF6A33' : '#374151'
            }
        },
        // 시리즈 생성
        series: [
            createSeries('전압(V)', voltageData, plotBandMap.voltage, [
                { value: 45, color: colors.fault},          // fault
                { value: 50, color: colors.warning},        // warning
                { value: 60, color: colors.voltage},        // 정상 범위
                { value: 65, color: colors.warning},        // warning
                { color: colors.fault}                      // fault
            ]),
            createSeries('전류(A)', currentData, plotBandMap.current, [
                { value: 2.0, color: colors.current},       // 정상 범위
                { value: 2.5, color: colors.warning},       // warning
                { color: colors.fault}                      // fault
            ]),
            createSeries('온도(˚C)', temperatureData, plotBandMap.temperature, [
                { value: 15, color: colors.fault},          // fault
                { value: 20, color: colors.warning},        // warning
                { value: 30, color: colors.temperature},    // 정상 범위
                { value: 35, color: colors.warning},        // warning
                { color: colors.fault}                      // fault
            ])
        ],
        credits: {
            enabled: false
        },
        isZoomed: false
    };

    // 차트가 없으면 새로 생성
    const existingChart = Highcharts.charts.find(chart => chart && chart.renderTo.id === 'chart');

    if (!existingChart) {
        const newChart = Highcharts.chart('chart', chartOptions);

        // x축 범위를 최근 3시간으로 고정
        newChart.xAxis[0].setExtremes(threeHoursAgo, now);
    }
}

// 날짜 포맷 변환
function formatDate(eventDt) {
    // 서버에서 받은 eventDt를 Date 객체로 변환
    // 서버에서는 문자열로 넘어오기 때문에
    // 한국 시간으로 변환 필요
    const date = new Date(eventDt);
    const offset = 1000 * 60 * 60 * 9;
    const korDate = new Date(date.getTime() + offset);

    const dateFormat = korDate.toISOString().replace('T', ' ').replace('Z', '').split('.')[0];

    return dateFormat;
}



