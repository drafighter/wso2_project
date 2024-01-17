<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
       <c:if test="${not empty terms}">
       <strong id='termpannel' class="txt_t terms"><c:out value="${channelName}" /> 회원 약관</strong>
       <ul class='terms'>
 		<c:forEach items="${terms}" var="term" varStatus="status">
         <li>
           <span class="checkboxA">
             <input type="checkbox" id="i_agree_<c:out value="${term.chCd}" /><c:out value="${status.count}" />" <c:if test="${term.tncAgrMandYn eq 'Y'}">class="required"</c:if> name="terms_check" data-cd="<c:out escapeXml="false" value="${term.tcatCd}" />" data-no="<c:out escapeXml="false" value="${term.tncvNo}" />"  title="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />'"/>
             <label for="i_agree_<c:out value="${term.chCd}" /><c:out value="${status.count}" />" ap-click-area="약관동의" ap-click-name="약관동의 - (<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 체크 박스" ap-click-data="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />'"><span class="checkbox_label">[<c:out value="${term.tncAgrMandYnTxt}" />] <c:out value="${term.tncTtl}" /></span></label>
           </span>
           <a href="javascript:;" ap-click-area="약관동의" ap-click-name="약관동의 - (<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 상세보기 버튼" ap-click-data="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 상세보기" class="btn_link" data-lnk="<c:out escapeXml="false" value="${term.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${term.chCd}" />" data-lnkcd="<c:out escapeXml="false" value="${term.tcatCd}" />" data-lnkno="<c:out escapeXml="false" value="${term.tncvNo}" />"><span class="blind">자세히보기</span></a>
         </li>
         <input type='hidden' name="tcatCds" value="<c:out escapeXml="false" value="${term.tcatCd}" />"/>
         <input type='hidden' name="tncvNos" value="<c:out escapeXml="false" value="${term.tncvNo}" />"/>
 		</c:forEach>
       </ul>
       </c:if>   
