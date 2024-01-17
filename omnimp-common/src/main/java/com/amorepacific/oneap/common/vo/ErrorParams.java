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
 * Date   	          : 2020. 11. 27..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ ErrorParams.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 27.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class ErrorParams implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -3383035979703902749L;

	private String chCd;
	private String home;
	private String homeurl;
	private String chnCd;
	private String storeCd;
	private String storenm;
	private String user_id;
	private String channelName;
	private String redirectUri;
	private String offlineInitUrl;
	
	private String message;
	private String errormsg;
	private String errorcode;
	
}
