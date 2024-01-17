package com.amorepacific.oneap.auth.ga.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amorepacific.oneap.auth.ga.OmniGaTaggingConstants;

/**
 * 클래스설명 : 
 * @version : 2022. 1. 18.
 * @author : kspark01
 * @분류 : 
 * omnimp-auth / package com.amorepacific.oneap.auth.common;
 */

/**
 * 1. ClassName : 
 * 2. FileName          : AsyncThreadConfig.java
 * 3. Package           : com.amorepacific.oneap.auth.common
 * 4. Commnet           : 
 * 5. 작성자                       : kspark01
 * 6. 작성일                       : 2022. 1. 18. 오전 9:56:18
 */
@Configuration
@EnableAsync
public class AsyncThreadConfig {

	/**
	 * 1. MethodName        : asyncTaggingThreadTaskExecutor
	 * 2. ClassName         : AsyncThreadConfig
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 1. 26. 오전 11:06:55
	 * @return Executor
	 * @return
	 */
	@Bean
    public Executor asyncTaggingThreadTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(OmniGaTaggingConstants.GA_CORE_POOL_SIZE);// 기본 스레드 수
        threadPoolTaskExecutor.setMaxPoolSize(OmniGaTaggingConstants.GA_MAX_POOL_SIZE);// 최대 스레드 수

        threadPoolTaskExecutor.setQueueCapacity(OmniGaTaggingConstants.GA_QUEUE_CAPACITY);// Queue 사이즈
        // 코어 스레드 타임아웃 허용 여부  true 설정할 경우 setCorePoolSize(0) , setMaxPoolSize(4), setQueueCapacity(10) 성능은  false 더 좋음 단 메모리 사용 
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(OmniGaTaggingConstants.GA_ALLOW_CORE_THREAD_TIME_OUT);
        threadPoolTaskExecutor.setKeepAliveSeconds(OmniGaTaggingConstants.GA_KEEPAlIVE_SECONDS); //thread 작업 최대 대기시간 설정 1초 ~ 3초로 조정
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());//오래된 작업을 skip 합니다.
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(OmniGaTaggingConstants.GA_WAIT_FOR_TASKS_TO_COMPLETE_SHUTDOWN); //시스템 다운 대기중 thread 처리 설정 false: 강제종료
//        threadPoolTaskExecutor.setAwaitTerminationSeconds(OmniGaTaggingConstants.GA_AWAIT_TERMINATION_SECONDS);	// shutdown 최대 1초 대기 setWaitForTasksToCompleteOnShutdown true인 경우 설정
        threadPoolTaskExecutor.setThreadNamePrefix(OmniGaTaggingConstants.GA_THREAD_NAME_PREFIX); //thread name preifx
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

	/**
	 * 1. MethodName        : asyncTaggingThreadTaskExecutor
	 * 2. ClassName         : AsyncThreadConfig
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 1. 26. 오전 11:06:55
	 * @return Executor
	 * @return
	 */
	@Bean
    public Executor asyncThreadTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(OmniGaTaggingConstants.GA_CORE_POOL_SIZE);// 기본 스레드 수
        threadPoolTaskExecutor.setMaxPoolSize(OmniGaTaggingConstants.GA_MAX_POOL_SIZE);// 최대 스레드 수
        threadPoolTaskExecutor.setQueueCapacity(OmniGaTaggingConstants.GA_QUEUE_CAPACITY);// Queue 사이즈
        // 코어 스레드 타임아웃 허용 여부  true 설정할 경우 setCorePoolSize(0) , setMaxPoolSize(4), setQueueCapacity(10) 성능은  false 더 좋음 단 메모리 사용 
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(OmniGaTaggingConstants.GA_ALLOW_CORE_THREAD_TIME_OUT);
        threadPoolTaskExecutor.setKeepAliveSeconds(OmniGaTaggingConstants.GA_KEEPAlIVE_SECONDS); //thread 작업 최대 대기시간 설정
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());//오래된 작업을 skip 합니다.
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(OmniGaTaggingConstants.GA_WAIT_FOR_TASKS_TO_COMPLETE_SHUTDOWN); //시스템 다운 대기중 thread 처리 설정 false: 강제종료
//        threadPoolTaskExecutor.setAwaitTerminationSeconds(OmniGaTaggingConstants.GA_AWAIT_TERMINATION_SECONDS);	// shutdown 최대 1초 대기 setWaitForTasksToCompleteOnShutdown true인 경우 설정
        threadPoolTaskExecutor.setThreadNamePrefix("omni-join-channel-rest-"); //thread name preifx
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }	
	
}
