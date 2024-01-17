 <%@ tag language="java" pageEncoding="UTF-8" body-content="empty" trimDirectiveWhitespaces="true"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <%@ attribute name="noscript" type="java.lang.Boolean"%>
 <% noscript = noscript == null ? false : noscript;  %>
 <c:choose>
   <c:when test="${noscript}">
  <!-- Google Tag Manager (noscript) -->
  <noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-5PT3MC6" height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
  <!-- End Google Tag Manager (noscript) -->      
   </c:when>
   <c:otherwise>
  <!-- Google Tag Manager -->
  <script>
    (function(w, d, s, l, i) {
      w[l] = w[l] || [];
      w[l].push({
        'gtm.start': new Date().getTime(),
        event: 'gtm.js'
      });
      var f = d.getElementsByTagName(s)[0]
        , j = d.createElement(s)
        , dl = l != 'dataLayer' ? '&l=' + l : '';
      j.async = true;
      j.src = 'https://www.googletagmanager.com/gtm.js?id=' + i + dl;
      f.parentNode.insertBefore(j, f);
    })(window, document, 'script', 'dataLayer', 'GTM-5PT3MC6');
  </script>
  <!-- End Google Tag Manager -->
   </c:otherwise>
 </c:choose>