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
 * Date   	          : 2020. 8. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.cert.vo.KmcisResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.icert.comm.secu.IcertSecuManager;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.util 
 *    |_ CertUtil.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 6.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@UtilityClass
public class CertUtil {

	public static final String NICE_ENCODE_DATA = "encodeData";
	
	public static final String KMCIS_EXPAND_VAR = "0000000000000000";

	public KmcisResult getKmcisData(final String certdata, final String certnum) {
		KmcisResult result = new KmcisResult(1, "");

		IcertSecuManager secumngr = new IcertSecuManager();

		// 1차 복호화, 수신된 certNum를 이용하여 복호화
		String decodedata = secumngr.getDec(certdata.trim(), certnum.trim());
		// 1차 파싱
		int inf1 = decodedata.indexOf("/", 0);
		int inf2 = decodedata.indexOf("/", inf1 + 1);
		final String encPara = decodedata.substring(0, inf1); // 암호화된 통합 파라미터
		final String encMsg1 = decodedata.substring(inf1 + 1, inf2); // 암호화된 통합 파라미터의 Hash값
		// 위변조 검증
		final String encMsg2 = secumngr.getMsg(encPara);
		if (encMsg2.equals(encMsg1)) {
			result.setStatus(1);
		} else {
			return new KmcisResult(-1, "비정상적인 접근");
		}
		//2차 복호화
		decodedata  = secumngr.getDec(encPara, certnum.trim());
		int info1 = decodedata.indexOf("/", 0);
		int info2 = decodedata.indexOf("/", info1 + 1);
		int info3 = decodedata.indexOf("/", info2 + 1);
		int info4 = decodedata.indexOf("/", info3 + 1);
		int info5 = decodedata.indexOf("/", info4 + 1);
		int info6 = decodedata.indexOf("/", info5 + 1);
		int info7 = decodedata.indexOf("/", info6 + 1);
		int info8 = decodedata.indexOf("/", info7 + 1);
		int info9 = decodedata.indexOf("/", info8 + 1);
		int info10 = decodedata.indexOf("/", info9 + 1);
		int info11 = decodedata.indexOf("/", info10 + 1);
		int info12 = decodedata.indexOf("/", info11 + 1);
		int info13 = decodedata.indexOf("/", info12 + 1);
		int info14 = decodedata.indexOf("/", info13 + 1);
		int info15 = decodedata.indexOf("/", info14 + 1);
		int info16 = decodedata.indexOf("/", info15 + 1);
		int info17 = decodedata.indexOf("/", info16 + 1);
		int info18 = decodedata.indexOf("/", info17 + 1);

		result.setCertNum(decodedata.substring(0, info1)); // 요청번호
		result.setDate(decodedata.substring(info1 + 1, info2)); // 요청일시
		result.setCi(decodedata.substring(info2 + 1, info3)); // 연계정보(CI)
		result.setPhoneNo(decodedata.substring(info3 + 1, info4)); // 휴대폰번호
		result.setPhoneCorp(decodedata.substring(info4 + 1, info5)); // 이동통신사
		result.setBirthDay(decodedata.substring(info5 + 1, info6)); // 생년월일
		result.setGender(decodedata.substring(info6 + 1, info7)); // 성별
		result.setNation(decodedata.substring(info7 + 1, info8)); // 내국인
		result.setName(decodedata.substring(info8 + 1, info9)); // 성명
		result.setResult(decodedata.substring(info9 + 1, info10)); // 결과값
		result.setCertMet(decodedata.substring(info10 + 1, info11)); // 인증방법
		result.setIp(decodedata.substring(info11 + 1, info12)); // ip주소
		result.setMName(decodedata.substring(info12 + 1, info13)); // 미성년자 성명
		result.setMBirthDay(decodedata.substring(info13 + 1, info14)); // 미성년자 생년월일
		result.setMGender(decodedata.substring(info14 + 1, info15)); // 미성년자 성별
		result.setMNation(decodedata.substring(info15 + 1, info16)); // 미성년자 내외국인
		result.setPlusInfo(decodedata.substring(info16 + 1, info17));
		result.setDi(decodedata.substring(info17 + 1, info18)); // 중복가입확인정보(DI)

		// CI, DI 복호화
		if (StringUtils.hasText(result.getCi())) {
			result.setCi(secumngr.getDec(result.getCi(), certnum));
		}
		if (StringUtils.hasText(result.getDi())) {
			result.setDi(secumngr.getDec(result.getDi(), certnum));
		}

		return result;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 5. 오후 5:07:25
	 * </pre>
	 * 
	 * @param value
	 * @param gubun
	 * @return
	 */
	public static String sanitizeNiceData(final String value, final String gubun) {
		String paramValue = value;
		if (StringUtils.hasText(value)) {
			paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

			paramValue = paramValue.replaceAll("\\*", "");
			paramValue = paramValue.replaceAll("\\?", "");
			paramValue = paramValue.replaceAll("\\[", "");
			paramValue = paramValue.replaceAll("\\{", "");
			paramValue = paramValue.replaceAll("\\(", "");
			paramValue = paramValue.replaceAll("\\)", "");
			paramValue = paramValue.replaceAll("\\^", "");
			paramValue = paramValue.replaceAll("\\$", "");
			paramValue = paramValue.replaceAll("'", "");
			paramValue = paramValue.replaceAll("@", "");
			paramValue = paramValue.replaceAll("%", "");
			paramValue = paramValue.replaceAll(";", "");
			paramValue = paramValue.replaceAll(":", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll("#", "");
			paramValue = paramValue.replaceAll("--", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll(",", "");

			if (!NICE_ENCODE_DATA.equals(gubun)) {
				paramValue = paramValue.replaceAll("\\+", "");
				paramValue = paramValue.replaceAll("/", "");
				paramValue = paramValue.replaceAll("=", "");
			}
		}
		return paramValue;
	}

	public void getQrCode(String url, HttpServletResponse response) throws UnsupportedEncodingException {
		final int qrCodeColor = 0xFF2e4e96; // 0xFF000001; // QRCODE 색상값(Dark moderate blue)
		final int qrBgColor = 0xFFFFFFFF;// QRCODE 배경색상값

		log.debug("local qr code generate url : {}", url.toString());

		Map<EncodeHintType, Object> encodingHints = new HashMap<EncodeHintType, Object>();
		encodingHints.put(EncodeHintType.MARGIN, 0);
		encodingHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
		try (OutputStream out = response.getOutputStream()) {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix matrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 450, 450, encodingHints);
			MatrixToImageConfig qrConfig = new MatrixToImageConfig(qrCodeColor, qrBgColor);
			response.setContentType("image/png; charset=UTF-8");
			response.setHeader("Content-Transfer-Encoding", "binary");
			MatrixToImageWriter.writeToStream(matrix, "png", out, qrConfig);
			out.flush();
		} catch (WriterException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
