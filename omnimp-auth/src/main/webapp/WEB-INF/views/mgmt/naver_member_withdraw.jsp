<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>회원탈퇴 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  	$(document).ready(function() {
		$('#do-withdraw').on('click', function() {
			
			OMNI.popup.open({
				id: "withdraw_pop",
				closelabel: "탈퇴하기",
				closeclass:'btn_blue',
				close: function() {
					OMNI.popup.close({ id: 'withdraw_pop' });
					withdraw();
				},
				oklabel: "취소",
				okclass: "",
				ok: function() {
					OMNI.popup.close({id:'withdraw_pop'});
				},
				content: "뷰티포인트 통합회원을 탈퇴하시겠습니까?"
			});
			$('.layer_wrap').focus();
			
		});
		
		$('#withdrawcheck').change(function() {
			disabledOnOff();
		});
		
		$("input[name=i_reasons_withdrawal_chkbox]").change(function() {
			disabledOnOff();
		});
		
		// 탈퇴사유 선택 시 서브 체크박스 초기화 및 노출
		var withdrawal_area = $(document).find('.withdrawal_area'),
		reasons = withdrawal_area.find('input[name=i_reasons_withdrawal]');

		reasons.change(function() {
			var chk4 = $('#i_reasons_withdrawal_4').is(':checked');
			var chk5 = $('#i_reasons_withdrawal_5').is(':checked');
			if (chk4) {
				$("#i_reasons_withdrawal_4_chkboxs").show().focus();
				$("#i_reasons_withdrawal_5_chkboxs").hide();
			} else if (chk5) {
				$("#i_reasons_withdrawal_4_chkboxs").hide();
				$("#i_reasons_withdrawal_5_chkboxs").show().focus();
			} else {
				$("#i_reasons_withdrawal_4_chkboxs").hide();
				$("#i_reasons_withdrawal_5_chkboxs").hide();
			}
			
			var withdrawal_chkbox = $(document).find('input[name=i_reasons_withdrawal_chkbox]');
			withdrawal_chkbox.each(function () {
				$(this).prop('checked', false);
			});
			
			disabledOnOff();
		});
  	});
  	
  	var disabledOnOff = function() {
  		var checkedRadioLength = $("input[type=radio]:checked").length;
  		var checkedRadioValue = $("input[type=radio]:checked").val();
  		var checkedReasonCheckBoxLength = $("input[name=i_reasons_withdrawal_chkbox]:checked").length;
  		var checkedWithdrawCheckBox = $('#withdrawcheck').is(':checked');
  		var reasonCondition = false;
  		
  		if(checkedRadioValue == '01' || checkedRadioValue == '02' || checkedRadioValue == '03' || checkedRadioValue == '99') {
  			reasonCondition = true;	
  		} else {
  			if(checkedReasonCheckBoxLength > 0) {
  				reasonCondition = true;
  			} else {
  				reasonCondition = false;
  			}
  		}
  		
  		var condition = checkedRadioLength == 1 && checkedWithdrawCheckBox;// && reasonCondition;
  		
  		if(condition) {
  			$('#do-withdraw').removeAttr("disabled");
  		} else {
  			$('#do-withdraw').attr("disabled", "disabled");
  		}
  	}
  
    var withdraw = function() {
		var selectreason = $('input[name="i_reasons_withdrawal"]:checked').val();
		var selectcontent = $('#i_reasons_withdrawal_7').val();
		
		$("#selectReason").val(selectreason);
		$("#selectContent").val(selectcontent);
		
		var checkedbuff = '';
		$("input[name=i_reasons_withdrawal_chkbox]:checked").each(function(idx, val) {
			var value = $(this).val();
			var checked = $(this).is(':checked');
			if (checked) {
				checkedbuff += '<input type="hidden" name="selectReasonChkBox" value="' + value + '"/>';
			}
		});
		$('#withdrawalForm').append(checkedbuff);		

		OMNI.loading.show('processing');
		
		$('#withdrawalForm')
		.attr('action', OMNIEnv.ctx + '/mgmt/withdraw/naver')
		.submit();
    };
    
	var closeAction = function() {
		location.href = OMNIEnv.ctx + '/terms/naver/callback';
	};	    
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
	<form id='withdrawalForm' method="post" action="">
	  	<input type="hidden" id="selectReason" name="selectReason" value="" />
	  	<input type="hidden" id="selectContent" name="selectContent" value="" />
  	</form>	
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="회원탈퇴" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="withdrawal_top_area bottom_line">
        <h2>정말 탈퇴 하시겠어요?</h2>
        <p>아모레퍼시픽의 뷰티포인트 서비스를 이용하시며 불편한 사항이 있으셨나요?</p>
        <p>뷰티포인트를 떠나시는 사유를 남겨주시면 보다 나은 서비스로 개선하는데 참고하겠습니다.</p>
      </div>
      <div class="withdrawal_top_area pt30">
        <h2>모든 뷰티포인트 멤버십 서비스 탈퇴</h2>
        <p class="txt_l">(아모레퍼시픽 전 브랜드 및 이니스프리, 에뛰드 포함)</p>
      </div>      
        <div class="withdrawal_area">
          <div class="box box1">
            <h3>포인트 현황</h3>
            <dl>
              <dt>잔여 뷰티포인트</dt>
              <dd><c:out value="${point}" />P</dd>
            </dl>
          </div>
          <div class="box box3">
            <h3>탈퇴 유의사항</h3>
            <ul>
              <li>모든 뷰티포인트 멤버십 서비스 탈퇴 시 잔여 뷰티포인트 마일리지가 모두 소멸되며, 유료회원 (마트 VIP, 아리따움 유료 VIP, 에뛰드 핑크패스 멤버십, 헬로우 캠퍼스 멤버십) 으로서의 혜택도 모두 사라집니다.</li>
              <li>회원탈퇴 후 30일간 재가입이 불가하며 동일 아이디는 사용할 수 없습니다.</li>
              <li>회원님의 잔여 포인트는 탈퇴 후 사용이 불가능하오니 신중하게 결정하시기 바랍니다.</li>
              <li>탈퇴 전, 현재 배송중인 상품 또는 뷰티포인트 교환신청 내역이 없는지 다시 한 번 확인 부탁드립니다. 탈퇴 후에는 정보 복구가 불가합니다.</li>
            </ul>
          </div>
          <div class="box box2">
            <h3>탈퇴 사유를 선택해 주세요.</h3>
            <ul>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_1" name="i_reasons_withdrawal" value='02' title="개인정보 유출이 우려되서"/>
                  <label for="i_reasons_withdrawal_1">개인정보 유출이 우려되서</label>
                </span>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_2" name="i_reasons_withdrawal" value='01' title="광고 메일 및 문자가 귀찮아서"/>
                  <label for="i_reasons_withdrawal_2">광고 메일 및 문자가 귀찮아서</label>
                </span>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_3" name="i_reasons_withdrawal" value='03' title="아모레퍼시픽 제품을 더 이상 사용하지 않아서"/>
                  <label for="i_reasons_withdrawal_3">아모레퍼시픽 제품을 더 이상 사용하지 않아서</label>
                </span>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_4" name="i_reasons_withdrawal" value='03'/>
                  <label for="i_reasons_withdrawal_4">뷰티포인트 서비스를 이용하지 않아서</label>
                </span>
		        <div id="i_reasons_withdrawal_4_chkboxs" class="user_info mt13" style="display: none;">
		          <h4>이유를 선택해 주세요. (복수 응답 가능)</h4>
	               <ul>
	                <li>
	                  <span class="checkboxA">
	                    <input type="checkbox" id="i_reasons_withdrawal_4_1" name="i_reasons_withdrawal_chkbox" value='04-01' title="기타"/>
	                    <label for="i_reasons_withdrawal_4_1"><span class="checkbox_label">기타</span></label>
	                  </span>
	                </li>
	                <li>
	                  <span class="checkboxA">
	                    <input type="checkbox" id="i_reasons_withdrawal_4_2" name="i_reasons_withdrawal_chkbox" value='04-02' title="적립률이 낮아서"/>
	                    <label for="i_reasons_withdrawal_4_2"><span>적립률이 낮아서</span></label>
	                  </span>
	                </li>
	                <li>
	                  <span class="checkboxA">
	                    <input type="checkbox" id="i_reasons_withdrawal_4_3" name="i_reasons_withdrawal_chkbox" value='04-03' title="혜택이 많지 않아서"/>
	                    <label for="i_reasons_withdrawal_4_3"><span>혜택이 많지 않아서</span></label>
	                  </span>
	                </li>
	              </ul>
		        </div>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_5" name="i_reasons_withdrawal" value='01' title="방문했던 매장의 서비스에 불만족해서"/>
                  <label for="i_reasons_withdrawal_5">방문했던 매장의 서비스에 불만족해서</label>
                </span>
		        <div id="i_reasons_withdrawal_5_chkboxs" class="user_info mt13" style="display: none;">
		          <h4>이유를 선택해 주세요. (복수 응답 가능)</h4>
	               <ul>
	                <li>
	                  <span class="checkboxA">
	                    <input type="checkbox" id="i_reasons_withdrawal_5_1" name="i_reasons_withdrawal_chkbox" value='05-01' title="판매한 제품의 상태"/>
	                    <label for="i_reasons_withdrawal_5_1"><span class="checkbox_label">판매한 제품의 상태</span></label>
	                  </span>
	                </li>
	                <li>
	                  <span class="checkboxA">
	                    <input type="checkbox" id="i_reasons_withdrawal_5_2" name="i_reasons_withdrawal_chkbox" value='05-02' title="판매사원의 태도"/>
	                    <label for="i_reasons_withdrawal_5_2"><span>판매사원의 태도</span></label>
	                  </span>
	                </li>
	                <li>
	                  <span class="checkboxA">
	                    <input type="checkbox" id="i_reasons_withdrawal_5_3" name="i_reasons_withdrawal_chkbox" value='05-03' title="매장의 청결 상태"/>
	                    <label for="i_reasons_withdrawal_5_3"><span>매장의 청결 상태</span></label>
	                  </span>
	                </li>
	                <li>
	                  <span class="checkboxA">
	                    <input type="checkbox" id="i_reasons_withdrawal_5_4" name="i_reasons_withdrawal_chkbox" value='05-04' title="재고가 없어서"/>
	                    <label for="i_reasons_withdrawal_5_4"><span>재고가 없어서</span></label>
	                  </span>
	                </li>
	              </ul>
		        </div>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_6" name="i_reasons_withdrawal" value='99' title="기타"/>
                  <label for="i_reasons_withdrawal_6">기타</label>
                </span>
              </li>
            </ul>
            <div class="etc_area top_line">
            	<p>그 외 남기고 싶은 말씀</p>
            	<textarea class="etc_text" name="i_reasons_withdrawal" id="i_reasons_withdrawal_7" rows="4" placeholder="최대 300자 까지 입력" ></textarea>
            </div>            
          </div>
		  <div class="reg_withdraw_id">
            <span class="checkboxA">
              <input type="checkbox" id="withdrawcheck" name="withdrawcheck""/>
              <label for="withdrawcheck"><span class="checkbox_label">위 유의사항을 모두 확인하였으며, 뷰티포인트 통합 회원탈퇴에 동의합니다.</span></label>
            </span>
          </div>
        </div>
        <div class="btn_submit">
          <button type="button" class="btnA btn_blue" disabled id='do-withdraw'>회원탈퇴</button>
        </div>
        <p class="txt_c">네이버 계정으로 본인 확인 후 회원탈퇴 처리 됩니다.</p>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>