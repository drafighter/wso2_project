<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- terms apply -->
<html lang="ko">
<head>
  <title>뷰티포인트 X 네이버 스마트 스토어 개인정보 동의 현황</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
	$(document).ready(function() {
		$('#req-withdraw').on('click', function() {
			location.href = OMNIEnv.ctx + '/mgmt/withdraw';
		});
		
		$('.btn_submit .btnA').on('click', function() {
			var $chCd = $(this).data('chcd');
			var $dataType = $(this).data('type');
			var $tcatCd = $(this).data('tcatcd');
			if($dataType == 'C') {
				$("#returnUrl").val("/terms/naver");
			} else {
				$("#returnUrl").val("/terms/naver/callback");
			}
			OMNI.popup.open({
				id:'next-warn',
				content: '선택하신 약관을 철회하시겠습니까?',
				oklabel:'취소',
				okclass:'btn_white',
				ok: function() {
					OMNI.popup.close({id:'next-warn'});
				},
				closelabel:'확인',
				closeclass:'btn_blue',
				close: function() {
					OMNI.popup.close({id:'next-warn'});
					$('#chCd').val($chCd);
					$('#dataType').val($dataType);
					$('#tcatCd').val($tcatCd);
					$('#termsForm').attr('action', OMNIEnv.ctx + '/terms/retraction').submit();
				}
			});
		});
		
		  <c:choose>
		  	<c:when test="${naverUnLinkVo.resultCode ne '0000' and !empty naverUnLinkVo.resultMessage}">
			OMNI.popup.open({
				id:'naver-membership-unlink-fail-waring',
				content:'<c:out value="${naverUnLinkVo.resultMessage}" escapeXml="false"/>',
				closelabel:'확인',
				closeclass:'btn_blue',
				close:function() {
					OMNI.popup.close({ id: 'naver-membership-unlink-fail-waring' });
					location.href = OMNIEnv.ctx + '/terms/naver';
				}
			});	  	
		  	</c:when>
		  </c:choose>		
	});
	var closeAction = function() {
		location.href = OMNIEnv.ctx + '/terms/naver';
	};	
  </script>
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-terms.js?ver=<c:out value="${rv}"/>"></script>
</head>
<body>
  <div id='agree-contents' style='display:none'></div>
  <form id='termsForm' method="post" action="">
  	<input type="hidden" id="chCd" name="chCd" value="<c:out value="${chCd}" />" />
  	<input type="hidden" id="returnUrl" name="returnUrl" value="" />
  	<input type="hidden" id="xincsNo" name="xincsNo" value="<c:out value="${naverUnLinkVo.xincsNo}" />" />
  	<input type="hidden" id="dataType" name="dataType" value="" />
  	<input type="hidden" id="tcatCd" name="tcatCd" value="" />
  </form>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2 style="text-align: center;">뷰티포인트 X 네이버 스마트 스토어<br/>개인정보 동의 현황</h2>
      </div>
      
	<c:if test="${naverUnLinkVo.resultCode eq '0000'}">
		<c:forEach items="${naverUnLinkVo.cicuedCuTncaTcVo}" var="cicuedCuTncaTcVo" varStatus="status">
			<c:if test="${cicuedCuTncaTcVo.tcatCd eq '010' and cicuedCuTncaTcVo.tncAgrYn eq 'Y'}">
   				<div class="unlink_area">
   					<fmt:parseDate var="dateFmt" pattern="yyyy-MM-dd HH:mm:ss" value="${cicuedCuTncaTcVo.tncaDttm}"/>
   					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
					<div class="agree_title">
						<ul>
							<li>
			         			<h3>[필수] 뷰티포인트 기본 정보</h3>
				         		<a href="javascript:;" class="btn_link_bp" data-type='S' data-chcd='<c:out escapeXml="false" value="${chCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (필수) '뷰티포인트 서비스 이용 약관' 상세 보기 버튼" ap-click-data="(필수) '뷰티포인트 서비스 이용 약관' 상세 보기 버튼">
				         		<span class="blind">자세히보기</span></a>							
							</li>
						</ul>	         			
	         		</div>
	         		<div class="user_info">
						<dl class="dt_w33">
			          		<dt>개인정보 항목</dt>
			          		<dd>성명, 생년월일, 성별, 휴대전화번호, 본인확인 정보</dd>
			       	 	</dl>
			       	 	<dl class="dt_w33">
			          		<dt>동의일</dt>
			          		<dd><c:out value="${dateParse}"/></dd>
			          		<dd></dd>
			       	 	</dl>
	         		</div>
	         	</div>
			</c:if>
		</c:forEach>
      
		<c:forEach items="${naverUnLinkVo.cicuedCuTncaTcVo}" var="cicuedCuTncaTcVo" varStatus="status">
			<c:if test="${cicuedCuTncaTcVo.tcatCd eq '030' and cicuedCuTncaTcVo.tncAgrYn eq 'Y' and naverUnLinkVo.naverLinked}">
   				<div class="unlink_area">
   					<fmt:parseDate var="dateFmt" pattern="yyyy-MM-dd HH:mm:ss" value="${cicuedCuTncaTcVo.tncaDttm}"/>
   					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
					<div class="agree_title">
						<ul>
							<li>
			         			<h3>[필수] 개인정보 제공 동의 (네이버 → 아모레퍼시픽)</h3>
				         		<a href="javascript:;" class="btn_link_bp" data-type='C' data-chcd='<c:out escapeXml="false" value="${chCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (필수) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼" ap-click-data="(필수) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
				         		<span class="blind">자세히보기</span></a>							
							</li>
						</ul>	         			
	         		</div>	         		
	         		<div class="user_info">
						<dl class="dt_w33">
			          		<dt>제공받은 개인정보<br/>항목</dt>
			          		<dd>성명, 생년월일, 성별, 휴대전화번호, 본인확인 정보</dd>
			       	 	</dl>
			       	 	<dl class="dt_w33">
			          		<dt>동의일</dt>
			          		<dd><c:out value="${dateParse}"/></dd>
			       	 	</dl>
	         		</div>
		          	<div class="btn_submit mt13">
		            	<button type="button" class="btnA btn_white" data-type="C" data-chcd='<c:out escapeXml="false" value="${chCd}" />'>동의 철회하기</button>
		          	</div>
		        </div>	         		
			</c:if>
		</c:forEach>      			
      
		<c:forEach items="${naverUnLinkVo.cicuedCuTncaTcVo}" var="cicuedCuTncaTcVo" varStatus="status">
			<c:if test="${cicuedCuTncaTcVo.tcatCd eq '050' and cicuedCuTncaTcVo.tncAgrYn eq 'Y'}">
   				<div class="unlink_area">
   					<fmt:parseDate var="dateFmt" pattern="yyyy-MM-dd HH:mm:ss" value="${cicuedCuTncaTcVo.tncaDttm}"/>
   					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
					<div class="agree_title">
						<ul>
							<li>
			         			<h3>[선택] 이벤트/프로모션 안내</h3>
				         		<a href="javascript:;" class="btn_link_bp" data-type='CO' data-chcd='<c:out escapeXml="false" value="${chCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 이용 및 수집에 대한 동의 상세 보기 버튼" ap-click-data="(선택) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
				         		<span class="blind">자세히보기</span></a>							
							</li>
						</ul>	         			
	         		</div>
	         		<div class="user_info">
						<dl class="dt_w33">
			          		<dt>개인정보 항목</dt>
			          		<dd>휴대전화번호</dd>
			       	 	</dl>
			       	 	<dl class="dt_w33">
			          		<dt>동의일</dt>
			          		<dd><c:out value="${dateParse}"/></dd>
			       	 	</dl>
	         		</div>
		          	<div class="btn_submit mt13">
		            	<button type="button" class="btnA btn_white" data-type="CO" data-chcd='<c:out escapeXml="false" value="${chCd}" />' >동의 철회하기</button>
		          	</div>
		        </div>	         		
			</c:if>
		</c:forEach>      			
	  
		<c:forEach items="${naverUnLinkVo.afltChCicuemCuOptiQcVoList}" var="afltChCicuemCuOptiQcVo" varStatus="status">
			<c:if test="${afltChCicuemCuOptiQcVo.chCd eq '401' and afltChCicuemCuOptiQcVo.smsOptiYn eq 'Y'}">
	  			<div class="unlink_area">
					<fmt:parseDate var="dateFmt" pattern="yyyyMMdd" value="${afltChCicuemCuOptiQcVo.smsOptiDt}"/>
					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
					<div class="agree_title">
	         			<h3>[선택] 광고성 정보 SMS 수신 동의</h3>
	         		</div>
	         		<div class="user_info">
			       	 	<dl class="dt_w33">
			          		<dt>동의일</dt>
			          		<dd><c:out value="${dateParse}"/></dd>
			       	 	</dl>
	         		</div>
		          	<div class="btn_submit mt13">
		            	<button type="button" class="btnA btn_white" data-chcd='<c:out escapeXml="false" value="${afltChCicuemCuOptiQcVo.chCd }"/>' data-type="smsOptiYn">동의 철회하기</button>
		          	</div>	    
		        </div>				
			</c:if>
		</c:forEach>
		
		<c:forEach items="${naverUnLinkVo.naverMembershipTermsVoList}" var="naverMembershipTermsVo" varStatus="status">
			<c:if test="${naverMembershipTermsVo.chCd eq '402'}">
				<c:forEach items="${naverUnLinkVo.cicuedCuTncaTcVo}" var="cicuedCuTncaTcVo" varStatus="status">
					<c:if test="${naverMembershipTermsVo.prcnTcatCd eq cicuedCuTncaTcVo.tcatCd and cicuedCuTncaTcVo.tncAgrYn eq 'Y'}">
		   				<div class="unlink_area">
		   					<fmt:parseDate var="dateFmt" pattern="yyyy-MM-dd HH:mm:ss" value="${cicuedCuTncaTcVo.tncaDttm}"/>
		   					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
							<div class="agree_title">
								<ul>
									<li>
					         			<h3>[<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />]&nbsp;<c:out escapeXml="false" value="${naverMembershipTermsVo.tncTtl}" /></h3>
					         			<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
							         		<a href="javascript:;" class="btn_link_bp" data-type='O' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의 상세 보기 버튼" ap-click-data="(<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
							         		<span class="blind">자세히보기</span></a>
						         		</c:if>
					         			<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
							         		<a href="javascript:;" class="btn_link_bp" data-type='N' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의 상세 보기 버튼" ap-click-data="(<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
							         		<span class="blind">자세히보기</span></a>
						         		</c:if>
									</li>
								</ul>	         			
			         		</div>
			         		<div class="user_info">
								<dl class="dt_w33">
									<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
										<dt>개인정보 항목</dt>
										<dd>네이버 및 아모레퍼시픽 멤버십 회원 연동정보, 구매내역, 구매금액, 구매상품</dd>
									</c:if>
									<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
										<dt>개인정보 항목</dt>
										<dd>성명, 본인확인 값, 생년월일, 내/외국인 여부, 성별, 휴대전화번호, 구매 거래 내역, 뷰티포인트 내역, 이메일주소, 주소</dd>
									</c:if>
					       	 	</dl>
					       	 	<dl class="dt_w33">
					          		<dt>동의일</dt>
					          		<dd><c:out value="${dateParse}"/></dd>
					       	 	</dl>
			         		</div>
				          	<div class="btn_submit mt13">
				          		<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
									<button type="button" class="btnA btn_white" data-type='O' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />'>동의 철회하기</button>				          		
				          		</c:if>
				          		<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
				          			<button type="button" class="btnA btn_white" data-type='N' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />'>동의 철회하기</button>
				          		</c:if>
				          	</div>
				        </div>	         		
					</c:if>
				</c:forEach> 
			</c:if>
		</c:forEach>
		
		<c:forEach items="${naverUnLinkVo.afltChCicuemCuOptiQcVoList}" var="afltChCicuemCuOptiQcVo" varStatus="status">
			<c:if test="${afltChCicuemCuOptiQcVo.chCd eq '402' and afltChCicuemCuOptiQcVo.smsOptiYn eq 'Y'}">
	  			<div class="unlink_area">
					<fmt:parseDate var="dateFmt" pattern="yyyyMMdd" value="${afltChCicuemCuOptiQcVo.smsOptiDt}"/>
					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
					<div class="agree_title">
	         			<h3>[선택] 이니스프리 광고성 정보 SMS 수신 동의</h3>
	         		</div>
	         		<div class="user_info">
			       	 	<dl class="dt_w33">
			          		<dt>동의일</dt>
			          		<dd><c:out value="${dateParse}"/></dd>
			       	 	</dl>
	         		</div>
		          	<div class="btn_submit mt13">
		            	<button type="button" class="btnA btn_white" data-chcd='<c:out escapeXml="false" value="${afltChCicuemCuOptiQcVo.chCd }"/>' data-type="smsOptiYn">동의 철회하기</button>
		          	</div>	    
		        </div>				
			</c:if>
		</c:forEach>
		
		<c:forEach items="${naverUnLinkVo.naverMembershipTermsVoList}" var="naverMembershipTermsVo" varStatus="status">
			<c:if test="${naverMembershipTermsVo.chCd eq '403'}">
				<c:forEach items="${naverUnLinkVo.cicuedCuTncaTcVo}" var="cicuedCuTncaTcVo" varStatus="status">
					<c:if test="${naverMembershipTermsVo.prcnTcatCd eq cicuedCuTncaTcVo.tcatCd and cicuedCuTncaTcVo.tncAgrYn eq 'Y'}">
		   				<div class="unlink_area">
		   					<fmt:parseDate var="dateFmt" pattern="yyyy-MM-dd HH:mm:ss" value="${cicuedCuTncaTcVo.tncaDttm}"/>
		   					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
							<div class="agree_title">
								<ul>
									<li>
					         			<h3>[<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />]&nbsp;<c:out escapeXml="false" value="${naverMembershipTermsVo.tncTtl}" /></h3>
					         			<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
							         		<a href="javascript:;" class="btn_link_bp" data-type='O' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의 상세 보기 버튼" ap-click-data="(<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
							         		<span class="blind">자세히보기</span></a>
						         		</c:if>
					         			<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
							         		<a href="javascript:;" class="btn_link_bp" data-type='N' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의 상세 보기 버튼" ap-click-data="(<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
							         		<span class="blind">자세히보기</span></a>
						         		</c:if>
									</li>
								</ul>	         			
			         		</div>
			         		<div class="user_info">
								<dl class="dt_w33">
									<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
										<dt>개인정보 항목</dt>
										<dd>네이버 및 아모레퍼시픽 멤버십 회원 연동정보, 구매내역, 구매금액, 구매상품</dd>
									</c:if>
									<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
										<dt>개인정보 항목</dt>
										<dd>성명, 본인확인 값, 생년월일, 내/외국인 여부, 성별, 휴대전화번호, 구매 거래 내역, 뷰티포인트 내역, 이메일주소, 주소</dd>
									</c:if>
					       	 	</dl>
					       	 	<dl class="dt_w33">
					          		<dt>동의일</dt>
					          		<dd><c:out value="${dateParse}"/></dd>
					       	 	</dl>
			         		</div>
				          	<div class="btn_submit mt13">
				          		<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
									<button type="button" class="btnA btn_white" data-type='O' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />'>동의 철회하기</button>				          		
				          		</c:if>
				          		<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
				          			<button type="button" class="btnA btn_white" data-type='N' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />'>동의 철회하기</button>
				          		</c:if>
				          	</div>
				        </div>	         		
					</c:if>
				</c:forEach> 
			</c:if>
		</c:forEach>
		
		<c:forEach items="${naverUnLinkVo.afltChCicuemCuOptiQcVoList}" var="afltChCicuemCuOptiQcVo" varStatus="status">
			<c:if test="${afltChCicuemCuOptiQcVo.chCd eq '403' and afltChCicuemCuOptiQcVo.smsOptiYn eq 'Y'}">
	  			<div class="unlink_area">
					<fmt:parseDate var="dateFmt" pattern="yyyyMMdd" value="${afltChCicuemCuOptiQcVo.smsOptiDt}"/>
					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
					<div class="agree_title">
	         			<h3>[선택] 에뛰드 광고성 정보 SMS 수신 동의</h3>
	         		</div>
	         		<div class="user_info">
			       	 	<dl class="dt_w33">
			          		<dt>동의일</dt>
			          		<dd><c:out value="${dateParse}"/></dd>
			       	 	</dl>
	         		</div>
		          	<div class="btn_submit mt13">
		            	<button type="button" class="btnA btn_white" data-chcd='<c:out escapeXml="false" value="${afltChCicuemCuOptiQcVo.chCd }"/>' data-type="smsOptiYn">동의 철회하기</button>
		          	</div>	    
		        </div>				
			</c:if>
		</c:forEach>
		
		<c:forEach items="${naverUnLinkVo.naverMembershipTermsVoList}" var="naverMembershipTermsVo" varStatus="status">
			<c:if test="${naverMembershipTermsVo.chCd eq '404'}">
				<c:forEach items="${naverUnLinkVo.cicuedCuTncaTcVo}" var="cicuedCuTncaTcVo" varStatus="status">
					<c:if test="${naverMembershipTermsVo.prcnTcatCd eq cicuedCuTncaTcVo.tcatCd and cicuedCuTncaTcVo.tncAgrYn eq 'Y'}">
		   				<div class="unlink_area">
		   					<fmt:parseDate var="dateFmt" pattern="yyyy-MM-dd HH:mm:ss" value="${cicuedCuTncaTcVo.tncaDttm}"/>
		   					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
							<div class="agree_title">
								<ul>
									<li>
					         			<h3>[<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />]&nbsp;<c:out escapeXml="false" value="${naverMembershipTermsVo.tncTtl}" /></h3>
					         			<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
							         		<a href="javascript:;" class="btn_link_bp" data-type='O' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의 상세 보기 버튼" ap-click-data="(<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
							         		<span class="blind">자세히보기</span></a>
						         		</c:if>
					         			<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
							         		<a href="javascript:;" class="btn_link_bp" data-type='N' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의 상세 보기 버튼" ap-click-data="(<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
							         		<span class="blind">자세히보기</span></a>
						         		</c:if>
									</li>
								</ul>	         			
			         		</div>
			         		<div class="user_info">
								<dl class="dt_w33">
									<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
										<dt>개인정보 항목</dt>
										<dd>네이버 및 아모레퍼시픽 멤버십 회원 연동정보, 구매내역, 구매금액, 구매상품</dd>
									</c:if>
									<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
										<dt>개인정보 항목</dt>
										<dd>성명, 본인확인 값, 생년월일, 내/외국인 여부, 성별, 휴대전화번호, 구매 거래 내역, 뷰티포인트 내역, 이메일주소, 주소</dd>
									</c:if>
					       	 	</dl>
					       	 	<dl class="dt_w33">
					          		<dt>동의일</dt>
					          		<dd><c:out value="${dateParse}"/></dd>
					       	 	</dl>
			         		</div>
				          	<div class="btn_submit mt13">
				          		<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
									<button type="button" class="btnA btn_white" data-type='O' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />'>동의 철회하기</button>				          		
				          		</c:if>
				          		<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
				          			<button type="button" class="btnA btn_white" data-type='N' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />'>동의 철회하기</button>
				          		</c:if>
				          	</div>
				        </div>	         		
					</c:if>
				</c:forEach> 
			</c:if>
		</c:forEach>
		
		<c:forEach items="${naverUnLinkVo.afltChCicuemCuOptiQcVoList}" var="afltChCicuemCuOptiQcVo" varStatus="status">
			<c:if test="${afltChCicuemCuOptiQcVo.chCd eq '404' and afltChCicuemCuOptiQcVo.smsOptiYn eq 'Y'}">
	  			<div class="unlink_area">
					<fmt:parseDate var="dateFmt" pattern="yyyyMMdd" value="${afltChCicuemCuOptiQcVo.smsOptiDt}"/>
					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
					<div class="agree_title">
	         			<h3>[선택] 에스쁘아 광고성 정보 SMS 수신 동의</h3>
	         		</div>
	         		<div class="user_info">
			       	 	<dl class="dt_w33">
			          		<dt>동의일</dt>
			          		<dd><c:out value="${dateParse}"/></dd>
			       	 	</dl>
	         		</div>
		          	<div class="btn_submit mt13">
		            	<button type="button" class="btnA btn_white" data-chcd='<c:out escapeXml="false" value="${afltChCicuemCuOptiQcVo.chCd }"/>' data-type="smsOptiYn">동의 철회하기</button>
		          	</div>	    
		        </div>				
			</c:if>
		</c:forEach>
		
		<c:forEach items="${naverUnLinkVo.naverMembershipTermsVoList}" var="naverMembershipTermsVo" varStatus="status">
			<c:if test="${naverMembershipTermsVo.chCd eq '405'}">
				<c:forEach items="${naverUnLinkVo.cicuedCuTncaTcVo}" var="cicuedCuTncaTcVo" varStatus="status">
					<c:if test="${naverMembershipTermsVo.prcnTcatCd eq cicuedCuTncaTcVo.tcatCd and cicuedCuTncaTcVo.tncAgrYn eq 'Y'}">
		   				<div class="unlink_area">
		   					<fmt:parseDate var="dateFmt" pattern="yyyy-MM-dd HH:mm:ss" value="${cicuedCuTncaTcVo.tncaDttm}"/>
		   					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
							<div class="agree_title">
								<ul>
									<li>
					         			<h3>[<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />]&nbsp;<c:out escapeXml="false" value="${naverMembershipTermsVo.tncTtl}" /></h3>
					         			<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
							         		<a href="javascript:;" class="btn_link_bp" data-type='O' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의 상세 보기 버튼" ap-click-data="(<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
							         		<span class="blind">자세히보기</span></a>
						         		</c:if>
					         			<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
							         		<a href="javascript:;" class="btn_link_bp" data-type='N' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의 상세 보기 버튼" ap-click-data="(<c:out escapeXml="false" value="${naverMembershipTermsVo.tncAgrMandYnTxt}" />) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
							         		<span class="blind">자세히보기</span></a>
						         		</c:if>
									</li>
								</ul>	         			
			         		</div>
			         		<div class="user_info">
								<dl class="dt_w33">
									<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
										<dt>개인정보 항목</dt>
										<dd>네이버 및 아모레퍼시픽 멤버십 회원 연동정보, 구매내역, 구매금액, 구매상품</dd>
									</c:if>
									<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
										<dt>개인정보 항목</dt>
										<dd>성명, 본인확인 값, 생년월일, 내/외국인 여부, 성별, 휴대전화번호, 구매 거래 내역, 뷰티포인트 내역, 이메일주소, 주소</dd>
									</c:if>
					       	 	</dl>
					       	 	<dl class="dt_w33">
					          		<dt>동의일</dt>
					          		<dd><c:out value="${dateParse}"/></dd>
					       	 	</dl>
			         		</div>
				          	<div class="btn_submit mt13">
				          		<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'Y'}">
									<button type="button" class="btnA btn_white" data-type='O' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />'>동의 철회하기</button>				          		
				          		</c:if>
				          		<c:if test="${naverMembershipTermsVo.tncAgrMandYn eq 'N'}">
				          			<button type="button" class="btnA btn_white" data-type='N' data-chcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.chCd}" />' data-tcatcd='<c:out escapeXml="false" value="${naverMembershipTermsVo.prcnTcatCd}" />'>동의 철회하기</button>
				          		</c:if>
				          	</div>
				        </div>	         		
					</c:if>
				</c:forEach> 
			</c:if>
		</c:forEach>
		
		<c:forEach items="${naverUnLinkVo.afltChCicuemCuOptiQcVoList}" var="afltChCicuemCuOptiQcVo" varStatus="status">
			<c:if test="${afltChCicuemCuOptiQcVo.chCd eq '405' and afltChCicuemCuOptiQcVo.smsOptiYn eq 'Y'}">
	  			<div class="unlink_area">
					<fmt:parseDate var="dateFmt" pattern="yyyyMMdd" value="${afltChCicuemCuOptiQcVo.smsOptiDt}"/>
					<fmt:formatDate var="dateParse" pattern="yyyy.MM.dd" value="${dateFmt}"/>
					<div class="agree_title">
	         			<h3>[선택] 오설록 광고성 정보 SMS 수신 동의</h3>
	         		</div>
	         		<div class="user_info">
			       	 	<dl class="dt_w33">
			          		<dt>동의일</dt>
			          		<dd><c:out value="${dateParse}"/></dd>
			       	 	</dl>
	         		</div>
		          	<div class="btn_submit mt13">
		            	<button type="button" class="btnA btn_white" data-chcd='<c:out escapeXml="false" value="${afltChCicuemCuOptiQcVo.chCd }"/>' data-type="smsOptiYn">동의 철회하기</button>
		          	</div>	    
		        </div>				
			</c:if>
		</c:forEach>
	</c:if>
      
      <div class="unlink_area_last">
      	<div class="agree_title">
          <h3>회원탈퇴</h3>
        </div>
        <div class="withdrawal_btnarea">
            <p>회원 탈퇴 신청을 합니다.</p>
            <button type="button" id='req-withdraw'>탈퇴신청</button>
        </div>
   	  </div>
    </section>
  </div>
</body>
<common:backblock block="true"/>
</html>