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
 * Date   	          : 2020. 8. 5..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.cert.vo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.cert.vo 
 *    |_ IpinResult.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 5.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class IpinResult {

	private int result;
	
	private String enc_data;
	private String param_r1;
	private String param_r2;
	private String param_r3;
	
	private String vnNumber; // 가상주민번호 (13자리이며, 숫자 또는 문자 포함)
	private String name; // 이름
	private String dupInfo; // 중복가입 확인값 (DI - 64 byte 고유값)
	private String ageCode; // 연령대 코드 (개발 가이드 참조)
	private String genderCode; // 성별 코드 (개발 가이드 참조)
	private String birthDate; // 생년월일 (YYYYMMDD)
	private String nationalInfo; // 내/외국인 정보 (개발 가이드 참조)
	private String cpRequestNo; // CP 요청번호
	private String authInfo; // 본인확인 수단 (개발 가이드 참조)
	private String coInfo; // 연계정보 확인값 (CI - 88 byte 고유값)
	private String ciUpdate; // CI 갱신정보    
	
}
