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
 * Date   	          : 2020. 9. 11..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.wso2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.wso2.service 
 *    |_ Wso2CmmsSoapApiService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 11.
 * @version : 1.0
 * @author  : hjw0228
 */

@Service
public class Wso2CmmsSoapApiService extends Wso2ApiCommonService {
	
	@Value("${wso2.identityserverendpointcontexturl}")
	private String identityserverendpointcontexturl;
	
	private static final String WSO2_CLAIM_METADATA_MGT = "/services/ClaimMetadataManagementService";
	
	private static final String getExternalClaimsSoapAction = "getExternalClaims";
	private static final String getLocalClaimsSoapAction = "getLocalClaims";

	public ResponseEntity<String> getOIDCDialectNodes() throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_CLAIM_METADATA_MGT);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:xsd=\"http://org.apache.axis2/xsd\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <xsd:getExternalClaims>\n" +
                        "         <xsd:externalClaimDialectURI>http://wso2.org/oidc/claim</xsd:externalClaimDialectURI>\n" +
                        "      </xsd:getExternalClaims>\n" +
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponseToXML(soapUrl, soapBody, getExternalClaimsSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> getLocalClaimNodes() throws Exception {
		String soapUrl = identityserverendpointcontexturl.concat(WSO2_CLAIM_METADATA_MGT);
		String soapBody =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"" +
                        " xmlns:xsd=\"http://org.apache.axis2/xsd\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        "      <xsd:getLocalClaims/>\n" +
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

		ResponseEntity<String> response = null;

		try {
			response = getSoapResponseToXML(soapUrl, soapBody, getLocalClaimsSoapAction);
			
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
