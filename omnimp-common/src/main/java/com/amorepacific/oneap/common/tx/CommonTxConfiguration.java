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
package com.amorepacific.oneap.common.tx;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * <pre>
 * com.amorepacific.oneap.common.tx 
 *    |_ CommonTxConfiguration.java
 *    
 * Transaction 공통 Configuration
 * 상속한 후
 * @Aspect,	@Configuration 을 붙여줘서 활성화 한다. 
 *     
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 28.
 * @version : 1.0
 * @author  : takkies
 */

public class CommonTxConfiguration {

	// 30 seconds
	public static final int TX_METHOD_TIMEOUT = 30;
	
	@Autowired
	private DataSourceTransactionManager transactionManager;
	
	@Bean
	public TransactionInterceptor txAdvice() {
		TransactionInterceptor txAdvice = new TransactionInterceptor();
		Properties txAttributes = new Properties();
		List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
		rollbackRules.add(new RollbackRuleAttribute(Exception.class));

		
		DefaultTransactionAttribute readOnlyAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
		readOnlyAttribute.setReadOnly(true);
		readOnlyAttribute.setTimeout(TX_METHOD_TIMEOUT);

		RuleBasedTransactionAttribute writeAttribute = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED,  rollbackRules);
		writeAttribute.setTimeout(TX_METHOD_TIMEOUT);
		
		// @Transactional로 처리할 수 있으나 호출자에서 트랜잭션 구분이 모호하다고 하여 명칭으로 구분함.
		RuleBasedTransactionAttribute requiresNewAttribute = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRES_NEW,  rollbackRules);
		requiresNewAttribute.setTimeout(TX_METHOD_TIMEOUT);		
		
        RuleBasedTransactionAttribute nestedAttribute = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_NESTED,  rollbackRules);
        requiresNewAttribute.setTimeout(TX_METHOD_TIMEOUT);     

        txAttributes.setProperty("select*", readOnlyAttribute.toString());
        txAttributes.setProperty("*Nested*", nestedAttribute.toString());
        txAttributes.setProperty("*RequiresNewTransaction*", requiresNewAttribute.toString());
		txAttributes.setProperty("*", writeAttribute.toString());
		
		txAdvice.setTransactionAttributes(txAttributes);
		txAdvice.setTransactionManager(transactionManager);
		
		return txAdvice;
	}
	
	@Bean
	public Advisor txAdviceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(* com.amorepacific.oneap..*Service.*(..))");
		return new DefaultPointcutAdvisor(pointcut, txAdvice());
	}
}
