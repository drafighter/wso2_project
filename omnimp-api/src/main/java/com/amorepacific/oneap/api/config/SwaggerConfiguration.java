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
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.amorepacific.oneap.api.exception.vo.ApiError;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.UuidUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.fasterxml.classmate.TypeResolver;
//import com.google.common.base.Optional;
// import com.google.common.collect.Lists;
//import com.google.common.collect.Ordering;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.builders.ResponseBuilder;
//import springfox.documentation.builders.ResponseMessageBuilder;
//import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
// import springfox.documentation.service.ApiKey;
import springfox.documentation.service.ApiListingReference;
// import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Operation;
//import springfox.documentation.service.Parameter;
//import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.Response;
import springfox.documentation.service.Server;
//import springfox.documentation.service.ResponseMessage;
// import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
// import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <pre>
 * com.apmorepacific.oneap.api.config 
 *    |_ SwaggerConfiguration.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */
@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration implements WebMvcConfigurer {

	private static final String VERSION1 = "v1";
	private static final String VERSION2 = "v2";
	private static final String VERSION3 = "v3";
	private static final String VERSION4 = "v4";

	@Autowired
	private TypeResolver typeResolver;

	private ConfigUtil config = ConfigUtil.getInstance();

	@Value("${omni.api.domain}")
	private String omniApiDomain;

	@Value("${server.port}")
	private int serverPort;

	@Value("${spring.profiles.active}")
	private String profile;

	@Profile({ "local", "dev", "stg", "prod" })
	@Bean
	public Docket apiV1(ServletContext servletContext) throws MalformedURLException {
        Server hostServer = new Server(profile, profile.equals("local")?"http://"+getSwaggerHost():"https://"+getSwaggerHost(), "for"+profile, Collections.emptyList(), Collections.emptyList());
        Predicate<String> paths = profile.equals("local")?PathSelectors.ant("/" + VERSION1 + "/**"):PathSelectors.ant("/api/" + VERSION1 + "/**");
		return new Docket(DocumentationType.OAS_30) //
				.servers(hostServer) //20230315 버전 변경으로 추가
				.enable(true) //
				.groupName("oneap-api-" + VERSION1) //
				.select()//
				.apis(RequestHandlerSelectors.basePackage("com.amorepacific.oneap.api." + VERSION1)) //
				// .apis(RequestHandlerSelectors.withClassAnnotation(io.swagger.annotations.Api.class))
				.paths(paths) //
				.build()//
				.consumes(new HashSet<String>(Arrays.asList(MediaType.APPLICATION_JSON_VALUE))) //
				.produces(new HashSet<String>(Arrays.asList(MediaType.APPLICATION_JSON_VALUE))) //
				// .genericModelSubstitutes(Optional.class) // add this
				.genericModelSubstitutes(ResponseEntity.class).directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class) //
				.directModelSubstitute(java.time.ZonedDateTime.class, Date.class) //
				.directModelSubstitute(java.time.LocalDateTime.class, Date.class) //
				// .securityContexts(Lists.newArrayList(securityContext())) //
				// .securitySchemes(Lists.newArrayList(apiKey()))
				.apiListingReferenceOrdering(new Comparator<ApiListingReference>() {
					public int compare(ApiListingReference left, ApiListingReference right) {
						return left.getPosition() >= right.getPosition() ? -1 : 1;
					}
				}).operationOrdering(new Comparator<Operation>() {
					public int compare(Operation left, Operation right) {
						return left.getPosition() >= right.getPosition() ? -1 : 1;
					}
				}).apiInfo(apiInfo(VERSION1))//
				.additionalModels(typeResolver.resolve(ApiError.class)) //
				.globalRequestParameters(getGlobalParameters()) //
				.globalResponses(HttpMethod.GET, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.POST, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.PUT, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.DELETE, getGlobalResponseMessage()) //
				.useDefaultResponseMessages(false); //
//				.host(getSwaggerHost()); // 버전 변경으로 삭제

	}

	@Profile({ "local", "dev", "stg", "prod" })
	@Bean
	public Docket apiV2(ServletContext servletContext) {
		Server hostServer = new Server(profile, profile.equals("local")?"http://"+getSwaggerHost():"https://"+getSwaggerHost(), "for"+profile, Collections.emptyList(), Collections.emptyList());
		Predicate<String> paths = profile.equals("local")?PathSelectors.ant("/" + VERSION2 + "/**"):PathSelectors.ant("/api/" + VERSION2 + "/**");
		return new Docket(DocumentationType.OAS_30) //
				.servers(hostServer) //20230315 버전 변경으로 추가
				.enable(true) //
				.groupName("oneap-api-" + VERSION2) //
				.select()//
				.apis(RequestHandlerSelectors.basePackage("com.amorepacific.oneap.api." + VERSION2)) //
				.paths(paths) //
				.build()//
				.consumes(new HashSet<String>(Arrays.asList(MediaType.APPLICATION_JSON_VALUE))) //
				.produces(new HashSet<String>(Arrays.asList(MediaType.APPLICATION_JSON_VALUE))) //
				.genericModelSubstitutes(Optional.class) // add this
				.apiListingReferenceOrdering(new Comparator<ApiListingReference>() {
					public int compare(ApiListingReference left, ApiListingReference right) {
						return left.getPosition() >= right.getPosition() ? -1 : 1;
					}
				}).operationOrdering(new Comparator<Operation>() {
					public int compare(Operation left, Operation right) {
						return left.getPosition() >= right.getPosition() ? -1 : 1;
					}
				}).apiInfo(apiInfo(VERSION2))//
				.additionalModels(typeResolver.resolve(ApiError.class)) //
				// .globalOperationParameters(getGlobalParameters()) //X-API-KEY 셋팅
				.globalResponses(HttpMethod.GET, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.POST, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.PUT, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.DELETE, getGlobalResponseMessage()) //
				.useDefaultResponseMessages(false) //
				.host(getSwaggerHost()); //

	}

	@Profile({ "local", "dev", "stg", "prod" })
	@Bean
	public Docket apiV3(ServletContext servletContext) {
		Server hostServer = new Server(profile, profile.equals("local")?"http://"+getSwaggerHost():"https://"+getSwaggerHost(), "for"+profile, Collections.emptyList(), Collections.emptyList());
		Predicate<String> paths = profile.equals("local")?PathSelectors.ant("/" + VERSION3 + "/**"):PathSelectors.ant("/api/" + VERSION3 + "/**");
		return new Docket(DocumentationType.OAS_30) //
				.servers(hostServer) //20230315 버전 변경으로 추가
				.enable(true) //
				.groupName("oneap-api-" + VERSION3) //
				.select()//
				.apis(RequestHandlerSelectors.basePackage("com.amorepacific.oneap.api." + VERSION3)) //
				.paths(paths) //
				.build()//
				.consumes(new HashSet<String>(Arrays.asList(MediaType.APPLICATION_JSON_VALUE))) //
				.produces(new HashSet<String>(Arrays.asList(MediaType.APPLICATION_JSON_VALUE))) //
				.genericModelSubstitutes(Optional.class) // add this
				.apiListingReferenceOrdering(new Comparator<ApiListingReference>() {
					public int compare(ApiListingReference left, ApiListingReference right) {
						return left.getPosition() >= right.getPosition() ? -1 : 1;
					}
				}).operationOrdering(new Comparator<Operation>() {
					public int compare(Operation left, Operation right) {
						return left.getPosition() >= right.getPosition() ? -1 : 1;
					}
				}).apiInfo(apiInfo(VERSION3))//
				.additionalModels(typeResolver.resolve(ApiError.class)) //
				.globalRequestParameters(getGlobalParameters()) // X-API-KEY 셋팅
				.globalResponses(HttpMethod.GET, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.POST, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.PUT, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.DELETE, getGlobalResponseMessage()) //
				.useDefaultResponseMessages(false) //
				.host(getSwaggerHost()); //

	}
	
	@Profile({ "local", "dev", "stg", "prod" })
	@Bean
	public Docket apiV4(ServletContext servletContext) {
		Server hostServer = new Server(profile, profile.equals("local")?"http://"+getSwaggerHost():"https://"+getSwaggerHost(), "for"+profile, Collections.emptyList(), Collections.emptyList());
		Predicate<String> paths = profile.equals("local")?PathSelectors.ant("/" + VERSION4 + "/**"):PathSelectors.ant("/api/" + VERSION4 + "/**");
		return new Docket(DocumentationType.OAS_30) //
				.servers(hostServer) //20230315 버전 변경으로 추가
				.enable(true) //
				.groupName("oneap-api-" + VERSION4) //
				.select()//
				.apis(RequestHandlerSelectors.basePackage("com.amorepacific.oneap.api." + VERSION4)) //
				.paths(paths) //
				.build()//
				.consumes(new HashSet<String>(Arrays.asList(MediaType.APPLICATION_JSON_VALUE))) //
				.produces(new HashSet<String>(Arrays.asList(MediaType.APPLICATION_JSON_VALUE))) //
				.genericModelSubstitutes(Optional.class) // add this
				.apiListingReferenceOrdering(new Comparator<ApiListingReference>() {
					public int compare(ApiListingReference left, ApiListingReference right) {
						return left.getPosition() >= right.getPosition() ? -1 : 1;
					}
				}).operationOrdering(new Comparator<Operation>() {
					public int compare(Operation left, Operation right) {
						return left.getPosition() >= right.getPosition() ? -1 : 1;
					}
				}).apiInfo(apiInfo(VERSION4))//
				.additionalModels(typeResolver.resolve(ApiError.class)) //
				.globalRequestParameters(getV4GlobalParameters()) // X-API-KEY 셋팅
				.globalResponses(HttpMethod.GET, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.POST, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.PUT, getGlobalResponseMessage()) //
				.globalResponses(HttpMethod.DELETE, getGlobalResponseMessage()) //
				.useDefaultResponseMessages(false) //
				.host(getSwaggerHost()); //

	}	

	/*
	 * @Profile({"stg"})
	 * 
	 * @Bean public Docket disabledApi(ServletContext servletContext) { return new
	 * Docket(DocumentationType.SWAGGER_2).enable(false).select().build(); }
	 */

	private List<Response> getGlobalResponseMessage() {
//		ModelRef modelRef = new ModelRef(ApiError.class.getSimpleName());
		List<Response> responseMessages = new ArrayList<Response>();
		Response message500 = new ResponseBuilder() //
				.code("500") //
				.description("Internal Server Error, Check Api Server process.") //
				// .responseModel(modelRef) //
				.build(); //

		Response message400 = new ResponseBuilder() //
				.code("400") //
				.description("Bad Request, Check Api Server pages.") //
				// .responseModel(modelRef) //
				.build();//

		Response message404 = new ResponseBuilder() //
				.code("404") //
				.description("Page Not Found, Check Api Server pages.") //
				// .responseModel(modelRef) //
				.build();//

		responseMessages.add(message500);
		responseMessages.add(message400);
		responseMessages.add(message404);

		return responseMessages;
	}

	/**
	 * Swagger API에 공통으로 적용될 필드를 정의한다.
	 * 
	 * @return
	 */
	private List<RequestParameter> getGlobalParameters() {
		
		List<RequestParameter> parameters = new ArrayList<>();
		// 1. X-API-KEY
		RequestParameter apiKey = new RequestParameterBuilder() //
				.name("X-API-KEY") //
				.description("발급된 API KEY(채널별 발급) : default value(IBweveYghKeE439odiNwcw==)") //
//				.modelRef(new ModelRef("string"))//
				.required(true)//
				.in("header")
				.query(q -> q.defaultValue(this.config.defaultApiKey())
                        .model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
//				.example(scalarExample)
//				.query(q -> q. model(m -> m. scalarModel(ScalarType.STRING)))
				// .defaultValue(this.config.defaultApiKey()) // pwd : oneap / code : api
				//.example()(example)
//		.order(0)
				.build();
		parameters.add(apiKey);

		// 2. jwt
//		Parameter jwt = new ParameterBuilder() //
//				.name("authorization") //
//				.description("authorization") //
//				.modelRef(new ModelRef("string"))//
//				.parameterType("header")//
//				.required(true)//
//				.defaultValue("xxxxxxxxxxxxxxxxxxxxxxxxxx") //
//				.order(0).build();
//		
//		parameters.add(jwt);		

		// 3. uuid
		RequestParameter trxUuid = new RequestParameterBuilder()//
				.name(OmniConstants.TRX_UUID)//
				.description("처리 트랜잭션 UUID")//
				// .modelRef(new ModelRef("string"))//
				.in("header")
				.required(false)//
				//.defaultValue(UuidUtil.getUuid()) //
				.query(q -> q.defaultValue(UuidUtil.getUuid())
                        .model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
				// .order(1)
				.build();

		parameters.add(trxUuid);

		return parameters;
	}
	
	/**
	 * Swagger API에 공통으로 적용될 필드를 정의한다.
	 * 
	 * @return
	 */
	private List<RequestParameter> getV4GlobalParameters() {
		
		List<RequestParameter> parameters = new ArrayList<>();
		// 1. stm-api-key
		RequestParameter apiKey = new RequestParameterBuilder() //
				.name("stm-api-key") //
				.description("발급된 API KEY(채널별 발급) : default value(3e6f3ef30c9311c7086582f5a7f015c3)") //
				.required(true)//
				.in("header")
				.query(q -> q.defaultValue(this.config.defaultApiKey())
                        .model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
				.build();
		parameters.add(apiKey);

		return parameters;
	}	

	private ApiInfo apiInfo(final String version) {
		return new ApiInfoBuilder() //
				.title("Amorepacific OMNIMP ONE-AP API Document") //
				.description("Amorepacific OMNIMP ONE-AP API " + version + "\n\n") //
				.contact(new Contact("Amorepacific OMNIMP ONE-AP", "https://one-ap.amorepacific.com", "one-ap@amorepacific.com")) //
				.version(version) //
				.termsOfServiceUrl("https://one-ap.amorepacific.com") //
				.license("Amorepacific OMNIMP ONE-AP Rest API") //
				.licenseUrl("https://one-ap.amorepacific.com") //
				.build();
	}

//	private SecurityContext securityContext() {
//		return SecurityContext.builder() //
//				.securityReferences(defaultAuth()) //
//				.forPaths(PathSelectors.any()) //
//				.build();
//	}
//	
//	private ApiKey apiKey() {
//		return new ApiKey("JWT", "Authorization", "header");
//	}

//	private List<SecurityReference> defaultAuth() {
//		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//		authorizationScopes[0] = authorizationScope;
//		return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
//	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui/**") //
				.addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");

		registry.addResourceHandler("/webjars/**") //
				.addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");

	}

	private String getSwaggerHost() {
		String swaggerHsot = "";
		try {
			URL url = new URL(omniApiDomain);
			String host = url.getHost();
			String port = url.getPort() == -1 ? "" : ":" + url.getPort();
			swaggerHsot = "local".equals(profile) ? "localhost:" + serverPort : host + port;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return swaggerHsot;
	}
}
