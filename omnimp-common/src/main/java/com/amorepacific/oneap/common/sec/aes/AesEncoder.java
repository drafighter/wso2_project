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
 * Date   	          : 2020. 7. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.sec.aes;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.sec.SecurityEncoder;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.sec.aes 
 *    |_ AesEncoder.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 21.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
public class AesEncoder implements SecurityEncoder {
	private int keysize;
	private int iteration;
	private String salt; // 16 bit
	
	private String iv; // 32 bit
	
	private Cipher cipher = null;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	public AesEncoder() {
		this.iv = this.config.getString("security.aes.iv");
		this.salt = this.config.getString("security.aes.salt");
		this.keysize = this.config.getInt("security.aes.keysize", 128);
		this.iteration = this.config.getInt("security.aes.interation", 1000);
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			log.error(e.getMessage());
		}
	}
	
	@Override
	public String encode(String passphrase, String plaintext) throws Exception {
		if (StringUtils.isEmpty(passphrase)) {
			throw new IllegalArgumentException("passphrase must have value");
		}
		try {
			SecretKey key = SecurityUtil.generateAesKey(this.salt, passphrase, this.iteration, this.keysize);
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(SecurityUtil.hex(iv)));
			byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
			return SecurityUtil.base64(encrypted);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
		return "";
	}

	@Override
	public String encode(String plaintext) throws Exception {
		return encode(null, plaintext);
	}

}
