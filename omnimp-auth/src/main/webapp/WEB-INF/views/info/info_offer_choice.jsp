<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

          <div class="agree-txt" tabindex="0">
            <h3>개인정보 제3자 제공 동의(선택)</h3>
            <p>회사는 이용자의 보다 다양한 서비스 제공을 위하여 아래와 같이 별도 동의를 받아 개인정보를 제공합니다.</p>
            <table>
              <caption>개인정보 제공동의 : 개인정보를 제공받는자, 제공목적, 제공하는 항목, 보유 및 이용기간</caption>
              <colgroup>
                <col width="20%">
                <col width="*">
                <col width="20%">
                <col width="20%">
              </colgroup>
              <thead>
                <tr>
                  <th scope="col">제공받는 자</th>
                  <th scope="col">제공 목적</th>
                  <th scope="col">제공 항목</th>
                  <th scope="col">보유 및 이용기간</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td class="agree_txt_strong">${chNm}</td>
                  <td class="agree_txt_strong">뷰티포인트 고객에 대한 제공받는 자의 고유 CRM 활동, 재화나 서비스 홍보ㆍ안내ㆍ마케팅 제공</td>
                  <td>성명, 본인확인 값, 생년월일, 내/외국인 여부, 성별, 휴대전화번호, 구매 거래 내역, 뷰티포인트 내역, 이메일주소, 주소</td>
                  <td class="agree_txt_strong">동의 철회 또는 회원 탈퇴 시까지(처리방침 제5조 참조)</td>
                </tr>
              </tbody>
            </table>
            <p>※고객님께서는 개인정보 제3자 제공 동의(선택)에 거부할 수 있습니다.</p>
            <p>다만, 거부하는 경우 ${chNm}에서의 이벤트나 서비스 정보 등을 받을 수 없습니다.</p>
          </div>
