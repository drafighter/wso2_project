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
 * Date   	          : 2020. 8. 26..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.terms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amorepacific.oneap.api.v2.terms.mapper.TermsMapper;
import com.amorepacific.oneap.api.v2.terms.vo.TermsVo;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

/**
 * <pre>
 * com.amorepacific.oneap.auth.terms.service 
 *    |_ TermsService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 26.
 * @version : 1.0
 * @author  : takkies
 */
@Service
public class TermsService {

	@Autowired
	private TermsMapper termsMapper;
	
	public List<TermsVo> getCorpTerms(final TermsVo termsVo) {
		return this.termsMapper.getCorpTerms(termsVo);
	}
	
	public boolean existTerms(final Terms terms) {
		return this.termsMapper.existTerms(terms) > 0;
	}
	
	public boolean mergeTerms(final Terms terms) {
		return this.termsMapper.mergeTerms(terms) > 0;
	}
	
	public boolean insertTermHist(final Terms terms) {
		return this.termsMapper.insertTermHist(terms) > 0;
	}
	/**
	 * <pre>
	 * comment  : 전사약관동의 여부 
	 */
	public boolean hasCorpTermsAgree(final UmOmniUser umOmniUser) {
		
		if (StringUtils.isEmpty(umOmniUser.getIncsNo()) || "0".equals(umOmniUser.getIncsNo())) {
			return false;
		}
		return this.termsMapper.hasCorpTermsAgree(umOmniUser) > 0; // 1 이면 필수약관 동의 완료
	}
	
	/**
	 * <pre>
	 * comment  : 약관동의 여부
	 */
	public boolean hasTermsAgree(final UmOmniUser umOmniUser) {
		
		if (StringUtils.isEmpty(umOmniUser.getIncsNo()) || "0".equals(umOmniUser.getIncsNo())) {
			return false;
		}
		return this.termsMapper.hasTermsAgree(umOmniUser) > 0; // 1 이면 필수약관동의 완료
	}
	
}
