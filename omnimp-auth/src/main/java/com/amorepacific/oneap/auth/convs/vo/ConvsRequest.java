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
 * Date   	          : 2020. 9. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.convs.vo;

import java.util.List;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.convs.vo 
 *    |_ ConvsRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 7.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class ConvsRequest {

	private String trnsType;
	private String id;
	private String pw;
	private String cpw;
	private String xno;
	private String previd;
	
	private List<String> terms; // 경로 약관동의 내역 선택 정보
    private List<String> tcatCds; // 경로 약관동의 내역 약관동의코드
    private List<String> tncvNos; // 경로 약관동의 내역 약관번호
    
    private List<String> bpterms; // 뷰티포인트 통합회원 약관
    private List<String> bpTcatCds; // 뷰티포인트 통합회원 약관 경로구분코드
    private List<String> bpTncvNos; // 뷰티포인트 약관동의 내역 약관번호
    
    private List<String> marketing; // 수신동의
    private List<String> marketingChcd; //
	
	private boolean callOnlineApi = true; // 온라인 API 호출여부
	private boolean callOfflineApi = true; // 오프라인 API 호출여부
	private boolean callBpApi = true; // 뷰티포인트 API 호출여부
	
	private String convsType;
}
