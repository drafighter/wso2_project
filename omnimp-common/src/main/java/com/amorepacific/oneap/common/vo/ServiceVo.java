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
 * Date   	          : 2020. 11. 30..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ ServiceVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 30.
 * @version : 1.0
 * @author  : takkies
 */
@Getter
@Setter
public class ServiceVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 251470159908398269L;
	private long svcSeq;
	private String svcId;
	private String svcName;
	private String svcClass;
	private String svcMethod;
	private String svcType;
	private String svcError;
	private Timestamp svcDt;
	private Long svcExecuteDt;
	private String svcInput;
	private String svcOutput;
	private String svcResult;
	private String svcMsg;
}
