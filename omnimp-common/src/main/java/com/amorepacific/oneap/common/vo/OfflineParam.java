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
 * Date   	          : 2020. 9. 3..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ EntryVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 3.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class OfflineParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8827222834521994487L;
	private String chCd; //
	private String joinPrtnId; //
	private String joinPrtnNm; //
	private String joinEmpId; // 이크리스 추가 정보
	private String returnUrl; // 
	// private String displayPath;// 완료화면 경로표시 여부 
	private String addInfo; // 부가 정보 필요
	
	private String chnCd; // 이니스프리 추가 정보
	private String storeCd; // 이니스프리 추가 정보
	private String storenm; // 이니스프리 추가 정보
	private String user_id; // 이니스프리 추가 정보
	
	private String hh; // 페이지 헤더(제목) 노출 여부, Y 페이지 헤더(제목) 미노출 
	private String dt; // 디바이스 타입, W web (Desktop Browser), M web (Mobile Browser), A app (MobileApplication) 
	private String op; // Operating System 타입, W Windows, M MAC, A Android (Galaxy 등), I iOS (iPhone, iPad 등) 
	
	private String cancelUrl; // 취소, 닫기시 
	
}
