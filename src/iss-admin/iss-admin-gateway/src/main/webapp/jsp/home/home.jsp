<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../header.jsp" %>
<div class="home-header">
    <div>
        <h1>
            <i class="settings icon"></i>
            ISS管理-用户登录
        </h1>
        <div class="home-header-welcome">
            <span>欢迎, </span><a href="javascript: void(0);">${loginUserName}</a><span> | </span><a href="${baseUrl}/auth/logout">登出</a>
        </div>
    </div>
</div>
<div class="home-view">
    <%@include file="sidebar.jsp"%>
</div>
<%@ include file="../footer.jsp" %>


