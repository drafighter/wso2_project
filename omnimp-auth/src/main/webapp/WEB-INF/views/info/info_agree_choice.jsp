<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<link rel="stylesheet" type="text/css" href="<c:out value='${ctx}'/>/css/common.css">

          <div class="agree-txt" tabindex="0">
            <h3>개인정보 수집 및 이용 동의(마케팅)</h3>
            <p>회사는 이용자의 회원서비스 제공을 위하여 아래와 같이 개인정보를 수집 및 이용합니다.</p>
            <table>
              <colgroup>
                <col width="30%">
                <col width="*">
                <col width="30%">
              </colgroup>
              <thead>
                <tr>
                  <th scope="col">수집항목</th>
                  <th scope="col">수집ㆍ이용 목적</th>
                  <th scope="col">보유ㆍ이용 기간</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>휴대전화번호, 이메일주소, 주소</td>
                  <td class="agree_txt_strong">본인 동의 시 회사 또는 제휴사의 서비스/사업 및 정책/기타 이벤트에 관한 정보 제공 및 그에 따른 경품 등 물품 배송</td>
                  <td class="agree_txt_strong">동의 철회 또는 회원 탈퇴 시까지<br>(처리방침 제5조 참조)</td>
                </tr>
                <tr>
                  <td>서비스 이용기록, 접속로그, 쿠키, 접속 IP정보, 구매 거래 내역</td>
                  <td>이용자의 관심, 기호, 성향의 추정을 통한 맞춤형 컨텐츠 및 서비스 제공</td>
                  <td class="agree_txt_strong">동의 철회 또는 회원 탈퇴 시까지<br>(처리방침 제5조 참조)</td>
                </tr>
              </tbody>
            </table>
            <p>※ 고객님께서는 개인정보 수집 및 이용(선택) 동의에 거부할 수 있습니다.<br> 
            다만, 거부하는 경우 이벤트에 관한 정보 제공 및 고객에게 맞춘 서비스를 받으실 수 없습니다.</p>
          </div>
