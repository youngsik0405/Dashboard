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

    // 대시보드 갱신 인터벌
    // setInterval(updateDashboard, 1000);

    // 차트 실시간 갱신 인터벌
    setInterval(() => lastRackStatusPoint(essId, rackDeviceId), 1000);
    // lastRackStatusPoint(essId, rackDeviceId);
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

            setTimeout(updateDashboard, 1000);

        })
        .catch(error => {
            console.error('갱신 실패', error);
        })
}

// 이벤트 히스토리 업데이트
function updateEventHistory(eventHistory) {
    // 데이터 변경 확인
    // if (JSON.stringify(previousEventHistory) === JSON.stringify(eventHistory)) {
    //     console.log('이벤트 데이터 동일 - 갱신 안함');
    //     return;
    // }
    //
    // console.log('새로운 이벤트 데이터 - 갱신 시작');

    const tbody = document.querySelector('#eventContent tbody');
    const rows = tbody.querySelectorAll('tr');

    // eventHitory가 없거나 비어있을 경우
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
                eventHistoryDt.textContent = events.eventDt ? formatData(events.eventDt) : '-';
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
    document.getElementById('eventDt').textContent = eventDt ? formatData(eventDt) : '없음';

    axios.get("/api/eventDetail", {
        params: {
            eventId: eventId
        }
    }).then(response => {

        const eventDetail = response.data.eventDetail || '없음';

        document.getElementById('eventDetail').textContent = eventDetail.includes('/') ? eventDetail.split('/').join('\n') : eventDetail;

        popover.style.display = 'block';

    }).catch(error => {
        console.error('이벤트 상세 조회 실패', error);
    });
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
            voltage.textContent = rackStatusInfo.rackDcVoltage != null ? rackStatusInfo.rackDcVoltage + 'V' : '-';

            voltage.classList.remove('threshold-high', 'threshold-low');
            if (rackStatusInfo.rackDcVoltage != null) {
                if (rackStatusInfo.rackDcVoltage > 60) {
                    voltage.classList.add('threshold-high');
                } else if (rackStatusInfo.rackDcVoltage < 50) {
                    voltage.classList.add('threshold-low');
                }
            }
        }
        if (temperature) {
            temperature.textContent = rackStatusInfo.rackTemperature != null ? rackStatusInfo.rackTemperature + '˚C' : '-';

            temperature.classList.remove('threshold-high', 'threshold-low');
            if (rackStatusInfo.rackTemperature != null) {
                if (rackStatusInfo.rackTemperature > 30) {
                    temperature.classList.add('threshold-high');
                } else if (rackStatusInfo.rackTemperature < 20) {
                    temperature.classList.add('threshold-low');
                }
            }
        }
        if (current) {
            current.textContent = rackStatusInfo.rackCurrent != null ? rackStatusInfo.rackCurrent + 'A' : '-';

            current.classList.remove('threshold-high', 'threshold-low');
            if (rackStatusInfo.rackCurrent != null) {
                if (rackStatusInfo.rackCurrent > 2) {
                    current.classList.add('threshold-high');
                } else if (rackStatusInfo.rackCurrent < 0) {
                    current.classList.add('threshold-low');
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
            voltage.classList.remove('threshold-high', 'threshold-low');
        }
        if (temperature) {
            temperature.classList.remove('threshold-high', 'threshold-low');
        }
        if (current) {
            current.classList.remove('threshold-high', 'threshold-low');
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

                moduleDcVoltageTd.classList.remove('threshold-high', 'threshold-low');
                if (module.moduleDcVoltage != null) {
                    if (module.moduleDcVoltage > 40) {
                        moduleDcVoltageTd.classList.add('threshold-high');
                    } else if (module.moduleDcVoltage < 20) {
                        moduleDcVoltageTd.classList.add('threshold-low');
                    }
                }
            }
            if (maxCellVoltageTd) {
                maxCellVoltageTd.textContent = module.maxCellVoltage != null ? module.maxCellVoltage : '-';

                maxCellVoltageTd.classList.remove('threshold-high', 'threshold-low');
                if (module.maxCellVoltage != null) {
                    if (module.maxCellVoltage > 4.045) {
                        maxCellVoltageTd.classList.add('threshold-high');
                    } else if (module.maxCellVoltage < 4.0) {
                        maxCellVoltageTd.classList.add('threshold-low');
                    }
                }
            }
            if (minCellVoltageTd) {
                minCellVoltageTd.textContent = module.minCellVoltage != null ? module.minCellVoltage : '-';

                minCellVoltageTd.classList.remove('threshold-high', 'threshold-low');
                if (module.minCellVoltage != null) {
                    if (module.minCellVoltage > 4.045) {
                        minCellVoltageTd.classList.add('threshold-high');
                    } else if (module.minCellVoltage < 4.0) {
                        minCellVoltageTd.classList.add('threshold-low');
                    }
                }
            }
            if (avgModuleTemperatureTd) {
                avgModuleTemperatureTd.textContent = module.avgModuleTemperature != null ? module.avgModuleTemperature : '-';

                avgModuleTemperatureTd.classList.remove('threshold-high', 'threshold-low');
                if (module.avgModuleTemperature != null) {
                    if (module.avgModuleTemperature > 30) {
                        avgModuleTemperatureTd.classList.add('threshold-high');
                    } else if (module.avgModuleTemperature < 20) {
                        avgModuleTemperatureTd.classList.add('threshold-low');
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
                        if (cell.voltage > 4.03) {
                            voltageTd.classList.add('threshold-high');
                        } else if (cell.voltage < 4.0) {
                            voltageTd.classList.add('threshold-low');
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

            let voltageData = [];
            let currentData = [];
            let temperatureData = [];

            // 데이터가 있으면 변환
            if (response.data && response.data.length > 0) {

                let prevTime = null;

                response.data.forEach(history => {
                    const currentTime = new Date(history.createdAt).getTime();

                    if (prevTime !== null) {
                        const timeDiff = currentTime - prevTime;

                        if (timeDiff > 10000) {
                            const disconnectStartTime = prevTime + 1000;

                            voltageData.push([disconnectStartTime, null]);
                            currentData.push([disconnectStartTime, null]);
                            temperatureData.push([disconnectStartTime, null]);

                            const disconnectEndTime = currentTime - 1000;

                            voltageData.push([disconnectEndTime, null]);
                            currentData.push([disconnectEndTime, null]);
                            temperatureData.push([disconnectEndTime, null]);
                        }
                    }

                    voltageData.push([currentTime, history.rackDcVoltage != null ? history.rackDcVoltage : null]);
                    currentData.push([currentTime, history.rackCurrent != null ? history.rackCurrent : null]);
                    temperatureData.push([currentTime, history.rackTemperature != null ? history.rackTemperature : null]);

                    prevTime = currentTime;
                });


                // 데이터 변환: Highchart 형식 [timestamp, value]
                // voltageData = response.data.map(history => [
                //     new Date(history.createdAt).getTime(), history.rackDcVoltage != null ? history.rackDcVoltage : null
                // ]);
                //
                // currentData = response.data.map(history => [
                //     new Date(history.createdAt).getTime(), history.rackCurrent != null ? history.rackCurrent : null
                // ]);
                //
                // temperatureData = response.data.map(history => [
                //     new Date(history.createdAt).getTime(), history.rackTemperature != null ? history.rackTemperature : null
                // ]);
            }

            // 차트 생성
            drawGraph(voltageData, currentData, temperatureData, false);

            // 차트 객체에 마지막 시간 저장
            const rackChart = Highcharts.charts.find(chart =>
                chart && chart.renderTo.id === 'chart'
            );
            if (rackChart) {
                rackChart.lastCreatedAtMillis =
                    (response.data && response.data.length > 0)
                        ? new Date(response.data[response.data.length - 1].createdAt).getTime()
                        : 0;
            }

    }).catch(error => {
        console.error('차트 데이터 조회 실패',error);
        // 에러 시에는 빈 차트 표시
        drawGraph([], [], [], true);
    });
}

// 차트 업데이트
function lastRackStatusPoint(essId, rackDeviceId) {
    const rackChart = Highcharts.charts.find(chart =>
        chart && chart.renderTo.id === 'chart'
    );

    if (!rackChart) {
        return;
    }

    // 차트가 기억하는 마지막 데이터의 시간 가져오기
   let lastTime = rackChart.lastCreatedAtMillis || 0;

    if (lastTime === 0) {
        return;
    }

    // 시간을 포맷팅 해서 "yyyy-MM-dd HH:mm:ss"형태로 반환
    const lastCreatedAt = formatData(lastTime);

    axios.get("/api/chart/latest", {
        params: {
            essId : essId,
            rackDeviceId : rackDeviceId,
            lastCreatedAt: lastCreatedAt
        }
    }).then(response => {
        const lastestRackStatus = response.data;

        // 받아온 데이터가 없으면 종료
        if (!lastestRackStatus || lastestRackStatus.length === 0) {
            return;
        }

        const now = Date.now();
        const oneHourAgo = now - (60 * 60 * 1000);
        let updated = false;

        // 받아몬 리스트를 순차적으로 차트에 추가
        lastestRackStatus.forEach(data => {
            const createdAtMillis = new Date(data.createdAt).getTime();

            // 중복 방지 (차트의 마지막 시간보다 작거나 같으면 패스)
            if (createdAtMillis <= rackChart.lastCreatedAtMillis) {
                console.log('중복 데이터 스킵:', createdAtMillis, '<=', rackChart.lastCreatedAtMillis);
                return;
            }

            // 10초 이상 연결이 끊긴 경우 데이터 null
            const timeDiff = createdAtMillis - rackChart.lastCreatedAtMillis;
            console.log('시간차:', timeDiff, 'ms');

            if (timeDiff > 10000) {
                console.log('연결 끊김 감지 - null 포인트 추가');
                // 연결끊김 시작 지점
                const disconnectStartTime = rackChart.lastCreatedAtMillis + 1000;
                const shiftDisconnectStart = rackChart.series[0].data.length > 0 && (rackChart.series[0].data[0].x < oneHourAgo);

                rackChart.series[0].addPoint([disconnectStartTime, null], false, shiftDisconnectStart);
                rackChart.series[1].addPoint([disconnectStartTime, null], false, shiftDisconnectStart);
                rackChart.series[2].addPoint([disconnectStartTime, null], false, shiftDisconnectStart);

                // 연결끊김 끝 지점
                const disconnectEndTime = createdAtMillis - 1000;
                const shiftDisconnectEnd = rackChart.series[0].data.length > 0 && (rackChart.series[0].data[0].x < oneHourAgo);

                rackChart.series[0].addPoint([disconnectEndTime, null], false, shiftDisconnectEnd);
                rackChart.series[1].addPoint([disconnectEndTime, null], false, shiftDisconnectEnd);
                rackChart.series[2].addPoint([disconnectEndTime, null], false, shiftDisconnectEnd);
            }

            // 차트의 마지막 시간 갱신
            rackChart.lastCreatedAtMillis = createdAtMillis;
            updated = true;

            // 데이터가 있고, 데이터가 1시간을 지났으면 shift 처리
            const shiftPoint = rackChart.series[0].data.length > 0 && (rackChart.series[0].data[0].x < oneHourAgo);

            // 데이터 추가 - addPoint(추가할 데이터, redraw 여부, shiftPoint 여부)
            // redraw 여부를 false로 한 이유는 나중에 한번에 처리하기 위해서
            rackChart.series[0].addPoint([createdAtMillis, data.rackDcVoltage ?? null], false, shiftPoint);
            rackChart.series[1].addPoint([createdAtMillis, data.rackCurrent ?? null], false, shiftPoint);
            rackChart.series[2].addPoint([createdAtMillis, data.rackTemperature ?? null], false, shiftPoint);
        });

        // 데이터가 추가되면 차트 다시 그리기
        if (updated) {
            // 줌 상태가 아닐 때 x축 업데이트
            if (!rackChart.isZoomed) {
                rackChart.xAxis[0].setExtremes(oneHourAgo, now, false);
            }
            rackChart.redraw();
        }
    }).catch(error => {
        console.error("최신 차트 데이터 조회 실패", error);
    });

    // .finally(() => {
    //         setTimeout(() => lastRackStatusPoint(essId, rackDeviceId), 1000);
    //     })
}

// 그래프 그리기
function drawGraph(voltageData, currentData, temperatureData, isError) {
    const now = Date.now();
    const oneHourAgo = now - (60 * 60 * 1000);
    const showLegend = voltageData.length > 0 || currentData.length > 0 || temperatureData.length > 0;

    Highcharts.setOptions({
        time: {
            useUTC: false
        },
        lang: {
            noData: isError ? '데이터를 불러오는 중 오류가 발생했습니다.' : '최근 1시간동안 수집한 데이터가 없습니다.'
        }
    });

    const chartOptions = {
        chart: {
            type: 'line',
            zoomType: 'xy',
            events: {
                selection: function (event) {
                    // 리셋 시 줌 상태 false
                    if (event.resetSelection) {
                        this.isZoomed = false;
                        return true;
                    }

                    if (event.xAxis || event.yAxis) {
                        // 차트 객체에 줌 상태 저장
                        this.isZoomed = true;
                        return true;
                    }
                },
                load: function () {
                    // 초기 로드 시 줌 상태 false
                    this.isZoomed = false;
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
            min: oneHourAgo, // 1시간전
            max: now,
            tickInterval: 5 * 60 * 1000, // 5분 단위
            labels: {
                format: '{value:%H:%M}'
            },
            showEmpty: false,
            events: {
                afterSetExtremes: function(e) {
                    // Reset zoom 버튼 클릭 감지
                    if (e.trigger === 'zoom' && this.chart.resetZoomButton == null) {
                        // resetZoomButton이 사라진 상태면 reset 판단
                        this.chart.isZoomed = false;
                    }
                }
            }
        },
        yAxis: [{
            title: {
                text: ''
            },
            plotBands: [{
                from: 50,
                to: 60,
                color: 'rgba(59, 130, 246, 0.15)'
            }, {
                from: 20,
                to: 30,
                color: 'rgba(245, 158, 11, 0.15)'
            }, {
                from: 0,
                to: 2,
                color: 'rgba(244, 63, 94, 0.15)'
            }],
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
            valueDecimals: 1,
            style: {
                fontSize: '18px'
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
        series: [{
            name: '전압(V)',
            data: voltageData,
            connectNulls: false, // null 값이면 연결 안함, 데이터 누락 표시를 위해서
            showInLegend: showLegend
        }, {
            name: '전류(A)',
            data: currentData,
            connectNulls: false,
            showInLegend: showLegend
        }, {
            name: '온도(˚C)',
            data: temperatureData,
            connectNulls: false,
            showInLegend: showLegend
        },
        {
            name: '전압 정상범위 (50~60V)',
            data: [],
            showInLegend: showLegend,
            color: 'rgba(59, 130, 246, 0.15)',
            marker: {
                symbol: 'square',
                radius: 8
            },
            lineWidth: 0
        },
        {
            name: '온도 정상범위 (20~30˚C)',
            data: [],
            showInLegend: showLegend,
            color: 'rgba(245, 158, 11, 0.15)',
            marker: {
                symbol: 'square',
                radius: 8
            },
            lineWidth: 0
        },
        {
            name: '전류 정상범위 (0~2A)',
            data: [],
            showInLegend: showLegend,
            color: 'rgba(244, 63, 94, 0.15)',
            marker: {
                symbol: 'square',
                radius: 8
            },
            lineWidth: 0
        }
        ],
        credits: {
            enabled: false
        },
        isZoomed: false
    };

    // 차트가 없으면 새로 생성
    const existingChart = Highcharts.charts.find(chart => chart && chart.renderTo.id === 'chart');

    if (!existingChart) {
        Highcharts.chart('chart', chartOptions);
    }
}

// 날짜 포맷 변환
function formatData(eventDt) {
    // 서버에서 받은 eventDt를 Date 객체로 변환
    // 서버에서는 문자열로 넘어오기 때문에
    // 한국 시간으로 변환 필요
    const date = new Date(eventDt);
    const offset = 1000 * 60 * 60 * 9;
    const korDate = new Date(date.getTime() + offset);

    const dateFormat = korDate.toISOString().replace('T', ' ').replace('Z', '').split('.')[0];

    return dateFormat;
}



