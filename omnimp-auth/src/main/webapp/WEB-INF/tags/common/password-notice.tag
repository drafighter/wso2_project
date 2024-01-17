<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="gaArea" type="java.lang.String"%>
          <div class="info_notice verR">
            <button type="button" class="btn_open_info" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 비밀번호 입력시 유의사항 버튼" ap-click-data="비밀번호 입력시 유의사항"><span>비밀번호 입력 시 유의사항</span></button>
            <div class="layer_info">
              <strong>비밀번호 입력 시 유의사항</strong>
              <ul class="notice_list">
                <li>영문 소문자, 숫자, 특수문자 중 최소 2가지 조합으로 8~16자 까지 생성 가능합니다.</li>
                <li>공백은 사용할 수 없으며, 특수문자는 다음과 같이 사용 가능합니다.<br /><%= "!”#$%&’()*+,-./:;<=>?@[＼]^_`{|}~" %></li>
                <li>비밀번호는 아이디와 동일하게 사용할 수 없습니다.</li>
              </ul>
              <button type="button" class="close_layer_info"><span class="blind">닫기</span></button>
            </div>
          </div>