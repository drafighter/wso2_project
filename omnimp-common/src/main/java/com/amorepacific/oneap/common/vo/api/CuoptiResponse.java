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
 * Date   	          : 2021. 10. 26..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import java.util.List;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ CuoptiResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 10. 26.
 * @version : 1.0
 * @author  : hjw0228
 */
@Data
public class CuoptiResponse {

	private List<CicuemCuOptiQcVo> cicuemCuOptiQcVo;
	private String rsltCd;
	private String rsltMsg;
	
	@Data
	public static class CicuemCuOptiQcVo {
		private String chCd; // 채널코드
		private String chgChCd; // 변경요청경로코드
		private String incsNo; // 통합고객번호
		private String emlOptiYn; // E-Mail수신동의여부
		private String smsOptiYn; // SMS수신동의여부
		private String dmOptiYn; // DM수신동의여부
		private String tmOptiYn; // TM수신동의여부
		private String emlOptiDt; // E-Mail수신동의일자	
		private String smsOptiDt; // SMS수신동의일자
		private String dmOptiDt; // DM수신동의일자
		private String tmOptiDt; // TM수신동의일자
		private String intlOptiYn; // 알림톡수신동의여부
		private String intlOptiDt; // 알림톡수신여부동의일자
		private String kkoIntlOptiYn; // 카카오 알림톡수신동의여부
		private String kkoIntlOptiDt; // 카카오 알림톡수신동의일자
		private String fscrId; // 최초등록ID
		private String fscrTsp; // 최초등록시간
		private String lschId; // 최종변경ID
		private String lschTsp; // 최종변경시간
	}
}
