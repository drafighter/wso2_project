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
 * Date   	          : 2020. 8. 31..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.common.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ProcessVo;
import com.amorepacific.oneap.common.vo.ServiceVo;
import com.amorepacific.oneap.common.vo.TaskVo;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common.mapper 
 *    |_ CommonMapper.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 31.
 * @version : 1.0
 * @author  : takkies
 */
@Mapper
public interface CommonMapper {

	List<Channel> getChannels();
	
	Channel getChannel(final String chCd);
	
	int insertServiceLog(final ServiceVo serviceVo);
	
	int insertProcessLog(final ProcessVo processVo);
	
	int insertProcessTask(final Map<String, List<TaskVo>> taskVos);
}
