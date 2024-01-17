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
 * Author	          : hjw0228
 * Date   	          : 2020. 8. 18..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.wso2.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amorepacific.oneap.api.v1.wso2.mapper.Wso2ApiMapper;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2SoapApiClaimVo;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.wso2.service 
 *    |_ Wso2RusmSoapApiService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 18.
 * @version : 1.0
 * @author  : hjw0228
 */

@Service
public class Wso2RusmSoapApiService extends Wso2ApiCommonService {

	@Autowired
	private Wso2ApiMapper wso2ApiMapper;

	@Value("${wso2.identityserverendpointcontexturl}")
	private String identityserverendpointcontexturl;
	
	private static final String WSO2_REMOTE_USER_STORE_MANAGER = "/services/RemoteUserStoreManagerService";
	
	private static final String updateCredentialSoapAction = "updateCredential";
	private static final String updateCredentialByAdminSoapAction = "updateCredentialByAdmin";
	private static final String setUserClaimValuesSoapAction = "setUserClaimValues";
	
	public ResponseEntity<String> postPasswordByUmUserId(String umUserId, String oldPassword, String newPassword) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUsernameByUmUserId(umUserId);
		
		return this.postPasswordByUsername(username, oldPassword, newPassword);
	}
	
	public ResponseEntity<String> postPasswordByUsername(String username, String oldPassword, String newPassword) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <ser:updateCredential>\n" +
                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
                        "         <ser:newCredential><![CDATA[" + newPassword + "]]></ser:newCredential>\n" +
                        "         <ser:oldCredential><![CDATA[" + oldPassword + "]]></ser:oldCredential>\n" +
                        "      </ser:updateCredential>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, updateCredentialSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> patchPasswordByUmUserId(String umUserId, String newPassword) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUsernameByUmUserId(umUserId);
		
		return this.patchPasswordByUsername(username, newPassword);
	}
	
	public ResponseEntity<String> patchPasswordByUsername(String username, String newPassword) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <ser:updateCredentialByAdmin>\n" +
                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
                        "         <ser:newCredential><![CDATA[" + newPassword + "]]></ser:newCredential>\n" +
                        "      </ser:updateCredentialByAdmin>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, updateCredentialByAdminSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> postLockByUmUserId(String umUserId) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUsernameByUmUserId(umUserId);
		
		return this.postLockByUsername(username);
	}
	
	public ResponseEntity<String> postLockByUsername(String username) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\"" + 
                        " xmlns:xsd=\"http://common.mgt.user.carbon.wso2.org/xsd\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <ser:setUserClaimValues>\n" +
                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
                        "         <ser:claims>\n" + 
                        "            <xsd:claimURI>http://wso2.org/claims/identity/accountLocked</xsd:claimURI>\n" +
                        "            <xsd:value>true</xsd:value>\n" +
                        "            <xsd:claimURI>http://wso2.org/claims/identity/accountState</xsd:claimURI>\n" +
                        "            <xsd:value>LOCKED</xsd:value>\n" +
                        "         </ser:claims>\n" + 
                        "         <ser:profileName>default</ser:profileName>\n" +
                        "      </ser:setUserClaimValues>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, setUserClaimValuesSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> postUnLockByUmUserId(String umUserId) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUsernameByUmUserId(umUserId);
		
		return this.postUnLockByUsername(username);
	}
	
	public ResponseEntity<String> postUnLockByUsername(String username) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\"" +
                		" xmlns:xsd=\"http://common.mgt.user.carbon.wso2.org/xsd\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <ser:setUserClaimValues>\n" +
                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
                        "         <ser:claims>\n" + 
                        "            <xsd:claimURI>http://wso2.org/claims/identity/accountLocked</xsd:claimURI>\n" +
                        "            <xsd:value>false</xsd:value>\n" +
                        "            <xsd:claimURI>http://wso2.org/claims/identity/accountState</xsd:claimURI>\n" +
                        "            <xsd:value>UNLOCKED</xsd:value>\n" +
                        "         </ser:claims>\n" + 
                        "         <ser:profileName>default</ser:profileName>\n" +
                        "      </ser:setUserClaimValues>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, setUserClaimValuesSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	
	
	public ResponseEntity<String> postDisableByUmUserId(String umUserId) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUsernameByUmUserId(umUserId);
		
		return this.postDisableByUsername(username);
	}
	
	public ResponseEntity<String> postDisableByUsername(String username) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\"" +
                		" xmlns:xsd=\"http://common.mgt.user.carbon.wso2.org/xsd\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <ser:setUserClaimValues>\n" +
                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
                        "         <ser:claims>\n" + 
                        "            <xsd:claimURI>http://wso2.org/claims/identity/accountDisabled</xsd:claimURI>\n" +
                        "            <xsd:value>true</xsd:value>\n" +
                        "         </ser:claims>\n" + 
                        "         <ser:profileName>default</ser:profileName>\n" +
                        "      </ser:setUserClaimValues>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, setUserClaimValuesSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> postEnableByUmUserId(String umUserId) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUsernameByUmUserId(umUserId);
		
		return this.postEnableByUsername(username);
	}
	
	public ResponseEntity<String> postEnableByUsername(String username) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\"" +
                		" xmlns:xsd=\"http://common.mgt.user.carbon.wso2.org/xsd\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <ser:setUserClaimValues>\n" +
                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
                        "         <ser:claims>\n" + 
                        "            <xsd:claimURI>http://wso2.org/claims/identity/accountDisabled</xsd:claimURI>\n" +
                        "            <xsd:value>false</xsd:value>\n" +
                        "         </ser:claims>\n" + 
                        "         <ser:profileName>default</ser:profileName>\n" +
                        "      </ser:setUserClaimValues>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, setUserClaimValuesSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> postFailedLoginAttemptsBeforeSuccessByUmUserId(String umUserId, int cnt) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUsernameByUmUserId(umUserId);
		
		return this.postFailedLoginAttemptsBeforeSuccessByUsername(username, cnt);
	}
	
	public ResponseEntity<String> postFailedLoginAttemptsBeforeSuccessByUsername(String username, int cnt) throws Exception {
		Wso2SoapApiClaimVo wso2SoapApiClaimVo = new Wso2SoapApiClaimVo("http://wso2.org/claims/identity/failedLoginAttemptsBeforeSuccess", String.valueOf(cnt));
		
		return this.postUserClaimsByUsername(username, new ArrayList<Wso2SoapApiClaimVo>(Arrays.asList(wso2SoapApiClaimVo)));
	}
	
	public ResponseEntity<String> postUserClaimsByUmUserId(String umUserId, List<Wso2SoapApiClaimVo> wso2SoapApiClaimVoList) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUsernameByUmUserId(umUserId);
		
		return this.postUserClaimsByUsername(username, wso2SoapApiClaimVoList);
	}
	
	public ResponseEntity<String> postUserClaimsByUsername(String username, List<Wso2SoapApiClaimVo> wso2SoapApiClaimVoList) throws Exception {
		if(!wso2SoapApiClaimVoList.isEmpty()) {
			String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
			String soapBody =
	                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
	                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\"" +
	                		" xmlns:xsd=\"http://common.mgt.user.carbon.wso2.org/xsd\">\n" +
	                        "   <soap:Header/>\n" +
	                        "   <soap:Body>\n" +
	                        "      <ser:setUserClaimValues>\n" +
	                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
	                        "         <ser:claims>\n";
			for(Wso2SoapApiClaimVo wso2SoapApiClaimVo : wso2SoapApiClaimVoList) {
				soapBody += "            <xsd:claimURI>" + wso2SoapApiClaimVo.getClaimURI() + "</xsd:claimURI>\n" +
	                        "            <xsd:value>" + wso2SoapApiClaimVo.getValue() + "</xsd:value>\n"; 
			}
			soapBody +=     "         </ser:claims>\n" + 
	                        "         <ser:profileName>default</ser:profileName>\n" +
	                        "      </ser:setUserClaimValues>\n" + 
	                        "   </soap:Body>\n" +
	                        "</soap:Envelope>";

			ResponseEntity<String> response = null;

			try {
				response = getSoapResponse(soapUrl, soapBody, setUserClaimValuesSoapAction);
				
				return response;
			} catch (Exception e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}			
		} else {
			return new ResponseEntity<>("wso2SoapApiClaimVoList is empty", HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	public ResponseEntity<String> postLoginFailCountByUsername(String username, int failCount) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\"" + 
                        " xmlns:xsd=\"http://common.mgt.user.carbon.wso2.org/xsd\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <ser:setUserClaimValues>\n" +
                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
                        "         <ser:claims>\n" + 
                        "            <xsd:claimURI>http://wso2.org/claims/identity/failedLoginAttempts</xsd:claimURI>\n" +
                        "            <xsd:value>"+failCount+"</xsd:value>\n" +
                        "         </ser:claims>\n" + 
                        "         <ser:profileName>default</ser:profileName>\n" +
                        "      </ser:setUserClaimValues>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, setUserClaimValuesSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> postLoginFailaccountStateByUsername(String username) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\"" + 
                        " xmlns:xsd=\"http://common.mgt.user.carbon.wso2.org/xsd\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <ser:setUserClaimValues>\n" +
                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
                        "         <ser:claims>\n" + 
                        "            <xsd:claimURI>http://wso2.org/claims/identity/accountState</xsd:claimURI>\n" +
                        "            <xsd:value>LOCKED</xsd:value>\n" +
                        "         </ser:claims>\n" + 
                        "         <ser:profileName>default</ser:profileName>\n" +
                        "      </ser:setUserClaimValues>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, setUserClaimValuesSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> postLoginFailunlockTimeByUsername(String username, String unlocktime) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_REMOTE_USER_STORE_MANAGER);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:ser=\"http://service.ws.um.carbon.wso2.org\"" + 
                        " xmlns:xsd=\"http://common.mgt.user.carbon.wso2.org/xsd\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <ser:setUserClaimValues>\n" +
                        "         <ser:userName><![CDATA[" + username + "]]></ser:userName>\n" +
                        "         <ser:claims>\n" + 
                        "            <xsd:claimURI>http://wso2.org/claims/identity/unlockTime</xsd:claimURI>\n" +
                        "            <xsd:value>"+unlocktime+"</xsd:value>\n" +
                        "         </ser:claims>\n" + 
                        "         <ser:profileName>default</ser:profileName>\n" +
                        "      </ser:setUserClaimValues>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, setUserClaimValuesSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
