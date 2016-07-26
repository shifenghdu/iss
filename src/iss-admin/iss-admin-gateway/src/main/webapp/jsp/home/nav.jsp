<%@ page contentType="text/html;charset=utf-8" language="java" %>
<div class="ui breadcrumb">
    <a class="section" href="/home">首页</a>
    <c:forEach items="${vo.menuLine}" var="item">
        <i class="right angle icon divider"></i>
        <a class="section">${item.name}</a>
    </c:forEach>
</div>