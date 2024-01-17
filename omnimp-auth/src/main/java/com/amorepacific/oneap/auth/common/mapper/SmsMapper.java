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
 * Date   	          : 2020. 8. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.common.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.amorepacific.oneap.common.vo.sms.SmsRetryData;
import com.amorepacific.oneap.common.vo.sms.SmsVo;

/**
 * <pre>
 * com.amorepacific.oneap.common.sms.mapper 
 *    |_ SmsMapper.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 7.
 * @version : 1.0
 * @author  : takkies
 */
@Mapper
public interface SmsMapper {
	
	public int selectSmsMaxSeq();

	public SmsVo selectSmsData(SmsVo smsVo);
	
	public int insertSmsData(SmsVo smsVo);
	
	public int updateSmsDataFailCount(SmsVo smsVo);
	
	public int deleteSmsData(SmsVo smsVo);
	
	public SmsVo selectAuthSms(SmsVo smsVo);
	
	public int selectSmsFailCount(SmsVo smsVo);
	
	public SmsRetryData selectSmsRetryAvaiable(SmsVo smsVo);
	
	public int updateSmsAuth(SmsVo smsVo);
	
	public int deleteSmsNotUsedData(SmsVo smsVo);
}
