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
 * Date   	          : 2020. 10. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ ChannelPairs.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 10. 7.
 * @version : 1.0
 * @author : takkies
 */

public enum ChannelPairs {

	INNI("036", "006"), // 036 이니스프리쇼핑몰 006 이니스프리
	ESP("042", "016"), // 042 에스쁘아쇼핑몰 016 에스쁘아
	EST("099", ""), // 099 에스트라 쇼핑몰
	ECRIS("BAA","101"), // BTA 뷰티엔젤 101 eCris
	SPA("","076"), //076 설화수 SPA 오프라인
	ILLIYOON("106",""), // 106 일리윤
	OSULLOC("039", "012"), // 039 오설록온라인몰 012 오설록티하우스
	OSULLOC_DST("", "008"), // 008 백화점 오설록
	AP("AOK",""), // AOK 아모레퍼시픽 브랜드
	HERA("AFK",""), // AFK 헤라
	SUL("AEK",""), // AEK 설화수
	MAM("AHK",""), // AHK 마몽드
	PRI("MCK",""), // MCK 프리메라
	LAN("ADK",""), // ADK 라네즈
	IOPE("AGK",""), // AGK 아이오페
	VIT("VRK",""), // VRK 바이탈뷰티
	MIS("AJK",""), // AJK 미쟝셴
	RYO("MRK",""), // MRK 려
	HAP("MHK",""), // MHK 해피바스
	STE("DRK",""), // DRK 스테디
	CUS("107",""), // 107 CUSTOM.ME+
	SSG("202",""),  // 202 SSG
	APM("031",""),  // 031 APMall
	BP("030",""),  // 030 뷰티포인트
	BEREADY("111",""), //307 비레디몰
	ART("048","005"), //048 아리따움몰 ART 005 아리따움POS
	COUNSEL("070",""), //070 카운셀러몰
	AMA("AMA",""), //AMA APMA(미술관홍보시스템)
	NCOUNSEL("108",""), //108 디지털방판모객프로그램
	CUB("103",""), //103 바이탈뷰티
	TONWORK("307",""), //307	톤워크닷컴
	ETUDE("","017"), //017 에뛰드 POS
	SCON("","062"), //  062 면세점(식스콘)
	SULFSS("","077"), //  077 설화수 FSS
	AMOREYONG("","097") //  097 아모레 용산
	; 

	private String onlineCd;
	private String offlineCd;

	private ChannelPairs(final String onlineCd, final String offlineCd) {
		this.onlineCd = onlineCd;
		this.offlineCd = offlineCd;
	}

	public String getOnlineCd() {
		return this.onlineCd;
	}

	public String getOfflineCd() {
		return this.offlineCd;
	}

	public static String getOnlineCd(final String offlineCd) {
		// 오설록 백화점인 경우 오설록몰 채널코드 리턴
		if(OmniConstants.OSULLOC_DEPARTMENT_CHCD.equals(offlineCd)) {
			return OmniConstants.OSULLOC_CHCD;
		}
		
		ChannelPairs[] channelParis = ChannelPairs.values();
		for (ChannelPairs channelPair : channelParis) {
			if (channelPair.getOfflineCd().equals(offlineCd)) {
				return channelPair.getOnlineCd();
			}
		}

		return offlineCd;
	}

	public static String getOfflineCd(final String onlineCd) {
		ChannelPairs[] channelParis = ChannelPairs.values();
		for (ChannelPairs channelPair : channelParis) {
			if (channelPair.getOnlineCd().equals(onlineCd)) {
				return channelPair.getOfflineCd();
			}
		}
		return onlineCd;
	}
}
