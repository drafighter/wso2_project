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
 * Date   	          : 2020. 7. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionInitializationException;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.emp.PartnerVo;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.util 
 *    |_ ConfigUtil.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 21.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
public class ConfigUtil {

	private ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder;

	public static class Singleton {
		private static final ConfigUtil instance = new ConfigUtil();
	}

	public static ConfigUtil getInstance() {
		return Singleton.instance;
	}

	private ConfigUtil() {
		this.init();
	}

	private void init() {

		Resource rs = new ClassPathResource("config/config-static.xml");
		try {
			builder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(XMLConfiguration.class) //
					.configure(new Parameters().fileBased().setURL(rs.getURL()));
			// 5 분에 한번씩 체크
			PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null, 5, TimeUnit.MINUTES);
			trigger.start();
			log.info(String.format("oneap configs \n%s", this.printConfig()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public String getString(final String key, final String defaultValue) {
		final Configuration config = getCompositeConfiguration();
		return config == null ? defaultValue : config.getString(key, defaultValue);
	}

	public String getString(final String key) {
		return getString(key, "");
	}
	
	public List<Object> getList(final String key) {
		return getList(key, Collections.emptyList());
	}
	
	public List<Object> getList(final String key, List<Object> defaultValues) {
		final Configuration config = getCompositeConfiguration();
		return config == null ? Collections.emptyList() : config.getList(key, defaultValues);
	}
	
	public String getJasyptPassword() {
		return getString("security.jasypt.password");
	}
	
	public String getJasyptPrefix() {
		return getString("security.jasypt.prefix");
	}
	
	public String getJasyptSuffix() {
		return getString("security.jasypt.suffix");
	}

	public String getPassphrase() {
		return getString("security.passphrase");
	}
	
	public String getDecryptPassphrase() {
		String passphrase = getPassphrase();
		if (passphrase.startsWith("ENC(")) {
			passphrase = passphrase.substring("ENC(".length());
		} else {
			return passphrase;
		}
		if (passphrase.endsWith(")")) {
			passphrase = passphrase.substring(0, passphrase.length() - 1);
		} else {
			return passphrase;
		}
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(getString("security.passsalt"));
			return encryptor.decrypt(passphrase);
		} catch (EncryptionOperationNotPossibleException | EncryptionInitializationException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getDecryptString(final String key) {
		String encodevalue = getString(key);
		if (encodevalue.startsWith("ENC(")) {
			encodevalue = encodevalue.substring("ENC(".length());
		} else {
			return encodevalue;
		}
		if (encodevalue.endsWith(")")) {
			encodevalue = encodevalue.substring(0, encodevalue.length() - 1);
		} else {
			return encodevalue;
		}
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(getString("security.passsalt"));
			return encryptor.decrypt(encodevalue);
		} catch (EncryptionOperationNotPossibleException | EncryptionInitializationException e) {
			// NOPMD
		} catch (Exception e) {
			// NO PMD
		}
		return null;
	}
	
	public int getInt(final String key, final int defaultValue) {
		final Configuration config = getCompositeConfiguration();
		return config == null ? defaultValue : config.getInt(key, defaultValue);
	}

	public int getInt(final String key) {
		return getInt(key, 0);
	}

	public long getLong(final String key, final long defaultValue) {
		final Configuration config = getCompositeConfiguration();
		return config == null ? defaultValue : config.getLong(key, defaultValue);
	}

	public long getLong(final String key) {
		return getLong(key, 0);
	}

	public boolean getBoolean(final String key, final boolean defaultValue) {
		final Configuration config = getCompositeConfiguration();
		return config == null ? defaultValue : config.getBoolean(key, defaultValue);
	}
	
	public boolean isXValueTimeCheckEndable() {
		return getBoolean("security.xvalue.timecheck.enable", false);	
	}
	
	public int getXValueTimeCheckTerms() {
		return getInt("security.xvalue.timecheck.terms", 5);
	}
	
	public String getJoinPrtnCode(final String chCd) {
		return getString("joinprtn.chcd" + chCd + ".code", "");
	}
	
	public String getJoinPrtnName(final String chCd) {
		return getString("joinprtn.chcd" + chCd + ".name", "");
	}
	
	public String getJoinEmpCode(final String chCd) {
		return getString("joinprtn.chcd" + chCd + ".empcode", "");
	}
	
	public boolean isJoinPrtnCodeRequired(final String chCd) {
		return getBoolean("joinprtn.chcd" + chCd + ".codereq", false);
	}
	
	public boolean isJoinEmpCodeRequired(final String chCd) {
		return getBoolean("joinprtn.chcd" + chCd + ".empcodereq", false);
	}
	
	public String getChannelApi(final String chCd, final String type, final String profile) {
		String key = "api.chcd".concat(chCd).concat(".").concat(profile).concat(".").concat(type);
		return getString(key, "");
	}
	
	public String getChannelInitUrl(final String chCd, final String profile) {
		String key = "api.chcd".concat(chCd).concat(".").concat(profile).concat(".initurl");
		return getString(key, "");
	}
	
	public String getChannelLoginUrl(final String chCd, final String profile) {
		String key = "api.chcd".concat(chCd).concat(".").concat(profile).concat(".loginurl");
		return getString(key, "");
	}
	
	public String getChannelJoinRedirect(final String chCd, final String profile) {
		return getString("joinfinish." + profile + ".chcd" + chCd);
	}
	
	public String getSnsInfo(final String profile, final String snsType, final String key) {
		String snsKey = "sns.".concat(snsType).concat(".").concat(profile).concat(".").concat(key);
		return getString(snsKey, "");
	}
	
	public boolean isChannelNonMemberEnable(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".nonmember"), "true");
		return StringUtil.isTrue(flag);
	}
	
	public boolean isChannelNonMemberParam(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".nonmemberparam"), "false");
		return StringUtil.isTrue(flag);
	}
	
	public String isChannelNonMemberName(final String chCd, final String profile) {
		return getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".nonmembername"), "");
	}	
	
	public boolean isMarketingSyncBpEnable(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".marketingsyncbp"), "false");
		return StringUtil.isTrue(flag);
	}
	
	public boolean isBrandSite(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".brandsite"), "false");
		return StringUtil.isTrue(flag);
	}
	
	public boolean isSnsSignUpEnable(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".issnssignup"), "true");
		return StringUtil.isTrue(flag);
	}
	
	public String getJoinChCd(final String chCd, final String profile) {
		String key = "api.chcd".concat(chCd).concat(".").concat(profile).concat(".joinchcd");
		return getString(key, OmniConstants.JOINON_CHCD);
	}
	
	public String getMarketingChCd(final String chCd, final String profile) {
		String key = "api.chcd".concat(chCd).concat(".").concat(profile).concat(".marketingchcd");
		return getString(key, "");
	}
	
	public boolean isEcpApi(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".isecpapi"), "false");
		return StringUtil.isTrue(flag);
	}
	
	public boolean isConfirmBtn(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".isconfirmbtn"), "false");
		return StringUtil.isTrue(flag);
	}
	
	public boolean isLoginBtn(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".isloginbtn"), "false");
		return StringUtil.isTrue(flag);
	}
	
	public boolean isLoginPageHeader(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".isloginheader"), "true");
		return StringUtil.isTrue(flag);
	}
	
	public boolean isThirdPartyConsent(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".thirdpartyconsent"), "true");
		return StringUtil.isTrue(flag);	
	}
	
	public boolean isOfflineLiveApi(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".offliveapi"), "false");
		return StringUtil.isTrue(flag);	
	}
	
	public boolean isOfflineLogin(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".offlinelogin"), "false");
		return StringUtil.isTrue(flag);
	}
	
	public String getOfflineLoginUrl(final String chCd, final String profile) {
		String key = "api.chcd".concat(chCd).concat(".").concat(profile).concat(".offlineloginurl");
		return getString(key, "");
	}	
	
	public String isAppLogin(final String chCd, final String profile) {//web2app 로그인 설정 
		return getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".applogin"), "");
	}
	
	public String isAppLoginPath(final String chCd, final String profile) { //타 앱으로 web2app 로그인 시 설정 : 채널 코드 입력
		return getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".apploginpath"), "");
	}
	
	public int isWeb2AppExpireTime(final String profile) {
		return getInt("web2app.expiretime".concat(profile), 1);	
	}
	
	public String isHeaderType(final String chCd, final String profile) { //headertype : cancelbtn(버튼 모두 cancelurl)
		return getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".headertype"), "");
	}
	
	public boolean isOfflineKakaoSyncEnable(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".iskakaosync"), "false");
		return StringUtil.isTrue(flag);
	}
	
	public String getTermsTags() {	// 카카오에 약관 요청할때 목록
		String key = "snsterms.tags";
		return getString(key, "");
	}
	
	public String getMarketingTermsTag(final String chCd) {	// 카카오에 마케팅 약관
		String key = "snsterms.marketing.ch" + chCd;
		return getString(key, "");
	}
	
	public String getPrivacyTermsTag(final String chCd) {	// 카카오에 개인정보 제공동의 약관
		String key = "snsterms.privacy.ch" + chCd;
		return getString(key, "");
	}	
	
	public String getYearsTermsTags() {	// 카카오에 연령 동의 약관
		String key = "snsterms.years";
		return getString(key, "");
	}
	
	public String getTermsCode(final String termsTag) { // 약관 동의 확인 용 (옴니 약관 코드)
		String key = "snsterms.".concat(termsTag).concat(".code");
		return getString(key, "");
	}
	
	public String getTermsVersion(final String termsTag) { // 약관 동의 확인 용 (옴니 약관 버전)
		String key = "snsterms.".concat(termsTag).concat(".version");
		return getString(key, "");
	}
	
	public String getChPublicId(final String profile, final String chCd) { // 경로별 카카오톡 수신동의
		String key = "snspublicids." + profile + ".ch" + chCd;
		return getString(key, "");
	}
	
	public String skipResources() {
		return getString("common.skip.resources", ".*.png|.*.jpg|.*.gif|.*.ico|.*.properties");
	}
	
	public String resourceVersion() {
		return getString("common.resoure.version", "v20200924");
	}
	
	public String commonAuthType() {
		return getString("common.auth.type", "POST");
	}
	
	public List<Object> apiKeys() {
		List<Object> defaults = new ArrayList<>();
		defaults.add("IBweveYghKeE439odiNwcw==");
		return getList("common.api.key", defaults);
	}
	
	public String apiKey() {
		List<Object> apikeys = apiKeys();
		return apikeys.get(0).toString();
	}
	
	public String defaultApiKey() {
		List<Object> apikeys = apiKeys();
		int idx = 0;
		for (Object apikey : apikeys) {
			boolean defaultkey = getBoolean("common.api.key(" + idx + ")[@defaultkey]", false);
			if (defaultkey) {
				return apikey.toString();
			}
			idx++;
		}
		return "IBweveYghKeE439odiNwcw==";
	}
	
	public int getChangePasswordTerm() {
		return getInt("wso2.password.changeterms", -1);
	}
	
	public boolean avaiableManualCert(final String profile) {
		final String cert = getString("common." + profile + ".manualcert", "false");
		return StringUtil.isTrue(cert);
	}
	
	public boolean isServiceLogging(final String profile) {
		final String logging = getString("common." + profile + ".svclogging", "false");
		return StringUtil.isTrue(logging);
	}
	
	public boolean isProcessLogging(final String profile) {
		final String logging = getString("common." + profile + ".prclogging", "false");
		return StringUtil.isTrue(logging);
	}
	
	public List<Object> snsTypes() {
		List<Object> defaults = new ArrayList<>();
		defaults.add("KA");
		defaults.add("AP");
		defaults.add("NA");
		defaults.add("FB");
		defaults.add("LOCAL");
		return getList("common.sns.type", defaults);
	}
	
	public List<Object> getBpChannelCodes() {
		List<Object> defaults = new ArrayList<>();
		defaults.add("030");
		defaults.add("031");
		defaults.add("043");
		defaults.add("070");
		defaults.add("100");
		return getList("common.bp.code.chcd", defaults);
	}
	
	public List<Object> getNaverAfltChannelCodes() {
		List<Object> defaults = new ArrayList<>();
		defaults.add("402");
		defaults.add("403");
		defaults.add("404");
		defaults.add("405");
		return getList("common.naver.affiliate.chcd", defaults);	
	}
	
	public String getBpChannelName(final String chCd) {
		return getString("common.bp.chcd"+chCd+".prtnnm", "AMOREPACIFIC");
	}
	
	public boolean isParamEncoding() {
		return getBoolean("common.param.encoding", false);
	}
	
	public boolean isSmsTestSend(final String profile) {
		return getBoolean("common." + profile + ".sms.testsend", false);
	}
	
	public boolean isSmsSystemCheck(final String profile) {
		return getBoolean("common." + profile + ".sms.systemcheck", false);
	}
	
	public String getFacebookRemoveCallbackUrl(final String profile, final String type) {
		String key = "sns.fb.".concat(profile).concat(".").concat(type);
		return getString(key, "");
	}
	
	public String getSessionChCd() {
		return WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
	}
	
	private Configuration getCompositeConfiguration() {
		try {
			return builder.getConfiguration();
		} catch (ConfigurationException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private String printConfig() {
		final Configuration config = getCompositeConfiguration();
		StringBuilder s = new StringBuilder();
		Iterator<String> keys = config.getKeys();
		String key;
		String val;
		while (keys.hasNext()) {
			key = keys.next();
			if (key.contains("pass")) {
				val = "<masked>";
			} else {
				val = StringUtils.collectionToDelimitedString(config.getList(key), ", ");
			}
			s.append(String.format("%s %s", StringUtil.padRight(key + " ", 35, "-"), val)).append("\n");
		}
		return s.toString();
	}
	
	public String getEmpMailConfirmUrl(final String profile) {
		return getString("emp.mail.confirm.url." + profile, "");
	}
	
	public String getAesEncryptionKey() {
		return getString("security.aes.encryptionkey", "");
	}
	
	public String getMembershipId(final String chCd, final String profile) {
		return getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".membershipid"), "");
	}
	
	public boolean isMembershipOpenApi(final String chCd, final String profile) {
		final String flag = getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".ismembershipopenapi"), "false");
		return StringUtil.isTrue(flag); 
	}
	
	public String getAddParam(final String chCd, final String profile, final String type) {
		return getString("api.chcd".concat(chCd).concat(".").concat(profile).concat(".addparam").concat(type), "");
	}
	
	public String getKakaoNoticePrtnNm(final String chCd) {
		return getString("kakao.notice.chcd".concat(chCd).concat(".").concat("prtnname"), "");
	}
	
	public String getKakaoNoticeId(final String chCd) {
		return getString("kakao.notice.chcd".concat(chCd).concat(".").concat("id"), "");
	}
	
	public String getKakaoNoticeTemplateCode(final String chCd) {
		return getString("kakao.notice.chcd".concat(chCd).concat(".").concat("templatecode"), "");
	}
}
