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
 * Date   	          : 2020. 8. 12..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.wso2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amorepacific.oneap.api.v1.wso2.mapper.Wso2ApiMapper;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2SoapApiCreateAssociatedVo;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2SoapApiMergeAssociatedVo;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.wso2.service 
 *    |_ Wso2UpmSoapApiService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 12.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Service
public class Wso2UpmSoapApiService extends Wso2ApiCommonService {

	@Autowired
	private Wso2ApiMapper wso2ApiMapper;

	@Value("${wso2.identityserverendpointcontexturl}")
	private String identityserverendpointcontexturl;
	
	private static final String WSO2_USER_PROFILE_MGT = "/services/UserProfileMgtService";
	
	private static final String getAssociatedIDsForUserSoapAction = "getAssociatedIDsForUser";
	private static final String associatedIDsForUserSoapAction = "associateIDForUser";
	private static final String removeAssociateIDSoapAction = "removeAssociateIDForUser";
	
	public ResponseEntity<String> getAssociatedIdsByUmUserId(String umUserId) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUmUserIdByUsername(umUserId);
		
		return this.getAssociatedIdsByUsername(username);
	}
	
	public ResponseEntity<String> getAssociatedIdsByUsername(String username) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_USER_PROFILE_MGT);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:mgt=\"http://mgt.profile.user.identity.carbon.wso2.org\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <mgt:getAssociatedIDsForUser>\n" +
                        "         <mgt:username><![CDATA[" + username + "]]></mgt:username>\n" +
                        "      </mgt:getAssociatedIDsForUser>\n" + 
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, getAssociatedIDsForUserSoapAction);
			
			log.info("response : " + response);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> postAssociatedIdsByUmUserId(String umUserId, Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUsernameByUmUserId(umUserId);
		
		return this.postAssociatedIdsByUsername(username, wso2SoapApiCreateAssociatedVo);
	}
	
	public ResponseEntity<String> postAssociatedIdsByUsername(String username, Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_USER_PROFILE_MGT);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:mgt=\"http://mgt.profile.user.identity.carbon.wso2.org\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <mgt:associateIDForUser>\n" +
                        "         <mgt:username><![CDATA[" + username + "]]></mgt:username>\n" +
                        "         <mgt:idpID>" + wso2SoapApiCreateAssociatedVo.getSnsIdpName() + "</mgt:idpID>\n" +
                        "         <mgt:associatedID>" + wso2SoapApiCreateAssociatedVo.getSnsUserId() + "</mgt:associatedID>\n" +
                        "      </mgt:associateIDForUser>\n" + 
                        "   </soap:Body>\n" +
                        "</soape:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, associatedIDsForUserSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> patchAssociatedIdsByUmUserId(String umUserId, Wso2SoapApiMergeAssociatedVo wso2SoapApiMergeAssociatedVo) throws Exception {
		
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUmUserIdByUsername(umUserId);
		
		Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo = new Wso2SoapApiCreateAssociatedVo(wso2SoapApiMergeAssociatedVo.getSnsIdpName(), wso2SoapApiMergeAssociatedVo.getAsisSnsUserId());
		
		// Check User Associated SNS Count
		int cnt = wso2ApiMapper.getUserAssociatedSnsCnt(wso2SoapApiMergeAssociatedVo);
				
		if(cnt > 0) {
			this.deleteAssociatedIdsByUsername(username, wso2SoapApiCreateAssociatedVo);
		}
		
		return this.postAssociatedIdsByUsername(username, wso2SoapApiCreateAssociatedVo);
	}
	
	public ResponseEntity<String> patchAssociatedIdsByUsername(String username, Wso2SoapApiMergeAssociatedVo wso2SoapApiMergeAssociatedVo) throws Exception {
		
		Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo = new Wso2SoapApiCreateAssociatedVo(wso2SoapApiMergeAssociatedVo.getSnsIdpName(), wso2SoapApiMergeAssociatedVo.getAsisSnsUserId());
		
		// Check User Associated SNS Count
		int cnt = wso2ApiMapper.getUserAssociatedSnsCnt(wso2SoapApiMergeAssociatedVo);
				
		if(cnt > 0) {
			this.deleteAssociatedIdsByUsername(username, wso2SoapApiCreateAssociatedVo);
		}
		
		wso2SoapApiCreateAssociatedVo.setSnsUserId(wso2SoapApiMergeAssociatedVo.getTobeSnsUserId());
		
		return this.postAssociatedIdsByUsername(username, wso2SoapApiCreateAssociatedVo);
	}
	
	public ResponseEntity<String> deleteAssociatedIdsByUmUserId(String umUserId, Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo) throws Exception {
		// Check username By User WSO2 ID(um_user_id)
		String username = wso2ApiMapper.getUmUserIdByUsername(umUserId);
		
		return this.deleteAssociatedIdsByUsername(username, wso2SoapApiCreateAssociatedVo);
	}
	
	public ResponseEntity<String> deleteAssociatedIdsByUsername(String username, Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo) throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_USER_PROFILE_MGT);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:mgt=\"http://mgt.profile.user.identity.carbon.wso2.org\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <mgt:removeAssociateIDForUser>\n" +
                        "         <mgt:username><![CDATA[" + username + "]]></mgt:username>\n" +
                        "         <mgt:idpID>" + wso2SoapApiCreateAssociatedVo.getSnsIdpName() + "</mgt:idpID>\n" +
                        "         <mgt:associatedID>" + wso2SoapApiCreateAssociatedVo.getSnsUserId() + "</mgt:associatedID>\n" +
                        "      </mgt:removeAssociateIDForUser>\n" + 
                        "   </soap:Body>\n" +
                        "</soape:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponse(soapUrl, soapBody, removeAssociateIDSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	
}
