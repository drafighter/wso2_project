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
 *    |_ TaskVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 30.
 * @version : 1.0
 * @author  : takkies
 */
@Getter
@Setter
public class TaskVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1261721266781821528L;
//	private String taskId;
//	private String taskName;
//	private int taskSeq;
//	private double taskExecuteSecs;
//	private long taskExecuteMillis;
//	private String taskRatio;
//	private Timestamp taskDt;
	
	// 테이블 스키마 변경
	private int mbpfUnitBsnsExctLogNo; //MBPF_UNIT_BSNS_EXCT_LOG_NO numeric(22) NOT NULL 
    private String mbpfUnitBsnsNo; //MBPF_UNIT_BSNS_NO varchar(50) NOT NULL 
    private String mbpfUnitBsnsNm; //MBPF_UNIT_BSNS_NM varchar(300) 
    private double mbpfUnitBsnsExctNss; //MBPF_UNIT_BSNS_EXCT_NSS numeric(10) 
    private long mbpfUnitBsnsExctNmss; //, MBPF_UNIT_BSNS_EXCT_NMSS numeric(10) 
    private String mbpfUnitBsnsExctPrt; //MBPF_UNIT_BSNS_EXCT_PRT numeric(5) 
    private Timestamp mbpfUnitBsnsExctBgnDttm; //MBPF_UNIT_BSNS_EXCT_BGN_DTTM timestamp NOT NULL 
    private String fscrId; //, FSCR_ID varchar(50) NOT NULL 
    private String lschId; //, LSCH_ID varchar(50) NOT NULL 

}
