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
 * Date   	          : 2020. 7. 13..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.config;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
//import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpProperties;
import org.springframework.boot.autoconfigure.http.HttpProperties.Encoding.Type;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
//import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
//import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
//import org.wso2.carbon.identity.application.authentication.endpoint.util.listener.AuthenticationEndpointContextListener;
//import org.wso2.carbon.ui.filters.cache.ContentTypeBasedCachePreventionFilter;

import com.amorepacific.oneap.auth.filter.AuthFilter;
import com.amorepacific.oneap.auth.filter.ContentTypeBasedCachePreventionFilter;
import com.amorepacific.oneap.auth.filter.OneApAuthenticationEndpointFilter;
import com.amorepacific.oneap.auth.interceptor.AuthLoggingInterceptor;
import com.amorepacific.oneap.auth.interceptor.SystemCheckInterceptor;
import com.amorepacific.oneap.common.filter.ExcludableOrderedCharacterEncodingFilter;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;

/**
 * <pre>
 * com.amorepacific.oneap.admin.config 
 *    |_ WebMvcConfiguration.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 13.
 * @version : 1.0
 * @author : takkies
 */
@Configuration
@ComponentScan("com.amorepacific.oneap.auth")
public class WebMvcConfiguration implements WebMvcConfigurer {

	public static final String LANG = "lang";

	@Value("${logging.filter}")
	private boolean isLoggingFilter;

	@Autowired
	private HttpProperties httpProperties;

	@Autowired
	@Qualifier("trxUuidGeneratorHandler")
	private HandlerInterceptor trxUuidGeneratorHandler;

//	@Override
//	public void configurePathMatch(PathMatchConfigurer configurer) {
//		configurer.setUseSuffixPatternMatch(false);
//		configurer.setUseRegisteredSuffixPatternMatch(true);
//	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(trxUuidGeneratorHandler);
		registry.addInterceptor(authLoggingInterceptor()) //
				.addPathPatterns("/**") //
				.excludePathPatterns("/images/**") //
				.excludePathPatterns("/js/**") //
				.excludePathPatterns("/css/**") //
				.excludePathPatterns("/login/**") //
				.excludePathPatterns("/error/**") //
				.excludePathPatterns("/fonts/**") //
				.excludePathPatterns("/favicon.ico");
		registry.addInterceptor(systemCheckInterceptor()).excludePathPatterns("/info-svc")
				.addPathPatterns("/**") //
				.excludePathPatterns("/images/**") //
				.excludePathPatterns("/js/**") //
				.excludePathPatterns("/css/**") //
				.excludePathPatterns("/login/**") //
				.excludePathPatterns("/error/**") //
				.excludePathPatterns("/fonts/**") //
				.excludePathPatterns("/favicon.ico");		
		
//		registry.addInterceptor(gaTaggingINterceptor())
//		.addPathPatterns("/login/**")
//		.addPathPatterns("/join/**");	
	}

	@Profile("!local")
	@Bean
	public CookieSerializer cookieSerializer() {
	    DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
	    // cookieSerializer.setUseHttpOnlyCookie(true);
	    // cookieSerializer.setUseSecureCookie(true);
	    // cookieSerializer.setSameSite("None");
	    return cookieSerializer;
	}
	
	@Bean
	public ServletContextInitializer clearJsession() {
		return new ServletContextInitializer() {

			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
				SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
				sessionCookieConfig.setHttpOnly(true);
				// sessionCookieConfig.setSecure(true);
			}
			
		};
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName(LANG);
		return localeChangeInterceptor;
	}

	@Bean
	public AuthLoggingInterceptor authLoggingInterceptor() {
		return new AuthLoggingInterceptor();
	}
	
	@Bean
	public SystemCheckInterceptor systemCheckInterceptor() {
		return new SystemCheckInterceptor();
	}
	
//	@Bean
//	public GaTaggingInterceptor gaTaggingINterceptor() {
//		return new GaTaggingInterceptor();
//	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		sessionLocaleResolver.setDefaultLocale(Locale.KOREA);
		return sessionLocaleResolver;
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/messages/message");
		messageSource.setDefaultEncoding("UTF-8"); // 기본 인코딩 설정
		messageSource.setCacheSeconds(60);
		messageSource.setUseCodeAsDefaultMessage(true); // 없는 메세지일 경우 예외를 발생시키는 대신 코드를 기본 메세지로 설정
		return messageSource;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
		registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/", "/templates/");
		registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/", "/static/images/") //
				.setCacheControl(CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic());
		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/", "/static/js/") //
				.setCacheControl(CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic());
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/", "/static/css/") //
				.setCacheControl(CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic());
		registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/", "/static/fonts/") //
				.setCacheControl(CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic());
		registry.addResourceHandler("/favicon.ico").addResourceLocations("/favicon.ico");
	}

//  Register Servlet
//	@Bean
//	public ServletRegistrationBean<DispatcherServlet> dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet) {
//		ServletRegistrationBean<DispatcherServlet> bean = new ServletRegistrationBean<>(dispatcherServlet);
//		
//		bean.addUrlMappings("/");
//		return bean;
//	}

	// Register Filter
	@Bean
	public FilterRegistrationBean<AuthFilter> authFilterBean() {
		FilterRegistrationBean<AuthFilter> filterRegistration = new FilterRegistrationBean<>();
		filterRegistration.setFilter(new AuthFilter());
		filterRegistration.setOrder(0);
		filterRegistration.addUrlPatterns("/*");
		filterRegistration.addInitParameter("loggingFilter", Boolean.toString(this.isLoggingFilter));
		return filterRegistration;
	}

	@Bean
	public FilterRegistrationBean<OneApAuthenticationEndpointFilter> authenticationEndpointFilterBean() {
		FilterRegistrationBean<OneApAuthenticationEndpointFilter> filterRegistration = new FilterRegistrationBean<>();
		filterRegistration.setFilter(new OneApAuthenticationEndpointFilter());
		filterRegistration.setOrder(1);
		filterRegistration.addUrlPatterns("/login", "/plogin", "/join");
		return filterRegistration;
	}

	@Bean
	public FilterRegistrationBean<XssEscapeServletFilter> filterRegistrationBean() {
		FilterRegistrationBean<XssEscapeServletFilter> filterRegistration = new FilterRegistrationBean<>();
		filterRegistration.setFilter(new XssEscapeServletFilter());
		filterRegistration.setOrder(1);
		filterRegistration.addUrlPatterns("/*");
		return filterRegistration;
	}

	@Bean
	public FilterRegistrationBean<ContentTypeBasedCachePreventionFilter> contentTypeBasedCachePreventionFilterBean() {
		FilterRegistrationBean<ContentTypeBasedCachePreventionFilter> filterRegistration = new FilterRegistrationBean<>();
		filterRegistration.setFilter(new ContentTypeBasedCachePreventionFilter());
		filterRegistration.setOrder(1);
		filterRegistration.addInitParameter("patterns", "text/html,application/json,plain/text");
		filterRegistration.addInitParameter("filterAction", "enforce");
		filterRegistration.addInitParameter("httpHeaders", "Cache-Control: no-store, no-cache, must-revalidate, private");
		filterRegistration.addUrlPatterns("*");
		return filterRegistration;
	}

	@Bean(name = "corsFilterRegistrationBean")
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin(CorsConfiguration.ALL);
		config.addAllowedHeader("Content-Type");
		config.addAllowedHeader("X-Requested-With");
		config.addAllowedHeader("accept");
		config.addAllowedHeader("Origin");
		config.addAllowedHeader("Access-Control-Request-Method");
		config.addAllowedHeader("Access-Control-Request-Headers");
		config.addAllowedMethod(HttpMethod.GET);
		config.addAllowedMethod(HttpMethod.POST);
		config.addAllowedMethod(HttpMethod.HEAD);
		config.addAllowedMethod(HttpMethod.OPTIONS);
		config.addAllowedMethod(HttpMethod.PUT);
		config.addAllowedMethod(HttpMethod.DELETE);

		source.registerCorsConfiguration("/**", config);

		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

//	@Bean
//	public CommonsRequestLoggingFilter requestLoggingFilter() {
//		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
//		loggingFilter.setIncludeHeaders(true);
//		loggingFilter.setIncludeQueryString(true);
//		loggingFilter.setIncludePayload(true);
//		loggingFilter.setMaxPayloadLength(100);
//		loggingFilter.setIncludeClientInfo(true);
//		loggingFilter.setBeforeMessagePrefix("Before : ");
//		loggingFilter.setBeforeMessageSuffix("");
//		loggingFilter.setAfterMessagePrefix("After : ");
//		loggingFilter.setAfterMessageSuffix("");
//		return loggingFilter;
//	}

	// Register ServletContextListener
//	@Bean
//	public ServletListenerRegistrationBean<ServletContextListener> listenerRegistrationBean() {
//		ServletListenerRegistrationBean<ServletContextListener> sevletRegistration = new ServletListenerRegistrationBean<>();
//		sevletRegistration.setListener(new JksTrustStoreLoaderListener());
//		return sevletRegistration;
//	}

//	@Bean
//	public ServletListenerRegistrationBean<ServletContextListener> authenticationEndpointContextListenerBean() {
//		ServletListenerRegistrationBean<ServletContextListener> sevletRegistration = new ServletListenerRegistrationBean<>();
//		sevletRegistration.setListener(new AuthenticationEndpointContextListener());
//		return sevletRegistration;
//	}

	@Bean
	@ConditionalOnMissingBean
	public CharacterEncodingFilter characterEncodingFilter() throws UnsupportedEncodingException {
		HttpProperties.Encoding encodning = this.httpProperties.getEncoding();
		ExcludableOrderedCharacterEncodingFilter filter = new ExcludableOrderedCharacterEncodingFilter("euc-kr");
		filter.setEncoding(encodning.getCharset().name());
		filter.setForceRequestEncoding(encodning.shouldForce(Type.REQUEST));
		filter.setForceResponseEncoding(encodning.shouldForce(Type.RESPONSE));

		// 아래 url은 euc-kr로 인코딩됨
		filter.addExcludePath("/cert/ipin-result"); // nice ipin
		filter.addExcludePath("/cert/kmcis-result");
		return filter;
	}

}
