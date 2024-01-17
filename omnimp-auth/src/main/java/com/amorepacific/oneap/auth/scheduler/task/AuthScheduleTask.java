/*
 * <pre>
 * Copyright (c) 2020 Amore Pacific.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Amore
 * Pacific. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Amore Pacific.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author	          : takkies
 * Date   	          : 2020. 8. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.scheduler.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amorepacific.oneap.auth.mgmt.service.MgmtService;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.scheduler.task 
 *    |_ AuthScheduleTask.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 6.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Component
public class AuthScheduleTask {

	@Value("${scheduler.withdraw.cron.use}")
	private boolean withdrawCronUse;

	@Autowired
	private MgmtService mgmtService;

	/**
	 * 
	 * <pre>
	 * comment  :
	 * 
	 * 1. 회원 탙퇴(통합 탈퇴, 경로 탈퇴) 시 회원 상태를 탈퇴로 변경하고 30일 간 개인정보 포함 유지 가능함
	 * (현재 APWEBDB는 퇼퇴 시점 분리 보관 후 Row 삭제하고 있으나 그럴 필요는 없음)
	 * ﻿==> 회원 탈퇴 시, SSO DB (UM_USER, UM_USER_ATTRIBUTE) 상에서는 Disabled로 처리하여 로그인 불가 처리
	 * ==> 기존 회원 데이터 마이그레이션 시에는 UM_USER table에만 login Web ID 와 임의의 password를 포함한 값만 migration 처리  
	 *  (탈퇴된 회원의 ID는 재사용이 불가함에 따라, 중복 방지를 위해 UM_USER table에 데이터 생성 필요)
	 * 2. 30일 이후 개인정보는 삭제해야 하고 ID 정보는 삭제하지 않아도 됨
	 * (저희는 탈퇴 시점에 고객명을 지우든(****) 30일 이후 지우든(*****) 상관 없을 거 같습니다)
	 * ==> 30일 이후, 해당 회원에 대한 UM_USER_ATTRIBUTE 테이블 삭제 처리, UM_USER table 내에 Password 값도 Not Null 임에 따라
	 * 임의의 값으로 초기화 처리
	 * ==> 탈퇴일자는 UM_USER_ATTRIBUTE table의 ROW 수가 너무 많을 듯 함에 따라, UM_USER table에 alter 명령어로 탈퇴일자 추가 예정
	 * ---------------------------------------------------------------------------------------------------- 
	 * *           *　　　　　　*　　　　　　*　　　　　　*　　　　　　*
	 * 초(0-59)   분(0-59)　　시간(0-23)　　일(1-31)　　월(1-12)　　요일(0-7) 
	 * 순서대로 초-분-시간-일-월-요일 순, 괄호 안의 숫자 범위 내로 별 대신 입력 가능
	 * 요일에서 0과 7은 일요일이며, 1부터 월요일이고 6이 토요일
	 * 10초 마다 : 0/10 * * * * *
	 * 매일 1시 : 0 0 1 * * *
	 * author   : takkies
	 * date     : 2020. 8. 6. 오전 10:14:55
	 * </pre>
	 */
	@Scheduled(cron = "${scheduler.withdraw.cron}", zone = "Asia/Seoul")
	public void doWithdrawProcess() {
		if (this.withdrawCronUse) {
			log.debug("▶▶▶▶▶ user withdrawl schedule [탈퇴일자 기준으로 30 일 이후 탈퇴처리하는 배치]");
			boolean rtn = this.mgmtService.doWithdraw();
			log.debug("▶▶▶▶▶ do withdraw user process : {}", rtn);
		}

	}
}
