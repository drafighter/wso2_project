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
package com.amorepacific.oneap.common.check;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amorepacific.oneap.common.check.actor.CheckActor;
import com.amorepacific.oneap.common.check.actor.impl.IdCharCheckActor;
import com.amorepacific.oneap.common.check.actor.impl.IdCheckActor;
import com.amorepacific.oneap.common.check.actor.impl.PasswordCheckActor;

/**
 * <pre>
 * com.apmorepacific.oneap.common.check 
 *    |_ Checker.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class Checker {

	private static Map<String, CheckActor> mappings = new HashMap<>();

	static {
		mappings.put(CheckActor.Type.IDCHAR.name(), new IdCharCheckActor());
		mappings.put(CheckActor.Type.ID.name(), new IdCheckActor());
		mappings.put(CheckActor.Type.PASSWORD.name(), new PasswordCheckActor());
		mappings = Collections.unmodifiableMap(mappings);
	}

	private CheckActor.Type checkType;
	private String checkValue;
	private String compareValue;
	private String beforeValue;

	public static class Builder {
		private CheckActor.Type checkType;
		private String checkValue;
		private String compareValue;
		private String beforeValue;

		public Builder() {

		}

		public Builder(final CheckActor.Type checkType, final String checkValue, final String compareValue, final String beforeValue) {
			this.checkType = checkType;
			this.checkValue = checkValue;
			this.compareValue = compareValue;
			this.beforeValue = beforeValue;
		}

		public Builder checkType(final CheckActor.Type checkType) {
			this.checkType = checkType;
			return this;
		}

		public Builder checkValue(final String checkValue) {
			this.checkValue = checkValue;
			return this;
		}

		public Builder compareValue(final String compareValue) {
			this.compareValue = compareValue;
			return this;
		}
		
		public Builder beforeValue(final String beforeValue) {
			this.beforeValue = beforeValue;
			return this;
		}

		public Checker build() {
			return new Checker(this);
		}
	}

	private Checker(Builder builder) {
		this.checkType = builder.checkType;
		this.checkValue = builder.checkValue;
		this.compareValue = builder.compareValue;
		this.beforeValue = builder.beforeValue;
	}

	/**
	 * 
	 * <pre>
	 * 	<code>
	 * 	CheckResponse response = new Checker.Builder() //
	 *			.checkType(CheckActor.Type.PASSWORD) //
	 *			.checkValue("test12345") //
	 *			.compareValue("test12345") //
	 *			.build() //
	 *			.check();
	 *	
	 *	log.debug("check response : {}", response.toString());
	 * </code> 
	 * commnet  : 사용자 아이디, 비밀번호에 대한 유효성 체크
	 * author   : takkies
	 * date     : 2020. 7. 9. 오전 11:54:24
	 * </pre>
	 * 
	 * @return CheckResponse 체크 결과
	 */
	public CheckResponse check() {
		CheckActor checkactor = mappings.get(this.checkType.name());
		return checkactor.check(this.checkValue, this.compareValue, this.beforeValue);
	}
	
}
