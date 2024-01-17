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
 * Date   	          : 2020. 11. 30..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ ProcessVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 30.
 * @version : 1.0
 * @author  : takkies
 */
@Getter
@Setter
public class ProcessVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1574514427047894150L;
//	private String prcId;
//	private String prcName;
//	private String prcClass;
//	private String prcMethod;
//	private String prcType;
//	private String prcResult;
//	private String prcMsg;
//	private String prcInput;
//	private String prcOutput;
//	private Timestamp prcDt;
	
	// 테이블 스키마 변경
	private String mbpfPrceNo; //MBPF_PRCE_NO varchar(50) NOT NULL 
    private String mbpfPrceNm; //MBPF_PRCE_NM varchar(300) 
    private String mbpfJvcsNm; // MBPF_JVCS_NM varchar(300) 
    private String mbpfJvmtNm; //MBPF_JVMT_NM varchar(300) 
    private String mbpfPrceTpCd; //MBPF_PRCE_TP_CD varchar(1) DEFAULT 'C' NOT NULL 
    private String mbpfPrceExctRsltCd; //MBPF_PRCE_EXCT_RSLT_CD varchar(100) 
    private String mbpfPrceExctRsltMsg; //MBPF_PRCE_EXCT_RSLT_MSG varchar(2000) 
    private String inParmLv; //IN_PARM_LV varchar(4000) 
    private String tlmsParmLv; //TLMS_PARM_LV varchar(2000) 
    private Timestamp mbpfPrceExctBgnDttm; //, MBPF_PRCE_EXCT_BGN_DTTM timestamp NOT NULL 
    private String fscrId; //FSCR_ID varchar(50) NOT NULL 
    private String lschId; //LSCH_ID varchar(50) NOT NULL 

}
