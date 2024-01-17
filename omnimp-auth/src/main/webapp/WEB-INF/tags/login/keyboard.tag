<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
        <div class="view_keyboard">
          <button class="btn_keyboard" ap-click-area="로그인" ap-click-name="로그인 - PC 키보드 보기 버튼" ap-click-data="PC 키보드 보기">PC 키보드 열기</button>
          <span class="img_keyboard">
            <img src="<c:out value='${ctx}'/>/images/common/img_keyboard.png" alt="키보드 배열 이미지" />
          </span>
        </div>