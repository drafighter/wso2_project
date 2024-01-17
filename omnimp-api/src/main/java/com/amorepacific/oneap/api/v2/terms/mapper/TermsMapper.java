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
package com.amorepacific.oneap.api.v2.terms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.amorepacific.oneap.api.v2.terms.vo.TermsVo;
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
	
	int existTerms(final Terms terms);
	
	int mergeTerms(final Terms terms);
	
	int insertTermHist(final Terms terms);
	
	int hasCorpTermsAgree(final UmOmniUser umOmniUser);
	
	int hasTermsAgree(final UmOmniUser umOmniUser);

}
