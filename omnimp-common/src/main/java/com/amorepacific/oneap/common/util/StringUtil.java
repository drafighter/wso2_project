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

import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.StringUtils.rightPad;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.validation.Phone;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.experimental.UtilityClass;

/**
 * <pre>
 * com.amorepacific.oneap.common.util 
 *    |_ StringUtil.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 15.
 * @version : 1.0
 * @author : takkies
 */
@UtilityClass
public class StringUtil {

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 26. 오후 6:54:01
	 * </pre>
	 * 
	 * @param boolflag
	 * @return
	 */
	public boolean isTrue(final String boolflag) {
		return StringUtils.isEmpty(boolflag) ? false : Boolean.parseBoolean(boolflag) || "1".equals(boolflag) || "Y".equalsIgnoreCase(boolflag);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : ascii 문자열 가져오기
	 * <code>
	 *  // ascii 코드 조회하기
	 *  for (char c = 0; c <= 255; c++) {
	 *      System.out.format("%c = 0x%02X (%3d)%n", c, (int) c, (int) c);
	 *  }
	 * </code>
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:02:04
	 * </pre>
	 * 
	 * @param asc
	 * @return
	 */
	public String ascToChar(final int asc) {
		char div = (char) asc;
		return Character.toString(div);
	}

	/**
	 * 
	 * <pre>
	 * commnet  :  문자열 조회
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:02:50
	 * </pre>
	 * 
	 * @return
	 */
	public String dot() {
		return ascToChar(7);
	}

	/**
	 * 
	 * <pre>
	 * comment  :  문자열 조회
	 * author   : takkies
	 * date     : 2020. 9. 17. 오후 5:23:29
	 * </pre>
	 * 
	 * @return
	 */
	public String dot2() {
		return ascToChar(8);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 숫자형 체크하기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:22:16
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNumeric(final String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 왼쪽에 문자열을 지정한 크기 만큼 추가하기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:32:18
	 * </pre>
	 * 
	 * @param orgstr 패딩할 문자열
	 * @param padsize 패딩할 사이즈
	 * @param padstr 패딩에 추가할 문자열
	 * @return
	 */
	public String padLeft(final String orgstr, final int padsize, final String padstr) {
		return leftPad(orgstr, padsize, padstr);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 오른쪽에 문자열을 지정한 크기 만큼 추가하기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:32:22
	 * </pre>
	 * 
	 * @param orgstr 패딩할 문자열
	 * @param padsize 패딩할 사이즈
	 * @param padstr 패딩에 추가할 문자열
	 * @return
	 */
	public String padRight(final String orgstr, final int padsize, final String padstr) {
		return rightPad(orgstr, padsize, padstr);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 문자열에서 특정 character 문자열 삭제
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:37:29
	 * </pre>
	 * 
	 * @param orgstr
	 * @param removestr
	 * @return
	 */
	public String removeChars(final String orgstr, final String removestr) {
		char[] chararray = removestr.toCharArray();
		String rtn = orgstr;
		for (char c : chararray) {
			rtn = remove(rtn, c);
		}
		return rtn;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 24. 오후 2:22:48
	 * </pre>
	 * 
	 * @param text
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public String replaceLast(String text, String regex, String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 6. 오후 2:06:56
	 * </pre>
	 * 
	 * @param patternstring
	 * @param param
	 * @return
	 */
	public boolean checkParameter(final String patternstring, final String param) {
		Pattern pattern = Pattern.compile(patternstring);
		Matcher matcher = pattern.matcher(param);
		return matcher.matches();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 7. 오후 7:23:27
	 * </pre>
	 * 
	 * @param mobile
	 * @return
	 */
	public String[] splitMobile(final String mobile) {
		String mobiles[] = new String[3];
		String mobileNo = mobile.replaceAll("-", "").replaceAll(" ", "");
		if (StringUtils.hasText(mobileNo)) {
			if (mobileNo.length() == 10) {
				mobiles[0] = mobileNo.substring(0, 3);
				mobiles[1] = mobileNo.substring(3, 6);
				mobiles[2] = mobileNo.substring(6);
			} else if (mobileNo.length() == 11) {
				mobiles[0] = mobileNo.substring(0, 3);
				mobiles[1] = mobileNo.substring(3, 7);
				mobiles[2] = mobileNo.substring(7);
			} else {
				throw new IllegalArgumentException(String.format("bad mobile number format : %s", mobile));
			}
		}
		return mobiles;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오후 7:18:41
	 * </pre>
	 * 
	 * @param customer
	 * @return
	 */
	public String mergeMobile(final Customer customer) {
		return new StringBuilder(customer.getCellTidn()) //
				.append(customer.getCellTexn()).append(customer.getCellTlsn()).toString();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 23. 오전 10:49:13
	 * </pre>
	 * 
	 * @param customer
	 * @return
	 */
	public String mergeNationalMobile(final Customer customer) {
		final String mobile = mergeMobile(customer);
		Phone phone = new Phone.Builder() //
				.phoneNumber(mobile) //
				.countryCode(LocaleUtil.getLocale().getCountry()) //
				.build();
		return phone.displayNationalPhoneNumber();
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 5. 오전 11:49:04
	 * </pre>
	 * @param mobile
	 * @return
	 */
	public String getNationalMobile(final String mobile) {
		Phone phone = new Phone.Builder() //
				.phoneNumber(mobile) //
				.countryCode(LocaleUtil.getLocale().getCountry()) //
				.build();
		return phone.displayNationalPhoneNumber();
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 비밀번호 강도 체크 시 중복 제거 문자 카운트 하기 
	 * author   : takkies
	 * date     : 2020. 8. 18. 오후 2:28:49
	 * </pre>
	 * 
	 * @param password
	 * @return
	 */
	public int checkRemoveDupCount(final String password) {
		List<String> resultArray = new ArrayList<>();
		String[] strArray = password.split("");
		int dupCount = 0;
		if (strArray.length > 0) {
			for (int i = 0; i < strArray.length; i++) {
				String el = strArray[i];
				if ("".equals(el)) {
					continue;
				} else {
					if (resultArray.toString().indexOf(el) == -1) {
						resultArray.add(el);
					} else {
						dupCount += 1;
					}
				}
			}
		}
		return dupCount;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 비밀번호 강도 체크 시 동일문자 카운트
	 * author   : takkies
	 * date     : 2020. 8. 18. 오후 2:30:42
	 * </pre>
	 * 
	 * @param password
	 * @return
	 */
	public int checkSameCount(final String password) {
		int cnt_same = 0;
		int max_conut = 0;

		for (int i = 0; i < password.length() - 1; i++) {
			if (password.substring(i, (i + 1)).equals(password.substring((i + 1), (i + 2)))) {
				cnt_same++;
			} else {
				if (cnt_same > max_conut) {
					max_conut = cnt_same;
				}
				cnt_same = 0;
			}
		}
		if (cnt_same > max_conut) {
			max_conut = cnt_same;
		}
		return max_conut;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 비밀번호 강도 체크 시 반복된문자 카운트
	 * author   : takkies
	 * date     : 2020. 8. 18. 오후 2:31:06
	 * </pre>
	 * 
	 * @param password
	 * @return
	 */
	public int checkContinueCnt(String password) {
		int count = 0;
		int max_cnt = 0;
		for (int i = 0; i < password.length() - 1; i++) {
			if (password.charAt(i) == (password.charAt(i + 1) - 1)) {
				count++;
			} else {
				if (count > max_cnt) {
					max_cnt = count;
				}
				count = 0;
			}
		}
		if (count > max_cnt) {
			max_cnt = count;
		}
		return max_cnt;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 3. 오후 1:59:35
	 * </pre>
	 * 
	 * @param obj
	 * @return
	 */
	public String printJson(Object obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonElement je = JsonParser.parseString(mapper.writeValueAsString(obj));
			Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
			return "\n" + gson.toJson(je);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 문자열 인코딩을 고려해서 문자열 자르기
	 * author   : takkies
	 * date     : 2020. 12. 7. 오후 2:28:27
	 * </pre>
	 * @param parameterName
	 * @param maxLength
	 * @return
	 */
	public static String substring(String parameterName, int maxLength) {
        int DB_FIELD_LENGTH = maxLength;
 
        Charset utf8Charset = Charset.forName("UTF-8");
        CharsetDecoder cd = utf8Charset.newDecoder();
 
        try {
            byte[] sba = parameterName.getBytes("UTF-8");
            // Ensure truncating by having byte buffer = DB_FIELD_LENGTH
            ByteBuffer bb = ByteBuffer.wrap(sba, 0, DB_FIELD_LENGTH); // len in [B]
            CharBuffer cb = CharBuffer.allocate(DB_FIELD_LENGTH); // len in [char] <= # [B]
            // Ignore an incomplete character
            cd.onMalformedInput(CodingErrorAction.IGNORE);
            cd.decode(bb, cb, true);
            cd.flush(cb);
            parameterName = new String(cb.array(), 0, cb.position());
        } catch (UnsupportedEncodingException e) {
            // log.error("### 지원하지 않는 인코딩입니다." + e);
        }
        return parameterName;
    }
 
    /**
     * 
     * <pre>
     * comment  : 문자열 인코딩에 따라서 글자수 체크
     * author   : takkies
     * date     : 2020. 12. 7. 오후 2:28:43
     * </pre>
     * @param sequence
     * @return
     */
    public static int length(CharSequence sequence) {
        int count = 0;
        for (int i = 0, len = sequence.length(); i < len; i++) {
            char ch = sequence.charAt(i);
 
            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                ++i;
            } else {
                count += 3;
            }
        }
        return count;
    }
    
    public static String compress(String input) {
    	try {
            String result = null;
            if (input != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipos = new GZIPOutputStream(baos);
                gzipos.write(input.getBytes());
                gzipos.close();
                result = new String(baos.toByteArray(), "ISO-8859-1");
                baos.close();
            }
            return result;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }

    public static String decompress(String input) {
    	try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes("ISO-8859-1"));
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(gzipInputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             
            byte[] buffer = new byte[1024];
             
            int length;
            while((length = bufferedInputStream.read(buffer,0,1024)) > 0) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
             
            bufferedInputStream.close();
            gzipInputStream.close();
            byteArrayInputStream.close();
            byteArrayOutputStream.close();
            
            String result = byteArrayOutputStream.toString("UTF-8");
             
            return result;      		
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public boolean isValidPhoneNumber(final String phoneNumber) {
    	String regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
		return phoneNumber.matches(regexp);
    }
}
