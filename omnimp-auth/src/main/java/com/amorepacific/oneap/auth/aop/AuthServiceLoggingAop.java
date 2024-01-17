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
 * Date   	          : 2020. 8. 3..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.aop 
 *    |_ AuthLoggingAop.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 3.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Aspect
@Component
public class AuthServiceLoggingAop {

	@Pointcut("execution(* com.amorepacific.oneap..*Service.*(..))")
	public void serviceMethod() {
	}

	@Before("serviceMethod()")
	public void onBeforeLogging(JoinPoint joinPoint) {
//		Class<? extends Object> clazz = joinPoint.getTarget().getClass();
//		String opName = joinPoint.getSignature().getName();
//		String arguments = Arrays.toString(joinPoint.getArgs());
//		log.debug("### ---> @Before {}.{}({})", new Object[] { clazz.getName(), opName, arguments });
	}

	@AfterReturning(pointcut = "serviceMethod()", returning = "returnVal")
	public void onAfterLogging(JoinPoint joinPoint, Object returnVal) {
//		Class<? extends Object> clazz = joinPoint.getTarget().getClass();
//		String opName = joinPoint.getSignature().getName();
//		String arguments = Arrays.toString(joinPoint.getArgs());
//		if (returnVal == null) {
//			log.debug("### ---> @AfterReturning {}.{}({}) return void", new Object[] { clazz.getName(), opName, arguments });
//		} else {
//			log.debug("### ---> @AfterReturning {}.{}({}) return {}", new Object[] { clazz.getName(), opName, arguments, returnVal.toString() });
//		}
	}

	@AfterThrowing(pointcut = "serviceMethod()", throwing = "exception")
	public void doRecoveryActions(JoinPoint joinPoint, Throwable exception) {
		Signature signature = joinPoint.getSignature();
		String methodName = StringUtils.isEmpty(signature.getName())?"":signature.getName();
		String stuff = StringUtils.isEmpty(signature.toString())?"":signature.toString();
		String arguments = StringUtils.isEmpty(joinPoint.getArgs())?"":Arrays.toString(joinPoint.getArgs());
		log.error("▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶");
		log.error("▶▶▶▶▶▶ We have caught exception in method: {} with arguments {}\nand the full toString: {}\nthe exception is: {}", methodName, arguments, stuff, exception.getMessage());
		log.error("▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶");
	}
}
