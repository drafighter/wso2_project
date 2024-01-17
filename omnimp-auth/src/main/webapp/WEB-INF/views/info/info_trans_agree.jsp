<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<link rel="stylesheet" type="text/css" href="<c:out value='${ctx}'/>/css/common.css">

          <div class="agree-txt" tabindex="0">
            <h3>국외이전동의</h3>
            <table>
              <caption>국외이전동의 : 이전 받는 자, 이전되는 국가, 이전항목, 이전방법, 이전목적, 이전 일시 및 보유기간</caption>
              <colgroup>
                <col style="width:30%">
                <col style="width:*">
              </colgroup>
              <tbody>
                <tr>
                  <th scope="row" class="agree_txt_strong">이전 받는 자</th>
                  <!-- <td style="word-break:break-all; text-align:left" class="agree_txt_strong">Facebook<br>1 Facebook Way, Menlo Park, CA 94025 미국<br>Mail : Facebook Ireland Ltd.<br>4 Grand Canal Square<br>Grand Canal Harbour Dublin 2 Ireland<br>On Line : https://www.facebook.com/help/contact/1461223320847982?sw_fnr_id=322475847&amp;fnr_t=0<br>Attention: Privacy Officer</td> -->
                  <td style="word-break:break-all; text-align:left" class="agree_txt_strong">Meta Platform, Inc.<br>1601 Willow Road, Menlo Park, CA 94025<br>korealocalagent@support.facebook.com</td>
                </tr>
                <tr>
                  <th scope="row">이전 되는&nbsp;국가</th>
                  <td>미국</td>
                </tr>
                <tr>
                  <th scope="row">이전항목</th>
                  <td>이메일, 휴대폰 번호</td>
                </tr>
                <tr>
                  <th scope="row">이전방법</th>
                  <td>Facebook 마케팅툴을 통해 이메일, 휴대폰 번호를 해쉬처리 후 이전 처리 예정</td>
                </tr>
                <tr>
                  <th scope="row" class="agree_txt_strong">이전목적</th>
                  <td class="agree_txt_strong">페이스북 제휴 오디언스 마케팅</td>
                </tr>
                <tr>
                  <th scope="row" class="agree_txt_emp">이전 시점</th>
                  <td class="agree_txt_strong">이전 시점 : 페이스북 캠페인 대상 선정시<br>보유기간 : 14일</td>
                </tr>
              </tbody>
            </table>
            <p>이용자는 국외이전에 대한 동의를 거부할 수 있으며, 동의를 거부하는 경우, 페이스북을 통한 유익한 정보를 받아볼 수 없습니다.</p>
          </div>
               