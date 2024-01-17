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
package com.amorepacific.oneap.auth.cert.vo;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * com.amorepacific.oneap.auth.cert.vo 
 *    |_ KmCertResult.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 6.
 * @version : 1.0
 * @author : takkies
 */
@Data
@RequiredArgsConstructor
public class KmcisResult {

	private String certNum; // 요청번호
	private String date; // 요청일시
	private String ci; // 연계정보(CI)
	private String phoneNo; // 휴대폰번호
	private String phoneCorp;// 이동통신사
	private String birthDay; // 생년월일
	private String gender; // 성별
	private String nation; // 내국인
	private String name; // 성명
	private String result; // 결과값
	private String certMet; // 인증방법
	private String ip; // ip주소
	private String mName; // 미성년자 성명
	private String mBirthDay; // 미성년자 생년월일
	private String mGender; // 미성년자 성별
	private String mNation; // 미성년자 내외국인
	private String plusInfo; //
	private String di; // 중복가입확인정보(DI)

	@NonNull
	private Integer status;
	@NonNull
	private String message;

}
