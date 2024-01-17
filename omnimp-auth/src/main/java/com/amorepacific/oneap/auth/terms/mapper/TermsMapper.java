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
 * Date   	          : 2020. 8. 20..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.terms.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipTermsVo;
import com.amorepacific.oneap.auth.social.vo.SnsTermsVo;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

/**
 * <pre>
 * com.amorepacific.oneap.auth.terms.mapper 
 *    |_ TermsMapper.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 20.
 * @version : 1.0
 * @author  : takkies
 */
@Mapper
public interface TermsMapper {
	
	List<TermsVo> getCorpTerms(final TermsVo termsVo);

	List<TermsVo> getTerms(final TermsVo termsVo);
	
	List<TermsVo> getTermsChoice(final TermsVo termsVo);
	
	List<TermsVo> getTermsByTags(final SnsTermsVo snsTermsVo);
	
	int existTerms(final Terms terms);
	
	int insertTerms(final Map<String, List<Terms>> joinTerm);
	
	int mergeTerms(final Terms terms);
	
	int insertTermsHist(final Map<String, List<Terms>> joinTerm);
	
	int insertTermHist(final Terms terms);
	
	int hasTermsAgree(final UmOmniUser umOmniUser);
	
	int hasCorpTermsAgree(final UmOmniUser umOmniUser);
	
	int deleteCustTerms(final UmOmniUser umOmniUser);
	
	int deleteCustCorpTerms(final UmOmniUser umOmniUser);
	
	List<String> getTermsTagList(final String chCd);
	
	List<NaverMembershipTermsVo> getNaverMembershipAgreeTerms(final Map<String, Object> paramMap);
	
	List<NaverMembershipTermsVo> getNaverMembershipAffiliateTerms(final List<String> afltChCdList);
}
