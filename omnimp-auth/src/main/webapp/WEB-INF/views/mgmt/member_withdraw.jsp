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
			
			if ($('#password').val() === '') {
				OMNI.popup.open({
					id: "pwd_check",
					closelabel: "확인",
					closeclass:'btn_blue',
					content: "비밀번호를 입력해주세요."
				});
				$('.layer_wrap').focus();
				return;
			}
			
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
		
		//탈퇴사유 기타선택
		var withdrawal_area = $(document).find('.withdrawal_area'),
		reasons = withdrawal_area.find('input[name=i_reasons_withdrawal]');

		reasons.change(function() {
			var chk = $('#i_reasons_withdrawal_6').is(':checked');
			if (chk) {
				$("#i_reasons_withdrawal_7").show().focus();
				$('#i_reasons_withdrawal_count').show();
			} else {
				$("#i_reasons_withdrawal_7").hide();
				$('#i_reasons_withdrawal_count').hide();
			}
		});
		$('#i_reasons_withdrawal_7').on('keyup', function(e) {
			var contentsize = calcByte.getByteLength($(this).val());
			if (contentsize > 300) {
				$(this).val(calcByte.cutByteLength($(this).val(), 300));
				$('#contentsize').text(contentsize);	
			} else {
				$('#contentsize').text(contentsize);
			}
			
		});
  	});
  
    var withdraw = function() {
		var selectreason = $('input[name="i_reasons_withdrawal"]:checked').val();
		var selectcontent = $('#i_reasons_withdrawal_7').val();
		var data = {
			cpw: OMNI.auth.encode(OMNIEnv.pprs, $('#password').val()),
			reason:selectreason,
			content:selectcontent
		};

		$.ajax({
			url:OMNIEnv.ctx + '/mgmt/changeinfo/widhdraw',
			type:'post',
			data:JSON.stringify(data),
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				if (data.resultCode === '0000') {
					location.href = OMNIEnv.ctx + '/mgmt/withdrawfinish';
				} else {
					OMNI.popup.open({
						id: "pwd_check",
						closelabel: "확인",
						closeclass:'btn_blue',
						content: "비밀번호를 다시 확인해주세요."
					});
					$('.layer_wrap').focus();
				}
			},
			error: function() {
			}
			
		});
    };
    
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="회원탈퇴"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>아모레퍼시픽 뷰티포인트 통합회원 서비스를 이용하시며 불편한 점이 있으셨나요?</h2>
        <p>소중한 의견을 남겨주시면 보다 나은 서비스로 개선하는데 참고하겠습니다.</p>
      </div>
        <div class="withdrawal_area">
          <div class="box box1">
            <h3>뷰티포인트 통합회원 탈퇴</h3>
            <h3>
              뷰티포인트 통합회원 탈퇴
            </h3>
            <p>모든 서비스에 대한 약관을 철회하고 회원 탈퇴합니다.</p>
            <p class="txt_l">(뷰티포인트 웹사이트, 아모레퍼시픽 전 브랜드 및 이니스프리, 에뛰드 포함)</p>
            <dl>
              <dt>잔여 뷰티포인트</dt>
              <dd><c:out value="${point}" />P</dd>
            </dl>
          </div>
          <div class="box box2">
            <h3>탈퇴 사유를 선택해 주세요.</h3>
            <ul>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_1" name="i_reasons_withdrawal" value='99' title="광고 메일 및 문자가 귀찮아서"/>
                  <label for="i_reasons_withdrawal_1">광고 메일 및 문자가 귀찮아서</label>
                </span>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_2" name="i_reasons_withdrawal" value='02' title="개인정보 유출이 우려되서"/>
                  <label for="i_reasons_withdrawal_2">개인정보 유출이 우려되서</label>
                </span>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_3" name="i_reasons_withdrawal" value='01' title="방문했던 매장의 서비스에 불만족해서"/>
                  <label for="i_reasons_withdrawal_3">방문했던 매장의 서비스에 불만족해서</label>
                </span>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_4" name="i_reasons_withdrawal" value='03'/>
                  <label for="i_reasons_withdrawal_4">뷰티포인트 서비스를 이용하지 않아서</label>
                </span>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_5" name="i_reasons_withdrawal" value='04' title="아모레퍼시픽 제품을 더 이상 사용하지 않아서"/>
                  <label for="i_reasons_withdrawal_5">아모레퍼시픽 제품을 더 이상 사용하지 않아서</label>
                </span>
              </li>
              <li>
                <span class="radioA">
                  <input type="radio" id="i_reasons_withdrawal_6" name="i_reasons_withdrawal" value='99' title="기타"/>
                  <label for="i_reasons_withdrawal_6">기타</label>
                </span>
              </li>
            </ul>
            <textarea name="i_reasons_withdrawal" id="i_reasons_withdrawal_7" rows="4" placeholder="최대 300자 까지 입력" style="display: none;" title="기타"></textarea>
            <span id="i_reasons_withdrawal_count" style="float:right;display: none;margin-right:3px;"><span id='contentsize'>0</span>/300</span>
          </div>
          <div class="box box3">
            <h3>탈퇴 유의사항</h3>
            <ul>
              <li>뷰티포인트 통합회원 탈퇴 시 잔여 뷰티포인트와 기타 포인트가 모두 소멸되며, 유료회원으로서의 혜택도 모두 사라집니다.</li>
              <li>회원탈퇴 후 30일간 재가입이 불가하며 동일아이디는 사용할 수 없습니다.</li>
              <li>탈퇴 전 현재 배송 중인 상품이 없는지 다시 한번 확인 부탁드립니다. (탈퇴 후에는 정보 복구가 불가합니다.)</li>
              <li>기타 문의 사항은 고객상담센터(080-023-5454)로 문의해주시기 바랍니다.</li>
            </ul>
          </div>
          <div class="input_form">
            <span class="inp">
              <input type="password" class="inp_text" id='password' maxlength="16" placeholder="비밀번호 확인 (영문, 숫자, 특수문자 조합)"  title="비밀번호 확인"/>
              <button type="button" class="btn_del"><span class="blind">삭제</span></button>
            </span>
          </div>
        </div>
        <div class="btn_submit">
          <button type="button" class="btnA btn_white" id='do-withdraw'>동의하고 회원탈퇴</button>
        </div>
        <p class="txt_c">모든 유의사항을 확인하였고, 탈퇴에 동의합니다.</p>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  <input type='hidden' id='xincsno' value='<c:out escapeXml="false" value="${xincsno}" />'/>
</body>

</html>