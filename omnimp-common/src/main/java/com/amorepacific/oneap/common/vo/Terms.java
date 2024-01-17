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
 * Date   	          : 2020. 8. 27..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.join.vo 
 *    |_ JoinTerms.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 27.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class Terms implements Serializable {

	private static final long serialVersionUID = -6630199969162153306L;
	private String tncAgrYn; // tnc_agr_yn 약관동의여부
	private int incsNo; // incs_no 통합고객번호
	private String tcatCd; // tcat_cd 약관동의유형코드
	private String tncvNo; // tncv_no 약관동의버전번호
	private String chgChCd; // chg_ch_cd 변경경로구분코드
	private String tncaChgDt; // tnca_chg_dt 약관동의변경일자
	private String tncaDttm;
}
