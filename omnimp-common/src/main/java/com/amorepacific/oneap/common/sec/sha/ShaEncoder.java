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
 * Date   	          : 2020. 7. 28..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.sec.sha;

import org.apache.commons.codec.digest.DigestUtils;

import com.amorepacific.oneap.common.sec.SecurityEncoder;

/**
 * <pre>
 * com.amorepacific.oneap.common.sec.sha 
 *    |_ ShaEncoder.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 28.
 * @version : 1.0
 * @author : takkies
 */

public class ShaEncoder implements SecurityEncoder {

	@Override
	public String encode(String passphrase, String plaintext) throws Exception {
		return DigestUtils.sha512Hex(plaintext);
	}

	@Override
	public String encode(String plaintext) throws Exception {
		return encode(null, plaintext);
	}

}
