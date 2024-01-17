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
 * com.amorepacific.oneap.common.vo 
 *    |_ ChannelVo.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 27.
 * @version : 1.0
 * @author : takkies
 */
@Data
public class Channel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1954925074909782378L;
	private String chCd; // ch_cd 경로구분코드 1 varchar 3 3
	private String chCdNm; // 경로구분코드명 2 varchar 50 50
	private String hmpgUrl; // 홈페이지URL 3 varchar 200 200
	private String pcNmbrOrdUrl; // 컴퓨터비회원주문URL
	private String mblNmbrOrdUrl; // 모바일비회원주문URL
	private String oflnChnYn; // 오프라인경로여부 5 varchar 1
	private String apiCallChnAthtVl; // API호출경로인증값 6 varchar 500
	private String kmcChnId; // 한국모바일인증경로ID 7 varchar 10 10
	private String kmcChnUrlCdVl; // 한국모바일인증경로URL코드값 8 varchar 7
	private String ipinSiteCdVl; // 아이핀사이트코드값 9 varchar 10 10
	private String kmcSmsCostAdmtId; // 한국모바일인증SMS비용정산ID 10 varchar 10
	private String chnLoutUrl; // 경로로그아웃URL
	private String prmsChnRdrcUrlLv; // 허용경로리다이렉션URL목록값 varchar 2000
	private String[] prmsChnRdrcUrlList; //허용경로리다이렉션URL리스트
}
