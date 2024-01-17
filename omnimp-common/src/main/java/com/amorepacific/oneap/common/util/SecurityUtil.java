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
 * Date   	          : 2020. 7. 15..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.sec.SecurityDecoder;
import com.amorepacific.oneap.common.sec.SecurityEncoder;
import com.amorepacific.oneap.common.sec.SecurityFactory;
import com.amorepacific.oneap.common.vo.SSOParam;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.util 
 *    |_ SecurityUtil.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 15.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@UtilityClass
public class SecurityUtil {

	private final Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);

	private final Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL);

	private final Pattern[] xssPatterns = new Pattern[] { Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE), // Script fragments
			Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), // src='...'
			Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), // src='...'
			Pattern.compile("</script>", Pattern.CASE_INSENSITIVE), // lonely script tags
			Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), // lonely script tags
			Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), // eval(...)
			Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), // expression(...)
			Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE), // javascript:...
			Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE), // vbscript:...
			Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL) // onload(...)=...
	};

	private SecurityFactory securityfactory = SecurityFactory.getInstance();

	private ConfigUtil config = ConfigUtil.getInstance();

	/**
	 * 
	 * <pre>
	 * commnet  : 기본적인 XSS 정보에 대해서 변경(기본적인 XSS 처리에 사용)
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:11:24
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public String clearXSSNormal(final String data) {
		String xssstr = data;
		xssstr = StringUtils.replace(xssstr, "&", "&amp;");
		xssstr = StringUtils.replace(xssstr, "<", "&lt;");
		xssstr = StringUtils.replace(xssstr, ">", "&gt;");
		xssstr = StringUtils.replace(xssstr, "\"", "&#34;");
		xssstr = StringUtils.replace(xssstr, "'", "&#39;");
		xssstr = StringUtils.replace(xssstr, "%22", "-");
		xssstr = StringUtils.replace(xssstr, "%2C", "");
		xssstr = StringUtils.replace(xssstr, "%2", "");
		xssstr = StringUtils.replace(xssstr, "alert", "-");
		xssstr = StringUtils.replace(xssstr, "alert(", "-");
		xssstr = StringUtils.replace(xssstr, "onmouse", "-");
		xssstr = StringUtils.replace(xssstr, "iframe", "-");
		return xssstr;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 추가 XSS 정보에 대해서 변경(최대한의 XSS 처리에 사용)
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:11:50
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public String clearXSSAdditional(final String data) {
		String xssstr = data;
		// xssstr = StringUtils.replace(xssstr, "%00", null);
		xssstr = StringUtils.replace(xssstr, "%", "&#37;");
		xssstr = clearXSSFilePath(xssstr);
		xssstr = StringUtils.replace(xssstr, "./", "");
		xssstr = StringUtils.replace(xssstr, "%2F", "");
		return xssstr;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 파일경로(다운로드와 같은 경우)에 대한 XSS에 대해서 변경
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:12:19
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public String clearXSSFilePath(final String data) {
		String xssstr = data;
		xssstr = StringUtils.replace(xssstr, "../", "");
		xssstr = StringUtils.replace(xssstr, "..\\", "");
		return xssstr;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 에디터와 같은 형태에서 XSS 우회 부분에 대한 처리
	 * <code>
	 * onload="javascript:location.replace('http://www.naver.com')"
	 * "><script>alert(document.cookie)</script>
	 * <script>alert(document.cookie)</script>
	 * onload="javascript:location.replace('http://www.naver.com')" ">
	 * </code>
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:07:50
	 * </pre>
	 * 
	 * @param content
	 * @return
	 */
	public String replaceXssForEditor(final String content) {
		String xssstr = content;
		xssstr = xssstr.replaceAll("(?i)<script>", "&lt;script&gt;"); // @add 2013.11.21 에디터의 html 보기에서 강제 XSS 입력 시 처리
		xssstr = xssstr.replaceAll("(?i)</script>", "&lt;/script&gt;"); // @add 2013.11.21 에디터의 html 보기에서 강제 XSS 입력 시 처리
		xssstr = xssstr.replaceAll("(?i)javascript", "JAVA-SCRIPT"); // @add 2013.11.21 에디터의 html 보기에서 강제 XSS 입력 시 처리
		return xssstr.replaceAll("(?i)onload", "ON-LOAD");
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 본문(에디터) 정보에서 불필요한 정보(script, style) 삭제
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:10:35
	 * </pre>
	 * 
	 * @param content
	 * @return
	 */
	public String clearXssForEditor(final String content) {
		String xssstr = content;
		if (xssstr == null) {
			return null;
		}
		Matcher m = SCRIPTS.matcher(xssstr);
		xssstr = m.replaceAll("");
		m = STYLE.matcher(xssstr);
		xssstr = m.replaceAll("");
		return xssstr;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 문자열로 넘어온 정보 중 웹에서 실행 가능 형태의 문자열 제거(XSS)
	 * 해당 처리 후 clearXssNormal 이나 clearXSSAdditional을 사용하여 화면 출력용으로 사용<br>
	 * <code>
	 * String xss = "<script>document.write('<sc" + "cript>alert(1);</sc" + "ript>');alert(document.cookie);</script>";
	 * System.out.println(clearXssSimple(xss));
	 * </code>s 
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:14:07
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public String clearXssSimple(final String data) {
		String xssstr = data;
		return xssstr.replaceAll("(?i)<script.*?>.*?</script.*?>", "") // case 1
				.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "") // case 2
				.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", ""); // case 3

	}

	/**
	 * 
	 * <pre>
	 * commnet  : 문자열로 넘어온 정보 중 웹에서 실행 가능 형태의 문자열을 강하게 제거(XSS)
	 * 해당 처리 후 clearXssNormal 이나 clearXSSAdditional을 사용하여 화면 출력용으로 사용
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:16:10
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public String clearXssTight(final String data) {
		String xssstr = data;
		if (xssstr != null) {
			xssstr = xssstr.replaceAll("\0", "");// Avoid null characters
			for (Pattern pattern : xssPatterns) {// Remove all sections that match a pattern
				xssstr = pattern.matcher(xssstr).replaceAll("");
			}
		}
		return xssstr;
	}

	/**
	 * 
	 * <pre>
	 * comment  : SSO 파라미터 처리
	 * author   : takkies
	 * date     : 2020. 11. 9. 오후 2:41:49
	 * </pre>
	 * 
	 * @param ssoParam
	 * @return
	 */
	public SSOParam clearXssSsoParam(final SSOParam ssoParam) {
		if (ssoParam != null) {
			SSOParam param = ssoParam;
			param.setChannelCd(clearXSSNormal(clearXssCrlf(ssoParam.getChannelCd())));
			param.setClient_id(clearXSSNormal(clearXssCrlf(ssoParam.getClient_id())));
			param.setRedirectUri(clearXssTight(clearXssCrlf(ssoParam.getRedirectUri())));
			param.setCancelUri(clearXssTight(clearXssCrlf(ssoParam.getCancelUri())));
			param.setRedirect_uri(clearXssTight(clearXssCrlf(ssoParam.getRedirect_uri())));
			param.setResponse_type(clearXSSNormal(clearXssCrlf(ssoParam.getResponse_type())));
			param.setScope(clearXSSNormal(clearXssCrlf(ssoParam.getScope())));
			param.setState(clearXssSsoParamState(ssoParam.getState()));
			param.setType(clearXSSNormal(clearXssCrlf(ssoParam.getType())));
			param.setJoin(clearXSSNormal(clearXssCrlf(ssoParam.getJoin())));
			param.setVt(clearXSSNormal(clearXssCrlf(ssoParam.getVt())));
			param.setPopup(clearXSSNormal(clearXssCrlf(ssoParam.getPopup())));
			return param;
		}
		return ssoParam;
	}

	/**
	 * 
	 * <pre>
	 * comment  : SSO 파라미터 중에 특수 케이스 처리
	 * author   : takkies
	 * date     : 2020. 11. 9. 오후 2:41:44
	 * </pre>
	 * 
	 * @param state
	 * @return
	 */
	public String clearXssSsoParamState(final String state) {
		if (StringUtils.isEmpty(state)) {
			return state;
		}
		if (state.contains(",")) {
			String stateArr[] = StringUtils.commaDelimitedListToStringArray(state);
			StringBuilder b = new StringBuilder();
			int idx = 0;
			for (String str : stateArr) {
				String stateParts[] = StringUtils.delimitedListToStringArray(str, "=");
				if (idx == stateArr.length - 1) {
					b.append(stateParts[0]).append("=").append(clearXssCrlf(stateParts[1]));
				} else {
					b.append(stateParts[0]).append("=").append(clearXssCrlf(stateParts[1])).append(",");
				}
				idx++;
			}
			return b.toString();
		} else {
			String param = clearXssCrlf(state);
			return clearXSSNormal(param);
		}
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 9. 오후 3:10:19
	 * </pre>
	 * @param val
	 * @return
	 */
	public String clearXssCrlf(final String val) {
		if (StringUtils.isEmpty(val)) {
			return val;
		}
		try {
			String rtn = URLDecoder.decode(val, StandardCharsets.UTF_8.name());
			rtn = SecurityUtil.clearXSSNormal(rtn);
			rtn = rtn.replaceAll("\\r|\\n", "");
			return rtn;
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	/**
	 * 
	 * <pre>
	 * commnet  : HTML 태그가 있는 문자열에서 마크업(태그)을 모두 삭제한 평문 얻어오기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:08:58
	 * </pre>
	 * 
	 * @param html
	 * @return
	 */
	public String clearHtmlMarkup(final String html) {
		String xssstr = html;
		Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
		Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL);
		Pattern TAGS = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		Pattern ENTITY_REFS = Pattern.compile("&[^;]+;");
		Matcher m = SCRIPTS.matcher(html);
		xssstr = m.replaceAll("");
		m = STYLE.matcher(html);
		xssstr = m.replaceAll("");
		m = TAGS.matcher(html);
		xssstr = m.replaceAll("");
		m = ENTITY_REFS.matcher(html);
		xssstr = m.replaceAll("");
		return xssstr;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : AES 암호화키 생성
	 * author   : takkies
	 * date     : 2020. 7. 21. 오후 12:19:54
	 * </pre>
	 * 
	 * @param salt
	 * @param passphrase
	 * @return
	 */
	public SecretKey generateAesKey(final String salt, final String passphrase, final int interation, final int keysize) {
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), interation, keysize);
			SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
			return key;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return null;
		}
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 문자열을 Hex로 디코드
	 * author   : takkies
	 * date     : 2020. 7. 21. 오후 12:20:11
	 * </pre>
	 * 
	 * @param text
	 * @return
	 */
	public byte[] hex(final String text) {
		try {
			return Hex.decodeHex(text);
		} catch (DecoderException e) {
			return null;
		}
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 문자열을 Hex로 인코드
	 * author   : hjw0228
	 * date     : 2021. 7. 1. 오후 7:35:11
	 * </pre>
	 * 
	 * @param text
	 * @return
	 */
	public char[] hex(final byte[] text) throws DecoderException {
		return Hex.encodeHex(text);
	}
	
	/**
	 * 
	 * <pre>
	 * commnet  : base64 디코드
	 * author   : takkies
	 * date     : 2020. 7. 21. 오후 12:19:58
	 * </pre>
	 * 
	 * @param text
	 * @return
	 */
	public byte[] base64(final String text) {
		return Base64.decodeBase64(text.getBytes());
	}
	
	public String decodeBase64(final String text) {
		byte[] decodeBase64Binary = SecurityUtil.base64(text);
		String decodeBase64 = "";
		try {
			decodeBase64 = String.valueOf(SecurityUtil.hex(decodeBase64Binary));
			
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return decodeBase64;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : base64 인코드
	 * author   : takkies
	 * date     : 2020. 7. 21. 오후 12:20:06
	 * </pre>
	 * 
	 * @param bytes
	 * @return
	 */
	public String base64(final byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 암호화된 문자열에 포함된 시간과 현재시간 차이를 구함.
	 * author   : takkies
	 * date     : 2020. 7. 22. 오후 7:14:20
	 * </pre>
	 * 
	 * @param ssologintime
	 * @return
	 */
	public int getXValueTimeTerms(final String ssologintime, final TimeUnit timeunit) {
		String currenttime = DateUtil.getCurrentDateString("yyyyMMddHHmmss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date cdt = sdf.parse(currenttime.substring(0, 14));
			Date ldt = sdf.parse(ssologintime.substring(0, 14));
			if (timeunit == TimeUnit.HOURS) {
				return (int) ((cdt.getTime() - ldt.getTime()) / (60 * 60 * 1000)); // hours
			} else if (timeunit == TimeUnit.MINUTES) {
				return (int) ((cdt.getTime() - ldt.getTime()) / (60 * 1000)); // minutes
			} else {
				return (int) ((cdt.getTime() - ldt.getTime()) / 1000); // seconds
			}
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			return 0;
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 일반 문자열을 암호화된 문자열로 암호화하기
	 * author   : takkies
	 * date     : 2020. 8. 21. 오전 11:38:30
	 * </pre>
	 * 
	 * @param passphrase 암호화할때 사용한 암호화 비.밀.번.호
	 * @param plaintext 암호화할 문자열
	 * @return
	 */
	public String setXyzValue(final String passphrase, final String plaintext) {
		if (StringUtils.hasText(plaintext)) {
			SecurityEncoder encoder = securityfactory.getEncoder(SecurityFactory.AES);
			try {
				String convertedtext = new StringBuilder(UuidUtil.getUuidByDate()) //
						.append(StringUtil.dot()) //
						.append(plaintext) //
						.append(StringUtil.dot()) //
						.append(DateUtil.getCurrentUtcTimeStamp()) //
						.toString();
				return encoder.encode(passphrase, convertedtext);
			} catch (Exception e) {
				// NO PMD
			}
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 일반 문자열을 암호화된 문자열로 암호화하기
	 * author   : takkies
	 * date     : 2020. 8. 21. 오전 11:38:26
	 * </pre>
	 * 
	 * @param plaintext 암호화할 문자열
	 * @return
	 */
	public String setXyzValue(final String plaintext) {
		final String passphrase = config.getDecryptPassphrase();
		return setXyzValue(passphrase, plaintext);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 암호화된 문자열에서 복호화 문자열 추출하기
	 * author   : takkies
	 * date     : 2020. 8. 19. 오후 6:49:40
	 * </pre>
	 * 
	 * @param passphrase 암호화할때 사용한 암호화 비.밀.번.호
	 * @param encryptedtext 암호화된 문자열
	 * @param timecheck 복호화할 경우 타임체크 여부(타임체크 시간이 지나면 실패)
	 * @return
	 */
	public String getXyzValue(final String passphrase, final String encryptedtext, final boolean timecheck) {
		if (StringUtils.hasText(encryptedtext)) {
			SecurityDecoder decoder = securityfactory.getDecorder(SecurityFactory.AES);
			String decryptedtext;
			try {
				decryptedtext = decoder.decode(passphrase, encryptedtext);
				String arrays[] = decryptedtext.split(StringUtil.dot());
				if (arrays != null && arrays.length == 3) {
					if (timecheck) { // 시간 체크할 경우만 처리
						final int term = DateUtil.getDateTermMinutes(arrays[2]);
						final int checkterms = config.getXValueTimeCheckTerms();
						if (term >= checkterms) { // 체크 기간이 5분을 넘을 경우 유효성 체크 실패
							log.error("fail to check data validated terms : {}(min), success : below {}(min)", term, checkterms);
							return null;
						}
					}
					return arrays[1];
				}
			} catch (Exception e) {
				// NO PMD
			}
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 암호화된 문자열에서 복호화 문자열 추출하기(암호화한지 1분이 경과하면 실패)
	 * author   : takkies
	 * date     : 2020. 9. 14. 오전 9:31:04
	 * </pre>
	 * 
	 * @param passphrase 암호화할때 사용한 암호화 비.밀.번.호
	 * @param encryptedtext 암호화된 문자열
	 * @return
	 */
	public String getInstantXyzValue(final String passphrase, final String encryptedtext) {
		if (StringUtils.hasText(encryptedtext)) {
			SecurityDecoder decoder = securityfactory.getDecorder(SecurityFactory.AES);
			String decryptedtext;
			try {
				decryptedtext = decoder.decode(passphrase, encryptedtext);
				String arrays[] = decryptedtext.split(StringUtil.dot());
				if (arrays != null && arrays.length == 3) {
					final int term = DateUtil.getDateTermSeconds(arrays[2]);
					final int checkterms = 60; // 60초 이상이면 오류 발생
					if (term >= checkterms) { // 체크 기간이 1분을 넘을 경우 유효성 체크 실패
						log.error("fail to check data validated terms : {}(sec), success : below {}(sec)", term, checkterms);
						return null;
					}
					return arrays[1];
				}
			} catch (Exception e) {
				// NO PMD
			}
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 암호화된 문자열에서 복호화 문자열 추출하기
	 * author   : takkies
	 * date     : 2020. 8. 19. 오후 6:49:36
	 * </pre>
	 * 
	 * @param passphrase 암호화할때 사용한 암호화 비.밀.번.호
	 * @param encrytedtext 암호화된 문자열
	 * @return
	 */
	public String getXyzValue(final String passphrase, final String encrytedtext) {
		final boolean timecheck = config.isXValueTimeCheckEndable();
		return getXyzValue(passphrase, encrytedtext, timecheck);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 암호화된 문자열에서 복호화 문자열 추출하기
	 * author   : takkies
	 * date     : 2020. 8. 19. 오후 6:04:56
	 * </pre>
	 * 
	 * @param encrytedtext 암호화된 문자열
	 * @param timecheck 복호화할 경우 타임체크 여부(타임체크 시간이 지나면 실패)
	 * @return
	 */
	public String getXValue(final String encrytedtext, final boolean timecheck) {
		final String passphrase = config.getDecryptPassphrase();
		return getXyzValue(passphrase, encrytedtext, timecheck);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 암호화된 문자열에서 실제값을 추출
	 * author   : takkies
	 * date     : 2020. 8. 19. 오후 6:03:49
	 * </pre>
	 * 
	 * @param encrytedtext 암호화된 문자열
	 * @return
	 */
	public String getXValue(final String encrytedtext) {
		final String passphrase = config.getDecryptPassphrase();
		final boolean timecheck = config.isXValueTimeCheckEndable();
		return getXyzValue(passphrase, encrytedtext, timecheck);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : SHA-512 -> SAH-512+Base64 인코딩 방식으로 변경
	 * <code>
	 *   String originalpassword = "7a3f54e02878950f79b9c8d9ca663f84a33757bd546fc3ede80b49b8dab552d39621cb5389cb82a4d0fefd0ff66f638b824d09721df8e844c83de53d4c87f7e2";
	 *   String convertpassword = convertEncodingPasswordToWso2Format(originalpassword);
	 *   String comparepassword = "ej9U4Ch4lQ95ucjZymY/hKM3V71Ub8Pt6AtJuNq1UtOWIctTicuCpND+/Q/2b2OLgk0Jch346ETIPeU9TIf34g==";
	 *	
	 *   System.out.println(String.format("comparing password result : %s", convertpassword.equals(comparepassword)));
	 * </code>
	 * author   : takkies
	 * date     : 2020. 7. 28. 오전 9:13:13
	 * </pre>
	 * 
	 * @param originalpassword SHA-512 패.스.워.드
	 * @param originalreturn 오류시 원래 패.스.워.드 반환
	 * @return SAH-512+Base64 인코딩 방식으로 변경된 패.스.워.드
	 */
	public String convertSHA512EncodedPasswordToWso2Format(final String originalpassword, boolean originalreturn) {
		try {
			byte[] binarydata = hex(originalpassword);
			return base64(binarydata);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return originalreturn ? originalpassword : null;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : SHA-512 -> SAH-512+Base64 인코딩 방식으로 변경
	 * author   : takkies
	 * date     : 2020. 7. 28. 오전 9:21:23
	 * </pre>
	 * 
	 * @param originalpassword
	 * @return
	 */
	public String convertSHA512EncodedPasswordToWso2Format(final String originalpassword) {
		return convertSHA512EncodedPasswordToWso2Format(originalpassword, false);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 평문 패.스.워.드를 WSO2(SAH-512+Base64) 인코딩 방식으로 변경
	 * author   : takkies
	 * date     : 2020. 7. 28. 오후 4:59:07
	 * </pre>
	 * 
	 * @param loginpassword
	 * @return
	 * @throws Exception
	 */
	public String getEncodedWso2Password(final String loginpassword) {
		SecurityFactory securityFactory = SecurityFactory.getInstance();
		SecurityEncoder encoder = securityFactory.getEncoder(SecurityFactory.SHA);
		try {
			String sha512pwd = encoder.encode(loginpassword);
			return SecurityUtil.convertSHA512EncodedPasswordToWso2Format(sha512pwd);
		} catch (Exception e) {
			// NO PMD
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : SHA-512 로 변환 
	 * author   : takkies
	 * date     : 2020. 9. 15. 오전 10:57:35
	 * </pre>
	 * 
	 * @param loginpassword
	 * @return
	 */
	public String getEncodedSHA512Password(final String loginpassword) {
		SecurityFactory securityFactory = SecurityFactory.getInstance();
		SecurityEncoder encoder = securityFactory.getEncoder(SecurityFactory.SHA);
		try {
			return encoder.encode(loginpassword);
		} catch (Exception e) {
			// NO PMD
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : DB 에 있는 비.밀.번.호 와 평문 비.밀.번호를 비교. 같으면 True
	 * author   : takkies
	 * date     : 2020. 8. 21. 오후 6:48:30
	 * </pre>
	 * 
	 * @param dbpassword
	 * @param inputplainpassword
	 * @return
	 */
	public boolean compareWso2Password(final String dbpassword, final String inputplainpassword) {
		final String newpassword = getEncodedWso2Password(inputplainpassword);
		return dbpassword.compareTo(newpassword) == 0;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 11. 오전 9:42:43
	 * </pre>
	 * 
	 * @param plaintext
	 * @return
	 */
	public String getEncodedJasypt(final String plaintext) {
		final String password = config.getJasyptPassword();
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(password);
		return encryptor.encrypt(plaintext);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 31. 오후 8:18:41
	 * </pre>
	 * 
	 * @param encodedtext
	 * @return
	 */
	public String getDecodedJasypt(final String encodedtext) {
		final String password = config.getJasyptPassword();
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(password);
		return encryptor.decrypt(encodedtext);
	}

	/**
	 * 
	 * <pre>
	 * comment  : Basic Authencate 정보 생성(SMS 발송 EAI용) 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오후 8:56:29
	 * </pre>
	 * 
	 * @param username
	 * @param userpassword
	 * @return
	 */
	public String getBasicAuthorizationBase64(final String username, final String userpassword) {
		String authorizationstring = username.concat(":").concat(userpassword);
		return "Basic ".concat(Base64.encodeBase64String(authorizationstring.getBytes()));
	}
	
	public String HmacSHA256(String key, String data) {
		String HMAC_SHA256 = "HmacSHA256";
		try {
			Mac hMacSHA256 = Mac.getInstance(HMAC_SHA256);
			byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
			final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, HMAC_SHA256);
			hMacSHA256.init(secretKey);
			byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
			byte[] res = hMacSHA256.doFinal(dataBytes);
			
			return Base64.encodeBase64String(res);
		
		} catch (InvalidKeyException  e) {
			log.error("▶▶▶▶▶▶ [HmacSHA256] InvalidKeyException : {}", e);
			return "";
		} catch (NoSuchAlgorithmException e) {
			log.error("▶▶▶▶▶▶ [HmacSHA256] NoSuchAlgorithmException : {}", e);
			return "";
		}
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : Base64 인코딩 여부 확인
	 * author   : hjw0228
	 * date     : 2021. 7. 1. 오후 6:53:19
	 * </pre>
	 * 
	 * @param plaintext
	 * @return
	 */
	public boolean isBase64(String plaintext) {
		Pattern pattern = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
		Matcher matcher = pattern.matcher(plaintext);
		if (matcher.find()) {
            return true;
        } else {
        	return false;
        }
	}
	
	/**
	 *  제휴쇼핑몰 인증, 통합회원번호 암호화에 사용 (고객통합플랫폼 암호화 로직)
	 *  - 암호화
	 * */
	public static String encryptionAESKey(String val) {
		
		String encryptionKey = config.getAesEncryptionKey();
		byte[] encrypted = null;
		
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			encrypted = cipher.doFinal(val.getBytes());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
		return byteArrayToHex(encrypted);
	}
	
	/**
	 *  제휴쇼핑몰 인증, 통합회원번호 암호화에 사용 (고객통합플랫폼 복호화 로직)
	 *  - 복호화
	 * */
	public static String decryptionAESKey(String val) {
		
		String encryptionKey = config.getAesEncryptionKey();
        SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        String result = "";

        Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);

	        byte[] original = cipher.doFinal(hexToByteArray(val));
	        result = new String(original);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return  result;
	}
	
    public static String byteArrayToHex(byte[] ba) {

        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;

        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }

        return sb.toString();
    }
    
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return ba;
    }
    
	/**
	 *  제휴쇼핑몰 인증, 사용자 정보 암호화에 사용 (SSG 암호화 로직)
	 *  - 암호화
	 * */
    public static String encryptionAESKey(String val, String key, String iv) throws Exception {
		byte[] ivBytes = iv.substring(0, 16).getBytes(StandardCharsets.UTF_8);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, genKeySpec(key), new IvParameterSpec(ivBytes));

		byte[] encrypted = cipher.doFinal(val.getBytes(StandardCharsets.UTF_8));
		return org.apache.tomcat.util.codec.binary.Base64.encodeBase64URLSafeString(encrypted);
    }
    
	/**
	 *  제휴쇼핑몰 인증, 사용자 정보 암호화에 사용 (SSG 복호화 로직)
	 *  - 복호화
	 * */
	public static String decryptionAESKey(String val, String key, String iv) throws Exception {
		log.debug("decryptionAESKey ▶▶▶▶▶▶ val : {}, key : {}, iv : {}", val, key, iv);
		byte[] ivBytes = iv.substring(0, 16).getBytes(StandardCharsets.UTF_8);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, genKeySpec(key), new IvParameterSpec(ivBytes));

		byte[] byteStr = decodeBase64URLSafe(val);
		return new String(cipher.doFinal(byteStr));
	}
	
    public static String encodeBase64URLSafeString(final byte[] binaryData) {
        return org.apache.tomcat.util.codec.binary.StringUtils.newStringUsAscii(org.apache.tomcat.util.codec.binary.Base64.encodeBase64(binaryData, false, true));
    }
	
    public static byte[] decodeBase64URLSafe(final String base64String) {
        return new org.apache.tomcat.util.codec.binary.Base64(true).decode(base64String);
    }
    
	private static SecretKeySpec genKeySpec(String aesKey) {
		return new SecretKeySpec(aesKey.substring(0, 16).getBytes(), "AES");
	}
	
	/**
	 *  JoinSunset 오프라인 매장 로그인 시 암/복호화에 사용
	 *  - 암호화
	 * */
	public static String encript(String val, String key){
		try {
			SecretKeySpec skeySpec = createAESKeySpec(key);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(val.getBytes("UTF-8"));
			return new String(org.apache.tomcat.util.codec.binary.Base64.encodeBase64(encrypted));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String decrypt(String val, String key) {
		try {
			SecretKeySpec skeySpec = createAESKeySpec(key);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decrypted = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(val);
			return new String(cipher.doFinal(decrypted));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static SecretKeySpec createAESKeySpec(String key){
		Key skey = new SecretKeySpec(key.getBytes(), "AES");
		SecretKeySpec skeySpec = new SecretKeySpec(skey.getEncoded(), "AES");
		return skeySpec;
	}
}
