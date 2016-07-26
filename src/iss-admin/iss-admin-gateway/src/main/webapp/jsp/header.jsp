<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="baseUrl" value="${pageContext.request.contextPath}" />
<c:set var="staticUrl" value="/statics" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <!-- Standard Meta -->
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">

    <!-- Site Properities -->
    <title>iss-admin</title>
    <link rel="stylesheet" type="text/css" href="${staticUrl}/semantic.min.css">
    <link rel="stylesheet" type="text/css" href="${staticUrl}/admin.css">
    <script src="${staticUrl}/jquery.min.js"></script>
    <script src="${staticUrl}/semantic.min.js"></script>
    <script src="${staticUrl}/convert-hex.js"></script>
    <script src="${staticUrl}/convert-string.js"></script>
    <script src="${staticUrl}/sha256.js"></script>
</head>
<body>
