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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.vo.cert.CertResult;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.util 
 *    |_ DateUtil.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 15.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@UtilityClass
public class DateUtil {

	public final String DEFAULT_DATETIME_FORMAT = "yyyyMMddHHmmss";

	public final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public final String DATE_FORMAT_WITH_DOT = "yyyy.MM.dd";

	/**
	 * 
	 * <pre>
	 * comment  : convert WSO2 UTC date format to string 
	 * author   : takkies
	 * date     : 2020. 8. 10. 오후 9:01:57
	 * </pre>
	 * 
	 * @param stringdate
	 * @param dateformat
	 * @return
	 */
	public String getDateTimeWso2Format(final String stringdate, final String dateformat) {
		String dtf = StringUtils.isEmpty(dateformat) ? DATETIME_FORMAT : dateformat;
		Instant instant = Instant.parse(stringdate);
		Date date = Date.from(instant);
		SimpleDateFormat sdf = new SimpleDateFormat(dtf);
		return sdf.format(date);
	}

	/**
	 * 
	 * <pre>
	 * comment  : convert WSO2 UTC date format to string
	 * author   : takkies
	 * date     : 2020. 8. 10. 오후 8:59:52
	 * </pre>
	 * 
	 * @param stringdate
	 * @return
	 */
	public String getDateTimeWso2Format(final String stringdate) {
		return getDateTimeWso2Format(stringdate, null);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 
	 * author   : takkies
	 * date     : 2020. 7. 16. 오전 11:59:24
	 * </pre>
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public String getDateTime(final String date) {
		try {
			return getDateTime(date, DEFAULT_DATETIME_FORMAT);
		} catch (ParseException e) {
			return date;
		}
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 
	 * author   : takkies
	 * date     : 2020. 7. 16. 오전 11:59:28
	 * </pre>
	 * 
	 * @param date yyyyMMddHHmmss, yyyy-MM-dd HH:mm:ss 형태(내부 처리시 숫자남기고 제거)
	 * @param dateformat 변환할 숫자 포맷
	 * @return
	 * @throws ParseException
	 */
	public String getDateTime(final String date, final String dateformat) throws ParseException {
		String datestr = date.replaceAll("[^0-9]", "");
		if (datestr.length() < 12) {
			// throw new ParseException(String.format("date format must 'yyyyMMddHHmmss', invalid format : %s", date), -1);
		}
		String format = dateformat;
		if (StringUtils.isEmpty(dateformat)) {
			format = DEFAULT_DATETIME_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date formatdate = sdf.parse(datestr);
		return getDateTimeTimezoneAndFormat(formatdate, LocaleUtil.getTimezone(), format);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 지정한 문자열 형태의 날짜를 정해진 타임존과 날짜 포멧에 따라 문자열로 가져오기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 8:03:44
	 * </pre>
	 * 
	 * @param date
	 * @param timezone
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public String getDateTimeTimezoneAndFormat(final String date, final String timezone, String datetimeformat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(datetimeformat);
		Date formatdate = sdf.parse(date);
		return getDateTimeTimezoneAndFormat(formatdate, timezone, datetimeformat);

	}

	/**
	 * 
	 * <pre>
	 * commnet  : 지정한 Date 형태의 날짜를 정해진 타임존과 날짜 포멧에 따라 문자열로 가져오기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 8:08:15
	 * </pre>
	 * 
	 * @param date
	 * @param timezone
	 * @param datetimeformat
	 * @return
	 */
	public String getDateTimeTimezoneAndFormat(final Date date, final String timezone, String datetimeformat) {
		SimpleDateFormat sdf = new SimpleDateFormat(datetimeformat);
		TimeZone tz = TimeZone.getTimeZone(timezone);
		sdf.setTimeZone(tz);
		return sdf.format(date);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 지정한 Timestamp 형태의 날짜를 정해진 타임존과 날짜 포멧에 따라 문자열로 가져오기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 8:10:13
	 * </pre>
	 * 
	 * @param date
	 * @param timezone
	 * @param datetimeformat
	 * @return
	 */
	public String getDateTimeTimezoneAndFormat(final Timestamp date, final String timezone, String datetimeformat) {
		Date formatdate = new Date(date.getTime());
		return getDateTimeTimezoneAndFormat(formatdate, timezone, datetimeformat);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 해당 로케일의 현재 날짜(YYYYMMDD) 가져오기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:59:26
	 * </pre>
	 * 
	 * @return
	 */
	public String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", LocaleUtil.getLocale());
		return formatter.format(new Date());
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 해당 로케일의 현재 시간(HHmmss) 가져오기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:59:29
	 * </pre>
	 * 
	 * @return
	 */
	public String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HHmmss", LocaleUtil.getLocale());
		return formatter.format(new Date());
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 13. 오후 3:42:16
	 * </pre>
	 * 
	 * @return
	 */
	public String getCurrentDateTime() {
		return getCurrentDate() + getCurrentTime();
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 현재 날짜와 시각을 yyyy-MM-dd hh:mm:ss.fffffffff Format의 Timestamp로 Return
	 * millis 미만은 정확한 System 시간이 아님. sequential 한 값을 반환하는게 목적
	 * 기존 miils(1/1000초 resolution) + sequence
	 * 2004-02-24 13:22:11.734
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:22:21
	 * </pre>
	 * 
	 * @return
	 */
	public Timestamp getCurrentTimestamp() {
		Timestamp ts = new Timestamp(new GregorianCalendar().getTime().getTime());
		ts.setNanos(ts.getNanos() + (int) (System.nanoTime() % 1000000000) / 1000);
		return ts;
	}
	
	public Timestamp getCurrentTimestampAfterSecond(final int second) {
		Timestamp ts = new Timestamp(new GregorianCalendar().getTime().getTime());
		ts.setNanos(ts.getNanos() + (int) (System.nanoTime() % 1000000000) / 1000);
		return ts;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 표준 date time String을 반환
	 * yyyy-MM-dd HH:mm:ss.SSSSSSSSS (JDK 1.8)
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:24:16
	 * </pre>
	 * 
	 * @return
	 */
	public String getCurrentDateTimestampString() {
		return getCurrentTimestamp().toString();
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:59:16
	 * </pre>
	 * 
	 * @return
	 */
	public String getCurrentUtcTimeStamp() {
		ZonedDateTime utc = Instant.ofEpochMilli(Instant.now().toEpochMilli()).atZone(ZoneOffset.UTC);
		return utc.toString();
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 현재 날짜를 주어진 Format의 String 으로 Return
	 * DateUtil.getCurrentDateString("yyyy-MM-dd HH:mm:ss") ===> 2017-12-05 09:38:42
	 * DateUtil.getCurrentDateString("yyyy/MM/dd") ===> 2004/02/24
	 * DateUtil.getCurrentDateString("HH:mm:ss"));	===> 13:40:05
	 * DateUtil.getCurrentDateString("hh:mm:ss"));	===> 01:40:05
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:39:22
	 * </pre>
	 * 
	 * @param format
	 * @return
	 */
	public String getCurrentDateString(final String format) {
		return formatTimestamp(getCurrentTimestamp(), format, LocaleUtil.getLocale());
	}
	
	public String getTimestampAfterSecond(final String format, final int second) {
		return formatTimestamp(getCurrentTimestampAfterSecond(second), format, LocaleUtil.getLocale());
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 현재 날짜에 날짜를 더하거나 빼서 주어진 Format의 String 으로 Return
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:42:22
	 * </pre>
	 * 
	 * @param format
	 * @param day
	 * @return
	 */
	public String getCurrentDateString(final String format, final int day) {
		return getDayAdd(getCurrentDateString(format), day, format);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 현재 날짜를 yyyy-MM-dd HH:mm:ss 포맷의 String 으로 Return
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:43:21
	 * </pre>
	 * 
	 * @return
	 */
	public String getCurrentDateTimeString() {
		return getCurrentDateString("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:29:10
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public String convertValidDateFormat(final String yyyymmdd) {
		String date = yyyymmdd;
		if (date == null || !(date.trim().length() == 8 || date.trim().length() == 10)) {
			throw new IllegalArgumentException("Invalid date format(YYYYMMDD): " + date);
		}
		if (date.length() == 10) {
			date = StringUtil.removeChars(date, "-");
		}
		return date;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : Timestamp 형식의 날짜를 입력한 Format 과 Locale 에 따라 Formatting
	 * formatTimestamp("2004-02-25 15:45:31.156","yyyy년MM월dd일",Locale) ===> 2004년02월25일
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:35:15
	 * </pre>
	 * 
	 * @param timestamp
	 * @param format
	 * @param locale
	 * @return
	 */
	public String formatTimestamp(final Timestamp timestamp, final String format, final Locale locale) {
		SimpleDateFormat formatter = new SimpleDateFormat(format, locale);
		return formatter.format(timestamp);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : Timestamp 형식의 날짜를 입력한 Format 과 Locale 에 따라 Formatting
	 * formatTimestamp("2004-02-25 15:45:31.156","yyyy년MM월dd일") ===> 2004년02월25일
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:36:12
	 * </pre>
	 * 
	 * @param timestamp
	 * @param format
	 * @return
	 */
	public String formatTimestamp(final Timestamp timestamp, final String format) {
		return formatTimestamp(timestamp, format, LocaleUtil.getLocale());
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 문자열 형식의 날짜를 입력한 Format 과 Locale 에 따라 Formatting
	 * author   : takkies
	 * date     : 2020. 7. 16. 오전 11:41:27
	 * </pre>
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public String formatTimestamp(final String date, final String format) {
		Timestamp timestamp = Timestamp.valueOf(date);
		return formatTimestamp(timestamp, format, LocaleUtil.getLocale());
	}

	/**
	 * 
	 * <pre>
	 * comment  : Unix 타임스탬프형식을 Date 문자열로 변환 
	 * author   : takkies
	 * date     : 2020. 8. 19. 오후 2:10:50
	 * </pre>
	 * 
	 * @param timestampstring
	 * @param format
	 * @return
	 */
	public String getUnixTimestampToDateString(final String timestampstring, final String format) {
		String timestampStr = timestampstring;
		if (StringUtils.hasText(timestampStr)) {
			if (timestampStr.length() > 13) {
				timestampStr = timestampStr.substring(0, 13);
			}
		}

		try {
			long timestamp = Long.parseLong(timestampStr);
			Date date;
			if (timestampStr.length() == 10) {
				date = new Date(timestamp * 1000L);
			} else if (timestampStr.length() == 13) {
				date = new Date(timestamp * 1L);
			} else {
				throw new IllegalArgumentException(String.format("timestamp string is invalid, %s(%d)", timestampstring, timestampstring.length()));
			}
			SimpleDateFormat sdf = new java.text.SimpleDateFormat(format); // "yyyy-MM-dd HH:mm:ss"
			String timeZone = Calendar.getInstance().getTimeZone().getID();
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone)); // sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
			return sdf.format(date);
		} catch (NumberFormatException e) {
			return "";
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : Unix 타임스탬프형식을 Date 문자열로 변환 
	 * author   : takkies
	 * date     : 2020. 8. 19. 오후 1:59:22
	 * </pre>
	 * 
	 * @param timestampstring
	 * @return
	 */
	public String getUnixTimestampToDateString(final String timestampstring) {
		final String format = "yyyyMMddHHmmss";
		return getUnixTimestampToDateString(timestampstring, format);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:59:05
	 * </pre>
	 * 
	 * @param strdt
	 * @param term
	 * @return
	 */
	public String getDayAdd(final String strdt, final int term) {
		return getDayAdd(strdt, term, "");
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:59:02
	 * </pre>
	 * 
	 * @param strdt
	 * @param term
	 * @param format
	 * @return
	 */
	public String getDayAdd(String strdt, int term, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format == null || "".equals(format) ? "yyyy-MM-dd" : format);
		try {
			Date dt = formatter.parse(strdt);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.DATE, term);
			return formatter.format(cal.getTime());
		} catch (ParseException e) {
			log.error(e.getMessage(),e );
			return "";
		}
	}
	
	/**
	 * <pre>
	 * comment  : 현재 날짜에 일정 시간의 분을 더한 시간 리턴
	 * author   : judahye
	 * date     : 2023. 11. 27. 오후 10:28:00
	 * </pre>
	 * @param strdt
	 * @param term
	 * @param format
	 * @return
	 */
	public String getDayMinute(String strdt, int term, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format == null || "".equals(format) ? "yyyy-MM-dd HH:mm:ss" : format);
		try {
			Date dt = formatter.parse(strdt);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.MINUTE, term);
			return formatter.format(cal.getTime());
		} catch (ParseException e) {
			log.error(e.getMessage(),e );
			return "";
		}
	}

	/**
	 * 
	 * <pre>
	 * commnet  : yyyyMMdd 혹은 yyyy-MM-dd 형식의 날짜 문자열을 입력 받아 년, 월, 일을 증감한다. 
	 * 년, 월, 일은 가감할 수를 의미하며, 음수를 입력할 경우 감한다.
	 * <pre>
	 * DateUtil.addYearMonthDay("19810828", 0, 0, 19)  = "19810916"
	 * DateUtil.addYearMonthDay("20060228", 0, 0, -10) = "20060218"
	 * DateUtil.addYearMonthDay("20060228", 0, 0, 10)  = "20060310"
	 * DateUtil.addYearMonthDay("20060228", 0, 0, 32)  = "20060401"
	 * DateUtil.addYearMonthDay("20050331", 0, -1, 0)  = "20050228"
	 * DateUtil.addYearMonthDay("20050301", 0, 2, 30)  = "20050531"
	 * DateUtil.addYearMonthDay("20050301", 1, 2, 30)  = "20060531"
	 * DateUtil.addYearMonthDay("20040301", 2, 0, 0)   = "20060301"
	 * DateUtil.addYearMonthDay("20040229", 2, 0, 0)   = "20060228"
	 * DateUtil.addYearMonthDay("20040229", 2, 0, 1)   = "20060301" 
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:30:36
	 * </pre>
	 * 
	 * @param date
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public String addYearMonthDay(final String date, final int year, final int month, final int day) {
		String dateStr = convertValidDateFormat(date);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		try {
			cal.setTime(sdf.parse(dateStr));
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid date format: " + dateStr);
		}
		if (year != 0)
			cal.add(Calendar.YEAR, year);
		if (month != 0)
			cal.add(Calendar.MONTH, month);
		if (day != 0)
			cal.add(Calendar.DATE, day);
		return sdf.format(cal.getTime());
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 주어진 날짜와 현재 시간(yyyyMMddHHmmss)의 차이를 분으로 조회
	 * final int term = getDateTermMinutes(arr[2]);
	 *		log.debug("check data validation terms : {}(below 10 ---> ok)", term);
	 *		if (term > 9) { // 체크 기간이 10분을 넘을 경우 유효성 체크 실패
	 *			log.warn("decrypt data valid fail!!!, validation term(below 10 mins) : {}", term);
	 *			return null;
	 *		}
	 * author   : takkies
	 * date     : 2020. 7. 22. 오후 8:23:42
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public int getDateTermMinutes(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String sdt = formatter.format(new Date());
		try {
			date = date.length() > 14 ? date.substring(0, 14) : StringUtil.padRight(date, 14, "0");
			Date dt = formatter.parse(date);
			Date ldt = convertUtcToLocalTime(dt); // javascript 에서 UTC 로 보내므로 변환해야함.
			Date ct = formatter.parse(sdt);
			long diff = ct.getTime() - ldt.getTime();
			return (int) TimeUnit.MILLISECONDS.toMinutes(diff); // (diff / (60 * 1000));
		} catch (ParseException e) {
			return -1;
		}
	}

	public String getUnlockTimeTermStringMinutes(String unlocktime) {
		int m = getUnlockTimeTermMinutes(unlocktime);
		String minute = Integer.toString(m);
		return minute.length() == 1 ? StringUtil.padLeft(minute, 2, "0") : minute;
	}

	public String getUnlockTimeTermStringSeconds(String unixunlocktime) {
		int s = getUnlockTimeTermSeconds(unixunlocktime) % 60;
		String second = Integer.toString(s);
		return second.length() == 1 ? StringUtil.padLeft(second, 2, "0") : second;
	}

	public String getRemainedUnlockTime(String unixunlocktime) {
		String minute = getUnlockTimeTermStringMinutes(unixunlocktime);
		String second = getUnlockTimeTermStringSeconds(unixunlocktime);
		return minute + ":" + second;
	}

	public int getUnlockTimeTermMinutes(String unixunlocktime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String sdt = formatter.format(new Date());
		try {
			unixunlocktime = unixunlocktime.length() > 14 ? unixunlocktime.substring(0, 14) : StringUtil.padRight(unixunlocktime, 14, "0");
			final String unlocktime = DateUtil.getUnixTimestampToDateString(unixunlocktime);
			Date dt = formatter.parse(unlocktime);
			Date ct = formatter.parse(sdt);
			long diff = dt.getTime() - ct.getTime();
			return (int) TimeUnit.MILLISECONDS.toMinutes(diff); // (diff / (60 * 1000));
		} catch (ParseException e) {
			return -1;
		}
	}

	public int getUnlockTimeTermSeconds(String unixunlocktime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String sdt = formatter.format(new Date());
		try {
			unixunlocktime = unixunlocktime.length() > 14 ? unixunlocktime.substring(0, 14) : StringUtil.padRight(unixunlocktime, 14, "0");
			final String unlocktime = DateUtil.getUnixTimestampToDateString(unixunlocktime);
			Date dt = formatter.parse(unlocktime);
			Date ct = formatter.parse(sdt);
			long diff = dt.getTime() - ct.getTime();
			return (int) TimeUnit.MILLISECONDS.toSeconds(diff); // (diff / (60 * 60 * 1000));
		} catch (ParseException e) {
			return -1;
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 14. 오전 9:30:11
	 * </pre>
	 * 
	 * @param begindt
	 * @param enddt
	 * @return
	 */
	public int getDateMinutesTerms(String begindt, String enddt) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date sdt = formatter.parse(begindt);
			Date edt = formatter.parse(enddt);
			long diff = sdt.getTime() - edt.getTime();
			return (int) TimeUnit.MILLISECONDS.toMinutes(diff);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			return -1;
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 14. 오전 9:30:15
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public int getDateTermSeconds(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String sdt = formatter.format(new Date());
		try {
			date = date.length() > 14 ? date.substring(0, 14) : StringUtil.padRight(date, 14, "0");
			Date dt = formatter.parse(date);
			Date ldt = convertUtcToLocalTime(dt); // javascript 에서 UTC 로 보내므로 변환해야함.
			Date ct = formatter.parse(sdt);
			long diff = ct.getTime() - ldt.getTime();
			return (int) TimeUnit.MILLISECONDS.toSeconds(diff); // (diff / (60 * 60 * 1000));
		} catch (ParseException e) {
			return -1;
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : WSO2 마지막 비밀번호 변경기간과 현재시간과의 차이 계산
	 * author   : takkies
	 * date     : 2020. 9. 16. 오후 7:07:37
	 * </pre>
	 * 
	 * @param unixdate
	 * @return
	 */
	public int getLastPasswordUpdateTermDays(String unixdate) {
		if (StringUtils.isEmpty(unixdate)) {
			return -1;
		}
		if (unixdate.length() < 13) {
			unixdate = StringUtil.padRight(unixdate, 13, "0");
		}
		// lastPasswordUpdate : 마지막으로 비밀번호 변경한 날짜
		String lstdt = getUnixTimestampToDateString(unixdate);
		return getDateTermDays(lstdt);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 16. 오후 7:04:53
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public int getDateTermDays(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String sdt = formatter.format(new Date());
		try {
			date = date.length() > 13 ? date.substring(0, 13) : StringUtil.padRight(date, 13, "0");
			Date dt = formatter.parse(date);
			Date ct = formatter.parse(sdt);
			long diff = ct.getTime() - dt.getTime();
			return (int) TimeUnit.MILLISECONDS.toDays(diff);
		} catch (ParseException e) {
			return -1;
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 20. 오후 6:49:58
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public boolean isValidDateFormat(final String date) {
		String dt = date;
		if (StringUtils.isEmpty(dt)) {
			return false;
		} else {
			
			dt = dt.replaceAll(" ", "").replaceAll("-", "").replaceAll("/", "").replaceAll("\\.", "");
			
			// 숫자가 아니면 체크 실패
			if (!StringUtil.isNumeric(dt)) {
				return false;
			}
			
			final String yr = dt.substring(0, 4);
			final String mm = dt.substring(4, 6);
			final String dd = dt.substring(6);
			dt = yr + "." + mm + "." + dd;
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			dateFormat.setLenient(false);
			dateFormat.parse(dt);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : WSO2 비.밀.번.호를 7회 이상 틀렸을 경우(20231102 5->7), unlocktime 기능 제거
	 * 잠금이 발생함. 이 경우 잠금이 발생하면 로그인을 할 수 없음.
	 * 이유는 로그인 플로우에서 먼저 DB를 조회하기 때문에
	 * 잠금 플래그가 조회되어 빠져나갈 수 없음.
	 * 이 경우를 해결하기 위해 unlockTime으로 체크
	 * unlockTime은 잠김이 발생하는 경우 지정한 시간이 더해져서 DB에 unix time으로 입력됨.
	 * 현재 시간이 unlockTime 을 지났을 경우 이미 잠금 시간이 지난 것이므로
	 * 잠금을 체크하지 않도록 하여 commonauth 까지 인증이 도달하도록 함.
	 * commonauth에서 정상적으로 로그인이 이루어지면 WSO2 에서 잠금이 자동으로 해제됨.
	 *  
	 * author   : takkies
	 * date     : 2020. 9. 8. 오후 9:29:48
	 * </pre>
	 * 
	 * @param unlockUnixTime
	 * @return
	 */
	public boolean isValidUnlockTime(final String unlockUnixTime) {
		if (unlockUnixTime.equals("0")) {
			return false; // reset unlocktime 아님
		}
		final String format = "yyyyMMddHHmmss";
		final String unlockTime = getUnixTimestampToDateString(unlockUnixTime);
		final String currentdt = getCurrentDateString(format);
		int dt = getDateMinutesTerms(unlockTime, currentdt);
		return dt > 0 ? true : false; // 현재시간이 unlocktime 을 지났으면 unlocktime 아님.
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 
	 * author   : takkies
	 * date     : 2020. 7. 22. 오후 8:23:55
	 * </pre>
	 * 
	 * @param dt
	 * @return
	 */
	public Date convertUtcToLocalTime(Date dt) {
		TimeZone tz = TimeZone.getDefault();
		int offset = tz.getOffset(dt.getTime());
		long ldt = dt.getTime() + offset;
		Date cdt = new Date();
		cdt.setTime(ldt);
		return cdt;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 해당 주어진 Timestamp 날짜와 오늘날짜와의 차이
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:54:44
	 * </pre>
	 * 
	 * @param timestamp
	 * @return
	 */
	public int getCurrentDateTimeTerms(final Timestamp timestamp) {
		Date date = new Date(timestamp.getTime());
		Date currdate = new Date();
		long diff = currdate.getTime() - date.getTime();
		long diffdays = diff / (24 * 60 * 60 * 1000);
		return (int) diffdays;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 해당 주어진 문자열 날짜와 오늘날짜와의 차이
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:55:01
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public int getCurrentDateTimeTerms(final String date) {
		if (StringUtils.hasText(date) && date.length() > 8) {
			Timestamp timestamp = Timestamp.valueOf(date);
			return getCurrentDateTimeTerms(timestamp);
		}
		throw new IllegalArgumentException("invalid date format : " + date);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 문자열 두날짜사이의 날짜 차이를 구하기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오후 7:44:12
	 * </pre>
	 * 
	 * @param begindate 시작일(YYYYMMDD) 문자열
	 * @param enddate 종료일(YYYYMMDD) 문자열
	 * @return
	 */
	public int getBetweenDateTerms(final String begindate, final String enddate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		try {
			Date beginDate = formatter.parse(begindate);
			Date endDate = formatter.parse(enddate);
			long diff = endDate.getTime() - beginDate.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			return (int) diffDays;
		} catch (ParseException e) {
			log.error(e.getMessage(),e );
			throw new IllegalArgumentException(String.format("invalid date format %s, %s ", begindate, enddate));
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 13. 오후 3:50:37
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public int getBetweenDateTerms(final String date) {
		final String currentdt = getCurrentDate();
		return getBetweenDateTerms(date, currentdt);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오후 1:19:28
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public String getBirthDate(final String date) {
		if (StringUtils.isEmpty(date)) {
			return date;
		}

		String strdate = date.replaceAll(" ", "").replaceAll("-", "").replaceAll("/", "").replaceAll("\\.", "");
		strdate = (strdate.length() > 8) ? strdate.substring(0, 8) : strdate;
		SimpleDateFormat beforeFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat afterFormat = new SimpleDateFormat(DATE_FORMAT_WITH_DOT);
		try {
			Date dt = beforeFormat.parse(strdate);
			return afterFormat.format(dt);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			return date;
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : DB에 YYMMDD 형태로 들어가 있으므로 해당 형태로 변경 
	 * author   : takkies
	 * date     : 2020. 8. 27. 오후 12:01:22
	 * </pre>
	 * 
	 * @param birth
	 * @return
	 */
	public String getCertBirth(final String birth) {
		if (StringUtils.hasText(birth)) {
			String dtformat = "yyMMdd";
			if (birth.length() == 8) {
				dtformat = "yyyyMMdd";
			}
			SimpleDateFormat beforeFormat = new SimpleDateFormat(dtformat);
			SimpleDateFormat afterFormat = new SimpleDateFormat("yyMMdd");
			try {
				Date dt = beforeFormat.parse(birth);
				return afterFormat.format(dt);
			} catch (ParseException e) {
				// NO PMD
			}
		}
		return birth;
	}

	/**
	 * 
	 * <pre>
	 * comment  : WSO2 um_user_attribute 날짜타입 문자열
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 21. 오후 6:31:48
	 * </pre>
	 * 
	 * @return
	 */
	public String getCurrentDateTimeWso2Utc() {
		DateTime dt = new DateTime(new Date().getTime()).withChronology(ISOChronology.getInstanceUTC());
		return dt.toString();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 본인인증 시 제한 연령(14세) 체크하기 
	 * author   : takkies
	 * date     : 2020. 9. 15. 오전 11:45:30
	 * </pre>
	 * 
	 * @param certResult
	 * @return
	 */
	public long joinRestictByAuth(final CertResult certResult) {
		String birthdt = certResult.getBirth();
		return joinRestictByAuth(birthdt);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 본인인증 시 제한 연령(14세) 체크하기
	 * author   : takkies
	 * date     : 2020. 10. 6. 오전 11:50:42
	 * </pre>
	 * 
	 * @param birth
	 * @return
	 */
	public long joinRestictByAuth(final String birth) {
		final String currdt = getCurrentDate();
		String birthdt = birth;
		birthdt = birthdt.replaceAll("-", "").replaceAll("/", "").replaceAll(" ", "").replaceAll("\\.", "");
		birthdt = birthdt.length() > 8 ? birthdt.substring(0, 8) : StringUtil.padRight(birthdt, 8, "0");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		try {
			Date cdt = format.parse(currdt);
			Date bdt = format.parse(birthdt);
			long diff = cdt.getTime() - bdt.getTime();
			long days = TimeUnit.MILLISECONDS.toDays(diff);
			long age = days / 365;
			return age;
		} catch (ParseException e) {
			// NO PMD
		}
		// 날짜 체크 시 오류 발생할 경우 제한 없다고 판단, 아니면 오류로 본인인증 진행안됨.
		return 20;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 본인인증 시 제한 연령(14세) 체크하기
	 * author   : takkies
	 * date     : 2020. 10. 6. 오후 3:16:52
	 * </pre>
	 * 
	 * @param certResult
	 * @return 제한이면 true
	 */
	public boolean isJoinRestrictByAuth(final CertResult certResult) {
		String birthdt = certResult.getBirth();
		return isJoinRestrictByAuth(birthdt);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 본인인증 시 제한 연령(14세) 체크하기
	 * author   : takkies
	 * date     : 2020. 10. 6. 오후 3:15:48
	 * </pre>
	 * 
	 * @param birth
	 * @return 제한이면 true
	 */
	public boolean isJoinRestrictByAuth(final String birth) {
		String birthdt = birth;
		birthdt = birthdt.replaceAll("-", "").replaceAll("/", "").replaceAll(" ", "").replaceAll("\\.", "");
		birthdt = birthdt.length() > 8 ? birthdt.substring(0, 8) : StringUtil.padRight(birthdt, 8, "0");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -14);
		Date caldt = cal.getTime();
		try {
			Date cdt = format.parse(format.format(caldt));
			Date bdt = format.parse(birthdt);
			int result = cdt.compareTo(bdt);
			return result >= 0 ? false : true;
		} catch (ParseException e) {
			return false;
		}
	}

	public boolean isValidDateFormat(final String date, final String format) {
		try {
            SimpleDateFormat dateFormatParser = new SimpleDateFormat(format); //검증할 날짜 포맷 설정
            dateFormatParser.setLenient(false); //false일경우 처리시 입력한 값이 잘못된 형식일 시 오류가 발생
            dateFormatParser.parse(date); //대상 값 포맷에 적용되는지 확인
            return true;
        } catch (Exception e) {
            return false;
        }
	}
}
