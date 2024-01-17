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
 * Author	          : hjw0228
 * Date   	          : 2020. 10. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.health.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * <pre>
 * com.amorepacific.oneap.auth.health.mapper 
 *    |_ HealthMapper.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 10. 21.
 * @version : 1.0
 * @author  : hjw0228
 */

@Mapper
public interface HealthMapper {
	
	int validationQuery();
}
