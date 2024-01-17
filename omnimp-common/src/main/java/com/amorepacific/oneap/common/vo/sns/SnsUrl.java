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
 * Date   	          : 2020. 9. 16..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sns;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.channel.vo 
 *    |_ SnsConstants.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 16.
 * @version : 1.0
 * @author : takkies
 */

public class SnsUrl {

	public static final String KA_AUTH = "https://kauth.kakao.com/oauth/authorize?client_id={{app_key}}&redirect_uri={{redirect_uri}}&response_type=code";
	public static final String KA_NEW_TOKEN = "https://kauth.kakao.com/oauth/token";
	public static final String KA_NOW_TOKEN = "https://kapi.kakao.com/v1/user/access_token_info";
	public static final String KA_REFRESH_TOKEN = "https://kauth.kakao.com/oauth/token";
	public static final String KA_PROFILE = "https://kapi.kakao.com/v2/user/me";
	public static final String KA_LOGOUT = "https://kapi.kakao.com/v1/user/logout";
	public static final String KA_UNLINK = "https://kapi.kakao.com/v1/user/unlink";
	public static final String KA_TERMS = "https://kapi.kakao.com/v1/user/service/terms";
	public static final String KA_PLUSFRIENDS = "https://kapi.kakao.com/v1/api/talk/plusfriends";

	public static final String FB_AUTH = "https://www.facebook.com/dialog/oauth?client_id={{app_key}}&redirect_uri={{redirect_uri}}";
	public static final String FB_NEW_TOKEN = "https://graph.facebook.com/oauth/access_token?grant_type=authorization_code&client_id={{app_key}}&client_secret={{secret_key}}&redirect_uri={{redirect_uri}}&code={{code}}";
	public static final String FB_PROFILE = "https://graph.facebook.com/me?fields=id,name,email,birthday,age_range,gender"; // + ?access_token={{access_token}} 필요한지 좀더 봐야함
	public static final String FB_UNLINK = "https://graph.facebook.com/{{user_id}}/permissions"; // delete method
	
	public static final String NA_AUTH = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id={{app_key}}&redirect_uri={{redirect_uri}}&state={{state}}";
	public static final String NA_NEW_TOKEN = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id={{app_key}}&client_secret={{secret_key}}&redirect_uri={{redirect_uri}}&code={{code}}&state={{state}}";
	public static final String NA_REFRESH_TOKEN = "https://nid.naver.com/oauth2.0/token?grant_type=refresh_token&client_id={{app_key}}&client_secret={{secret_key}}&refresh_token={{refresh_token}}";
	public static final String NA_PROFILE = "https://openapi.naver.com/v1/nid/me";
	public static final String NA_UNLINK = "https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id={{app_key}}&client_secret={{secret_key}}&service_provider=NAVER&access_token={{access_token}}";
	
	public static final String AP_PUBLIC_KEY = "https://appleid.apple.com/auth/keys";

}
