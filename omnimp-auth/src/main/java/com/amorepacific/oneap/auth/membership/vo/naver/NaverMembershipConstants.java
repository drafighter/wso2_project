/*0
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
 * Date   	          : 2023. 4. 16..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.membership.vo.naver;

/**
 * <pre>
 * com.amorepacific.oneap.auth.membership.vo.naver 
 *    |_ NaverMembershipConstants.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 4. 16.
 * @version : 1.0
 * @author  : hjw0228
 */

public class NaverMembershipConstants {
	
	public static final String[] AGREE_BP_DEV = {"qQaVzCONQuWaYCR2ticRwA"}; 				// 개발계 네이버 → 아모레퍼시픽 개인정보 제 3자 제공 동의 (필수)
	public static final String[] AGREE_BP_PROD = {"D_iWwVUGQP2ByOYoHna6xg"}; 				// 운영계 네이버 → 아모레퍼시픽 개인정보 제 3자 제공 동의 (필수)
	public static final  String[] AGREE_010_DEV = {"HvLFBhmzQn2owC5X1senlA"};				// 개발계 아모레퍼시픽 뷰티포인트 서비스 이용약관 (필수)
	public static final  String[] AGREE_010_PROD = {"AdMsM6ZkSxS7JEMzXl3lhA"};				// 운영계 아모레퍼시픽 뷰티포인트 서비스 이용약관 (필수)
	public static final  String[] AGREE_030_DEV = {"4NUE-mTSTn6SNskTNp9P8Q"};				// 개발계 아모레퍼시픽 개인정보 수집이용 동의 (필수)
	public static final  String[] AGREE_030_PROD = {"wq-X9vCsQRmHsxSPhRwWFw"};				// 운영계 아모레퍼시픽 개인정보 수집이용 동의 (필수)
	public static final  String[] AGREE_050_DEV = {"cDbdbhqHQo6zVchMDmh6nA"};				// 개발계 아모레퍼시픽 개인정보 수집이용 동의 (선택)
	public static final  String[] AGREE_050_PROD = {"28t3Q40fTBK6u8lXwrfbSg"};				// 운영계 아모레퍼시픽 개인정보 수집이용 동의 (선택)
	public static final  String[] AGREE_RECEPTION_DEV = {"zd4lDcWxTbm3lurV9PcM8Q"};			// 개발계 아모레퍼시픽 뷰티포인트 이벤트 혜택 소식 수신 동의 (선택)
	public static final  String[] AGREE_RECEPTION_PROD = {"WhTrXSMXRdC-21-CN-UPBQ"};		// 운영계 아모레퍼시픽 뷰티포인트 이벤트 혜택 소식 수신 동의 (선택)
	public static final  String[] AGREE_SMS_RECEPTION_DEV = {"FMZvk5N8RgKK_5nOKgjoxw"};		// 개발계 SMS 수신 동의
	public static final  String[] AGREE_SMS_RECEPTION_PROD = {"1itrBrpfShWX_2kKt524Lw"};	// 운영계 SMS 수신 동의
	
	public static String[] getAgreeBp(String profile) {
		if("prod".equals(profile)) return AGREE_BP_PROD;
		else return AGREE_BP_DEV; 
	}
	
	public static String[] getAgree010(String profile) {
		if("prod".equals(profile)) return AGREE_010_PROD;
		else return AGREE_010_DEV; 
	}
	
	public static String[] getAgree030(String profile) {
		if("prod".equals(profile)) return AGREE_030_PROD;
		else return AGREE_030_DEV; 
	}
	
	public static String[] getAgree050(String profile) {
		if("prod".equals(profile)) return AGREE_050_PROD;
		else return AGREE_050_DEV; 
	}
	
	public static String[] getAgreeReception(String profile) {
		if("prod".equals(profile)) return AGREE_RECEPTION_PROD;
		else return AGREE_RECEPTION_DEV; 
	}
	
	public static String[] getAgreeSmsReception(String profile) {
		if("prod".equals(profile)) return AGREE_SMS_RECEPTION_PROD;
		else return AGREE_SMS_RECEPTION_DEV; 
	}
}
