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
package com.amorepacific.oneap.api.v1.wso2.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2CmmsSoapApiService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.wso2.web 
 *    |_ Wso2ClaimMetadataMgtController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 11.
 * @version : 1.0
 * @author  : hjw0228
 */

@RestController
@Api(tags = { "API v1  > wso2 > Claim Metadata API" }, hidden = true)
@ApiIgnore
@RequestMapping("/v1/ClaimMetadataManagementService")
public class Wso2ClaimMetadataMgtController {

	@Autowired
	Wso2CmmsSoapApiService wso2CmmsSoapApiService;
	
	@ApiOperation( //
			value = "WSO2 Soap API Get OIDCDialect Nodes", //
			notes = "WSO2 Soap API Get OIDCDialect Nodes", //
			httpMethod = "GET", //
			produces = "application/xml", //
			response = ResponseEntity.class)
	@GetMapping("/OIDCDialectNodes")
	public ResponseEntity<String> getOIDCDialectNodes() throws ApiBusinessException, Exception {
		return wso2CmmsSoapApiService.getOIDCDialectNodes();
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Get Local Claim Nodes", //
			notes = "WSO2 Soap API Get Local Claim Nodes", //
			httpMethod = "GET", //
			produces = "application/xml", //
			response = ResponseEntity.class)
	@GetMapping("/localClaimNodes")
	public ResponseEntity<String> getLocalClaimNodes() throws ApiBusinessException, Exception {
		return wso2CmmsSoapApiService.getLocalClaimNodes();
	}
}
