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
 * Date   	          : 2020. 8. 14..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.terms.vo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.terms 
 *    |_ TermsVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 14.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class TermsVo {
	
	private String tcatCd; // 약관동의유형코드
	private String tncvNo; // 약관동의버전번호
	private String chCd; // 경로구분코드
	private int mkSn; // 표시순번
	private String tncTtl; // 약관제목
	private String tncTxtUrl; // 약관내용URL
	private String tncAgrMandYn; // 약관동의필수여부
	private String tncAgrMandYnTxt; // 약관동의필수여부 문자열
	private String aplyBgnDt; // 적용시작일자
	private String aplyEndDt; // 적용종료일자
	private String kasyTncIdntVl; // (카카오)약관태그명
	private String incsNo;	// 고객통합번호
}
