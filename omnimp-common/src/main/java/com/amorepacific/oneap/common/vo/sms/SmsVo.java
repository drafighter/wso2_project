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
 * Date   	          : 2020. 8. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sms;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.sms.vo 
 *    |_ SmsVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 7.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class SmsVo {
	private int smsAthtSendNo; //sms_atht_send_no SMS인증발송번호
	@JsonIgnore
	private int incsNo; //incs_no 통합고객번호
	@JsonIgnore
	private String smsAthtNoVl; //sms_atht_no_vl SMS인증번호값
	@JsonIgnore
	private Timestamp smsSendDttm; //sms_send_dttm SMS발송일시
	@JsonIgnore
	private Timestamp smsAthtExprDttm; //sms_atht_expr_dttm SMS인증만료일시
	private String smsAthtProcRsltCd; //sms_atht_proc_rslt_cd SMS인증처리결과코드
	private int smsAthtFailCnt; //sms_atht_fail_cnt SMS인증실패수
	@JsonIgnore
	private String fscrId; //fscr_id 최초생성ID
	@JsonIgnore
	private Timestamp fscrTsp; //fscr_tsp 최초생성시각
	@JsonIgnore
	private String lschId; //lsch_id 최종변경ID
	@JsonIgnore
	private Timestamp lschTsp; //lsch_tsp 최종변경시각
	@JsonIgnore
	private String sdtpCd; //sdtp_cd 표준시간대코드
	@JsonIgnore
	private String regApplClCd; //reg_appl_cl_cd 등록어플리케이션구분코드
	
	private int status;
	@JsonIgnore
	private String rsltCd;
	@JsonIgnore
	private String rsltMsg;
	
	
	@JsonIgnore
	private String sendMessage;
	
	private String smsNo;
	private String name;
	private String phoneNo;
	private String times;

}
