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
 * Date   	          : 2020. 7. 9..
 * Description 	  : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.mask;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.mask.actor.MaskActor;
import com.amorepacific.oneap.common.mask.actor.impl.AddressMaskActor;
import com.amorepacific.oneap.common.mask.actor.impl.BankMaskActor;
import com.amorepacific.oneap.common.mask.actor.impl.BirthMaskActor;
import com.amorepacific.oneap.common.mask.actor.impl.CreditMaskActor;
import com.amorepacific.oneap.common.mask.actor.impl.EmailMaskActor;
import com.amorepacific.oneap.common.mask.actor.impl.MobileMaskActor;
import com.amorepacific.oneap.common.mask.actor.impl.PhoneMaskActor;
import com.amorepacific.oneap.common.mask.actor.impl.UserIdMaskActor;
import com.amorepacific.oneap.common.mask.actor.impl.UserNameMaskActor;

/**
 * <pre>
 * com.apmorepacific.oneap.common.mask 
 *    |_ Masker.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */
public class Masker {

	private static Map<String, MaskActor> mappings = new HashMap<>();

	static {
		mappings.put(MaskActor.Type.USERNAME.name(), new UserNameMaskActor());
		mappings.put(MaskActor.Type.USERID.name(), new UserIdMaskActor());
		mappings.put(MaskActor.Type.EMAIL.name(), new EmailMaskActor());
		mappings.put(MaskActor.Type.MOBILE.name(), new MobileMaskActor());
		mappings.put(MaskActor.Type.PHONE.name(), new PhoneMaskActor());
		mappings.put(MaskActor.Type.ADDRESS.name(), new AddressMaskActor());
		mappings.put(MaskActor.Type.CREDIT.name(), new CreditMaskActor());
		mappings.put(MaskActor.Type.BANK.name(), new BankMaskActor());
		mappings.put(MaskActor.Type.BIRTH.name(), new BirthMaskActor());
		mappings = Collections.unmodifiableMap(mappings);

	}

	private MaskActor.Type maskType;
	private String maskValue;
	private String countryCode;

	public static class Builder {
		private MaskActor.Type maskType;
		private String maskValue;
		private String countryCode;


		public Builder() {
		}

		/**
		 * 
		 * @param maskType
		 * @param maskValue
		 * @param countryCode
		 */
		public Builder(final MaskActor.Type maskType, final String maskValue, final String countryCode) {
			this.maskType = maskType;
			this.maskValue = maskValue;
			this.countryCode = countryCode;
		}

		/**
		 * 
		 * @param maskType
		 * @return
		 */
		public Builder maskType(final MaskActor.Type maskType) {
			this.maskType = maskType;
			return this;
		}

		/**
		 * 
		 * @param maskValue
		 * @return
		 */
		public Builder maskValue(final String maskValue) {
			this.maskValue = maskValue;
			return this;
		}

		/**
		 * 
		 * @param countryCode
		 * @return
		 */
		public Builder countryCode(final String countryCode) {
			this.countryCode = countryCode;
			return this;
		}

		/**
		 * 
		 * @return
		 */
		public Masker build() {
			return new Masker(this);
		}

	}

	public Masker(final Builder builder) {
		this.maskType = builder.maskType;
		this.maskValue = builder.maskValue;
		if (StringUtils.isEmpty(builder.countryCode)) {
			this.countryCode = Locale.KOREA.getCountry();
		} else {
			this.countryCode = builder.countryCode;
		}
	}

	/**
	 * 
	 * <pre>
	 *  <code>
	 *  String result = new Masker.Builder().maskType(MaskActor.Type.EMAIL) //
	 *    .maskValue("beyondoubt@hotmail.com") //
	 *    .build().masking();	 
	 *  log.debug("masking result : {}", result);
	 *  </code>
	 * comment  : 타입별 마스킹 처리
	 * author   : takkies
	 * date     : 2020. 7. 9. 오후 12:12:20
	 * </pre>
	 * 
	 * @return 마스킹된 문자열
	 */
	public String masking() {
		MaskActor maskactor = mappings.get(this.maskType.name());
		return maskactor.masking(this.maskValue, this.countryCode);
	}
}
