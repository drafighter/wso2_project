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
 * Date   	          : 2023. 10. 24..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.kakao;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.kakao 
 *    |_ KakaoNoticeResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 10. 24.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
public class KakaoNoticeResponse {
	
	private KakaoMsgInput tgtSMS_KKO_MSG_IInput;
	private KakaoNoticeResponseData Response;
	private String ID;
	private String STATUS;
	private String PHONE;
	private String CALLBACK;
	private String REQDATE;
	private String MSG;
	private String TEMPLATE_CODE;
	private String FAILED_TYPE;
	private String FAILED_SUBJECT;
	private String FAILED_MSG;
	private String PROFILE_KEY;
	private String APPL_CL_CD;
	private String PLTF_CL_CD;
	private StartTransactionOutput startTransactionOutput;
	private KakaoMsgOutput tgtSMS_KKO_MSG_IOutput;
	
	@Data
	public static class StartTransactionOutput {
		private String transactionName;
	}
	
	@Data
	public static class KakaoMsgInput {
		private String id;
		private String status;
		private String phone;
		private String callback;
		private String reqdate;
		private String msg;
		private String template_code;
		private String failed_type;
		private String failed_subject;
		private String failed_msg;
		private String profile_key;
		private String pltf_cl_cd;
		private String appl_cl_cd;
	}
	
	@Data
	public static class KakaoMsgOutput {
		private String selcCnt;
	}
}
