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
 * Author	          : jspark2
 * Date   	          : 2021. 1. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.sns.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsUnlinkResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsUnlinkVo;
import com.amorepacific.oneap.api.v1.mgmt.web.MgmtApiController;
import com.amorepacific.oneap.api.v1.sns.service.SnsCallbackApiService;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.vo.sns.SnsFacebookCallback;
import com.amorepacific.oneap.common.vo.sns.SnsFacebookInfo;
import com.amorepacific.oneap.common.vo.sns.SnsFacebookSignedRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.sns.web 
 *    |_ SnsCallbackApiController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 1. 21.
 * @version : 1.0
 * @author  : jspark2
 */
@RestController
@Slf4j
@RequestMapping("/v1/sns/callback")
public class SnsCallbackApiController {

	@Autowired
	private SnsCallbackApiService snsCallbackApiService;
	
	@Autowired
	private MgmtApiController mgmtApiController;
	
	@PostMapping("/rm/facebook/{snsType}")
	private ResponseEntity<SnsFacebookCallback> removeFacebookCallback(final SnsFacebookSignedRequest signedRequest, @PathVariable final String snsType) throws ApiBusinessException {
		
		log.debug("▶▶▶▶▶ [SNS Checker] signedRequest info : {}, snsType : {}", signedRequest.getSignedRequest(), snsType);
		
		// hash 검증 및 데이터 추출
		SnsFacebookInfo snsFacebookInfo = snsCallbackApiService.verificationFBCallbackRequest(signedRequest.getSignedRequest(), snsType);
		String confirmationCode = "fail";
		
		if(snsFacebookInfo.getIsValied()) {
			// sns id 값으로 사용자 정보 조회
			Map<String, String> sqlParam = new HashMap<>();
			sqlParam.put("snsType", snsType);
			sqlParam.put("snsId", snsFacebookInfo.getData().getUserId());
			
			SnsUnlinkVo snsUnlinkVo = this.snsCallbackApiService.getIDNAssociatedIdIncsNo(sqlParam);

			log.debug("▶▶▶▶▶ [SNS Checker] snsUnlinkVo info : {}", snsUnlinkVo);
			// sns unlink
			if(snsUnlinkVo != null) {
				SnsUnlinkResponse response = this.mgmtApiController.disconnectSnsAssociated(snsUnlinkVo);
				log.debug("◀◀◀◀◀◀ [detailSnsUnlink] response : {}", StringUtil.printJson(response));
				
				if(ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
					confirmationCode = "success";
				}
				else {
					confirmationCode = "fail";
				}
				
			}
			
			
		}
		snsFacebookInfo.getCallback().setUrl(snsFacebookInfo.getCallback().getUrl() + "/deletion?result=" + confirmationCode);
		snsFacebookInfo.getCallback().setConfirmationCode(confirmationCode);
		
		log.debug("▶▶▶▶▶▶ [removeFacebookCallback] url : {}", snsFacebookInfo.getCallback().getUrl());
		log.debug("▶▶▶▶▶▶ [removeFacebookCallback] confirmationCode : {}", snsFacebookInfo.getCallback().getConfirmationCode());
		
		ResponseEntity<SnsFacebookCallback> response = new ResponseEntity<>(snsFacebookInfo.getCallback(), HttpStatus.OK);

		return response;
	}
	
}
