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
 * Date   	          : 2020. 7. 9..
 * Description 	  : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.validation;

import lombok.Data;

/**
 * <pre>
 * com.apmorepacific.oneap.common.validation 
 *    |_ PhoneInfo.java
 *    
 * 각 필드에 대한 설명   
 * +-----------------------------+-----------------------------------------    
 * + countryCode                 | 전화번호 국가코드 번호
 * +-----------------------------+----------------------------------------- 
 * + leadingZeros                | 0을 포함하는 숫자길이
 * +-----------------------------+----------------------------------------- 
 * + nationalFormatNumber        | 지역 전화번호(포맷) 
 * +-----------------------------+----------------------------------------- 
 * + nationalNoFormatNumber      | 지역 전화번호(포맷없음, 첫번째 0 빠짐)
 * +-----------------------------+----------------------------------------- 
 * + internationalFormatNumber   | 국가별 국제 전화번호(포맷)
 * +-----------------------------+----------------------------------------- 
 * + internaitonalNoFormatNumber | 국가별 국제 전화번호(포맷없음)
 * +-----------------------------+----------------------------------------- 
 * + validateNumber              | 유효 전화번호인지 체크
 * +-----------------------------+-----------------------------------------
 * + numberType                  | 전화번호인지 타입
 * +-----------------------------+-----------------------------------------   
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */
@Data
public class PhoneInfo {
	/** 전화번호 국가코드 번호 */
	private int countryCode;
	/** 0을 포함하는 숫자길이 */
	private int leadingZeros;
	/** 지역 전화번호(포맷없음, 첫번째 0 빠짐) */
	private long nationalNoFormatNumber;
	/** 지역 전화번호(포맷) */
	private String nationalFormatNumber;
	/** 국가별 국제 전화번호(포맷) */
	private String internationalFormatNumber;
	/** 국가별 국제 전화번호(포맷없음) */
	private String internaitonalNoFormatNumber;
	/** 유효 전화번호인지 체크 */
	private boolean validNumber;
	/** 번호의 타입 */
	private String numberType;
}
