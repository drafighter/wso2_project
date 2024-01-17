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
 * Date   	          : 2020. 7. 14..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.RandomStringUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.util 
 *    |_ RandomUtil.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 14.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@UtilityClass
public class UuidUtil {
	private int iCount = (new SecureRandom()).nextInt();

	public enum Type {
		DATE, RANDOMUUID, ALPHANUMERIC
	}

	private int getCount() {
		synchronized (UuidUtil.class) {
			++iCount;
			iCount = (iCount >= 10000 || iCount < 0) ? 0 : iCount;
			return iCount;
		}
	}

	public String getIdByDate(final String prefix) {
		int count = getCount();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS", new Locale("ko", "KO"));
		String formattedValue = formatter.format(new Date());
		return prefix + formattedValue + count;
	}

	public String getUuidByDate() {
		String uuid = org.apache.commons.lang3.StringUtils.rightPad(getIdByDate(""), 21, "xyz");
		uuid = uuid.concat(RandomStringUtils.randomAlphabetic(11));
		return uuid;
	}

	public String getUuidByRandomUuid() {
		UUID guid = UUID.randomUUID();
		return guid.toString().replaceAll("-", "");
	}

	public String getUuidByAlphanumeric() {
		return RandomStringUtils.randomAlphanumeric(32);
	}

	public String getUuid() {
		return getUuid(Type.DATE);
	}

	public String getUuid(final UuidUtil.Type type) {
		if (type == Type.DATE) {
			return getUuidByDate();
		} else if (type == Type.RANDOMUUID) {
			return getUuidByRandomUuid();
		} else if (type == Type.ALPHANUMERIC) {
			return getUuidByAlphanumeric();
		}
		return getUuidByDate();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 27. 오후 4:59:47
	 * </pre>
	 * @return
	 */
	public String getVirtualIncsNo() {
		return "999" + RandomStringUtils.randomNumeric(6);
	}

	/**
	 * 
	 * <pre>
	 * comment  : OTP 생성하기
	 * author   : takkies
	 * date     : 2020. 8. 7. 오후 12:00:44
	 * </pre>
	 * @return
	 */
	public String getOtp() {
		return getOtp(0);
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 7. 오전 11:54:50
	 * </pre>
	 * 
	 * @param distance OTP 변경 주기 (sec)
	 * @return
	 */
	public String getOtp(final long distance) {
		return getOtp("oneap0626", distance);
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 7. 오후 12:00:40
	 * </pre>
	 * @param secretkey OTP 발생 시크릿키
	 * @param distance OTP 변경 주기 (sec)
	 * @return
	 */
	public String getOtp(final String secretkey, final long distance) {
		final String ALGORITHM = "HmacSHA1";
		final byte[] SECRET_KEY = secretkey.getBytes(); // "oneap0626"
		long ditanceSeconds = distance * 1000;
		byte[] data = new byte[8];
		long value = distance > 0 ? new Date().getTime() / ditanceSeconds : new Date().getTime();
		for (int i = 8; i-- > 0; value >>>= 8) {
			data[i] = (byte) value;
		}
		long truncatedHash = 0;
		try {
			Mac mac = Mac.getInstance(ALGORITHM);
			mac.init(new SecretKeySpec(SECRET_KEY, ALGORITHM));
			byte[] hash = mac.doFinal(data);
			int offset = hash[20 - 1] & 0xF;
			for (int i = 0; i < 4; ++i) {
				truncatedHash <<= 8;
				truncatedHash |= hash[offset + i] & 0xFF;
			}
			truncatedHash &= 0x7FFFFFFF;
			truncatedHash %= 1000000;
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidKeyException e) {
			log.error(e.getMessage(), e);
		}
		return String.format("%06d", truncatedHash);
	}
	


}
