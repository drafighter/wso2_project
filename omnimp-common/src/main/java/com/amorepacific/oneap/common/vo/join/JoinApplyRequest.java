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
 * Date   	          : 2020. 9. 2..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.join;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.join 
 *    |_ JoinApplyRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 2.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class JoinApplyRequest implements Serializable {
	
	private static final long serialVersionUID = 6246840358172601384L;
	private String uid; // 로그인 아이디
	private String unm; // 로그인 사용자명(본인인증에서 받아옴)
	private String upw; // 로그인 비.밀.번.호
	private String ucpw; // 로그인 확인용 비.밀.번.호
	private String incsno;
	private String xincsno;
    private String chcd; 
    private String integrateid; // 오프라인인 경우 통합아이디 등록 여부
    
    private List<String> terms; // 경로 약관동의 내역 선택 정보
    private List<String> tcatCds; // 경로 약관동의 내역 약관동의코드
    private List<String> tncvNos; // 경로 약관동의 내역 약관번호
    
    private List<String> bpterms; // 뷰티포인트 통합회원 약관
    private List<String> bpTcatCds; // 뷰티포인트 통합회원 약관 경로구분코드
    private List<String> bpTncvNos; // 뷰티포인트 약관동의 내역 약관번호
    
    
    private List<String> marketing; // 수신동의
    private List<String> marketingChcd; //
    
    private String mlogin;
    
    private String corpterms; // 전사약관인 경우 약관동의시 경로에서는 처리하지 않도록 하는 값(true, false)
    
    private String joinType;
    private String joinStepType;
    
    
}
