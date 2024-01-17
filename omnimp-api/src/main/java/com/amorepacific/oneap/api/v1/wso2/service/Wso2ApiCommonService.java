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
 * Date   	          : 2020. 8. 3..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.wso2.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2RestApiGetUserVo;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.wso2.service 
 *    |_ Wso2ApiCommonService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 3.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Service
public class Wso2ApiCommonService {
	
	@Value("${wso2.adminuserid}")
	private String adminuserid;
	
	@Value("${wso2.adminuserpassword}")
	private String adminuserpassword;
	
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	protected HttpEntity<Object> createHttpEntity(String appType, String params) { 
		String userInfo = adminuserid + ":" + adminuserpassword; 
		String encodeAuth = "Basic " + Base64.getEncoder().encodeToString(userInfo.getBytes()); 
		HttpHeaders headers = new HttpHeaders(); 
		headers.add("Authorization", encodeAuth);
		headers.add("Content-Type", "application/json;charset=UTF-8"); // UTF-8을 넣어주지 않으면 한글이 깨짐 주의!!!
		headers.add("accept", appType);
		
		if(StringUtils.isEmpty(params)) {
			return new HttpEntity<Object>(headers);
		} else {
			return new HttpEntity<Object>(params, headers);
		}
	}
	
	protected Wso2RestApiGetUserVo generateGetUserFilter(Wso2RestApiGetUserVo wso2RestApiGetUserVo) {
		String filter = wso2RestApiGetUserVo.getFilter();
		
		if(!StringUtils.isEmpty(wso2RestApiGetUserVo.getUserName())) {
			filter = StringUtils.isEmpty(filter) ? "userName eq \"" + wso2RestApiGetUserVo.getUserName() + "\"" : "(" + filter + ")" + " and userName eq \"" + wso2RestApiGetUserVo.getUserName() + "\"";
		}
		
		if(!StringUtils.isEmpty(wso2RestApiGetUserVo.getUserName())) {
			filter = StringUtils.isEmpty(filter) ? "mobile eq \"" + wso2RestApiGetUserVo.getMobile() + "\"" : "(" + filter + ")" + " and mobile eq \"" + wso2RestApiGetUserVo.getMobile() + "\"";
		}
		
		/*
		 * if(!StringUtils.isEmpty(wso2RestApiGetUserVo.getUserCi())) { filter = StringUtils.isEmpty(filter) ? "userCi eq \"" +
		 * wso2RestApiGetUserVo.getUserCi() + "\"" : "(" + filter + ")" + " and userCi eq \"" + wso2RestApiGetUserVo.getUserCi() + "\""; }
		 */
		
		if(!StringUtils.isEmpty(wso2RestApiGetUserVo.getIncsNo())) {
			filter = StringUtils.isEmpty(filter) ? "incsNo eq \"" + wso2RestApiGetUserVo.getIncsNo() + "\"" : "(" + filter + ")" + " and incsNo eq \"" + wso2RestApiGetUserVo.getIncsNo() + "\"";
		}
		
		if(!StringUtils.isEmpty(wso2RestApiGetUserVo.getAccountState())) {
			filter = StringUtils.isEmpty(filter) ? "accountState eq \"" + wso2RestApiGetUserVo.getAccountState() + "\"" : "(" + filter + ")" + " and accountState eq \"" + wso2RestApiGetUserVo.getAccountState() + "\"";
		}
		
		if(!StringUtils.isEmpty(wso2RestApiGetUserVo.getAccountLocked())) {
			filter = StringUtils.isEmpty(filter) ? "accountLocked eq \"" + wso2RestApiGetUserVo.getAccountLocked() + "\"" : "(" + filter + ")" + " and accountLocked eq \"" + wso2RestApiGetUserVo.getAccountLocked() + "\"";
		}
		
		if(!StringUtils.isEmpty(wso2RestApiGetUserVo.getAccountDisabled())) {
			filter = StringUtils.isEmpty(filter) ? "accountDisabled eq \"" + wso2RestApiGetUserVo.getAccountDisabled() + "\"" : "(" + filter + ")" + " and accountDisabled eq \"" + wso2RestApiGetUserVo.getAccountDisabled() + "\"";
		}
		
		wso2RestApiGetUserVo.setFilter(filter);
		
		return wso2RestApiGetUserVo;
	}
	
	@SuppressWarnings("static-access")
	protected ResponseEntity<String> getSoapResponse(String soapUrl, String soapBody, String soapAction) {

		try {
			String userInfo = adminuserid + ":" + adminuserpassword; 
			String encodeAuth = "Basic " + Base64.getEncoder().encodeToString(userInfo.getBytes());
			
            final URL url = new URL(soapUrl);
            final HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8;action=\"urn:"+soapAction+"\"");
            urlConnection.setRequestProperty(
                    "Authorization", encodeAuth);
            // urlConnection.setRequestProperty("SOAPAction", soapAction);

            OutputStream outputStream = urlConnection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            //outputStream.write(soapBody.getBytes());
            outputStreamWriter.write(soapBody);
            outputStreamWriter.close();
            outputStream.close();

            urlConnection.connect();
            
            int responseCode = urlConnection.getResponseCode();
            if(responseCode > 400) {
            	InputStreamReader errorStreamReader = new InputStreamReader(urlConnection.getErrorStream());
            	BufferedReader errorReader = new BufferedReader(errorStreamReader);
            	
            	final StringBuilder errorResponseBuilder = new StringBuilder();
            	
                String line;
                while ((line = errorReader.readLine()) != null) {
                	errorResponseBuilder.append(line);
                }
                errorReader.close();
                if(errorResponseBuilder != null && !"".equals(errorResponseBuilder.toString().trim())) {
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.OMNI_API_WSO_SERVER_ERROR);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("WSO2 Identity Server Soap API Error = {}", errorResponseBuilder.toString());
                	log.debug("soapAction : " + soapAction);
                	log.debug("soapBody : \n" + soapBody);
                	LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
                	// return new ResponseEntity<>(errorResponseBuilder.toString(), responseCode);
                }
                return new ResponseEntity<>(errorResponseBuilder.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final StringBuilder soapResponseBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                soapResponseBuilder.append(line);
            }
            
            JSONObject jsonObject = new JSONObject();
            
            if(soapResponseBuilder != null && !"".equals(soapResponseBuilder.toString().trim())) {
                List<Node> nodes = new ArrayList<>();
                
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document xmlDocument = documentBuilder.parse(new ByteArrayInputStream(soapResponseBuilder.toString().getBytes()));
                
                // Extract "ns:return" tagged nodes
                NodeList nsReturnElements = xmlDocument.getElementsByTagName("ns:return");
                
                for (int i = 0; i < nsReturnElements.getLength(); i++) {
                	nodes.add(nsReturnElements.item(i));
                }
                
                if(nodes.isEmpty()) {
                	return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
                }
                
                jsonObject = XML.toJSONObject(this.nodesToString(nodes));
            } 
            
            return new ResponseEntity<String>(jsonObject.toString(PRETTY_PRINT_INDENT_FACTOR), HttpStatus.OK);
            
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	protected ResponseEntity<String> getSoapResponseToXML(String soapUrl, String soapBody, String soapAction) {

		try {
			String userInfo = adminuserid + ":" + adminuserpassword; 
			String encodeAuth = "Basic " + Base64.getEncoder().encodeToString(userInfo.getBytes());
			
            final URL url = new URL(soapUrl);
            final HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8;action=\"urn:"+soapAction+"\"");
            urlConnection.setRequestProperty(
                    "Authorization", encodeAuth);
            // urlConnection.setRequestProperty("SOAPAction", soapAction);

            OutputStream outputStream = urlConnection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            //outputStream.write(soapBody.getBytes());
            outputStreamWriter.write(soapBody);
            outputStreamWriter.close();
            outputStream.close();

            urlConnection.connect();
            
            int responseCode = urlConnection.getResponseCode();
            if(responseCode > 400) {
            	InputStreamReader errorStreamReader = new InputStreamReader(urlConnection.getErrorStream());
            	BufferedReader errorReader = new BufferedReader(errorStreamReader);
            	
            	final StringBuilder errorResponseBuilder = new StringBuilder();
            	
                String line;
                while ((line = errorReader.readLine()) != null) {
                	errorResponseBuilder.append(line);
                }
                errorReader.close();
                if(errorResponseBuilder != null && !"".equals(errorResponseBuilder.toString().trim())) {
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.OMNI_API_WSO_SERVER_ERROR);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("WSO2 Identity Server Soap API Error = {}", errorResponseBuilder.toString());
                	log.debug("soapAction : " + soapAction);
                	log.debug("soapBody : \n" + soapBody);
                	LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
                }
            }

            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final StringBuilder soapResponseBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                soapResponseBuilder.append(line);
            }
            
            return new ResponseEntity<String>(soapResponseBuilder.toString(), HttpStatus.OK);
            
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private static String nodeToString(Node node) throws Exception {
		StringWriter sw = new StringWriter();

		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.transform(new DOMSource(node), new StreamResult(sw));

		return sw.toString();
	}
	
	private static String nodesToString(List<Node> nodes) throws Exception {
		String result = "";
		for(Node node : nodes) {
			result += nodeToString(node);
		}
		
		return result;
	}
}
