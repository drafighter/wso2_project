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
 * Author	          : hjw0228
 * Date   	          : 2023. 4. 12..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.kakao;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.kakao 
 *    |_ KakaoNoticeRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 4. 12.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoNoticeRequest implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -4794282386179259248L;
	
	private String ID; // 고객이 발급한 SubID
	private String STATUS; // 발송상태  
	private String PHONE; // 수신할 핸드폰 번호, 동보일 경우 전화번호 개수
	private String CALLBACK; // 송신자 전화번호
	private String REQDATE; // 메시지를 전송할 시간, 미래 시간을 넣으면 예약 발송됨
	private String SENTDATE; // 메세지를 전송할 시간
	private String RSLTDATE; // 이동통신사로부터 결과를 통보받은 시각
	private String REPORTDATE; // 결과 수신 받은 시각
	private String RSLT; // 발송 결과수신 값
	private String MSG_RSLT; // 카카오알림톡 실패시 메세지 결과 수신 값
	private String NET; // 전송완료 후 최종 이동통신사 정보
	private String MSG; // 전송할 메세지
	private String TEMPLATE_CODE; // 카카오 알림톡 템플릿 코드
	private String FAILED_TYPE; // 카카오알림톡 전송 실패 시 전송할 메세지 타입
	private String FAILED_SUBJECT; // 카카오알림톡 전송 실패 시 전송할 제목
	private String FAILED_MSG; // 카카오알림톡 전송 실패 시 전송할 내용
	private String FAILED_IMG; // 카카오알림톡 전송 실패 시 전송할 이미지
	private String PROFILE_KEY; // @옐로우아이디 프로파일키 (cjagent.conf 설정)
	private String URL; // 알림톡 버튼 타입 URL
	private String URL_BUTTON_TXT; // 알림톡 타입 버튼 TEXT
	private String IMG_PATH; // 친구톡 이미지 경로
	private String IMG_URL; // 친구톡 이미지 URL
	private String BUTTON_JSON; // 버튼그룹 데이터 JSON
	private String AD_FLAG; // 친구톡 광고 표시여부
	private String ETC1; // 통합고객번호(기타1)
	private String ETC2; // 캠페인구분코드(기타2)
	private String ETC3; // 거래처ID(기타3)
	private String ETC4; // 기타 필드4
	private String ETC5; // 기타 필드5
	private String ETC6; // 기타 필드6
	private String APPL_CL_CD; // 등록어플리케이션구분코드
	private String PLTF_CL_CD; // 등록플랫폼구분코드

}
