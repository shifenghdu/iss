<%@ page contentType="text/html;charset=utf-8" language="java" %>
<div class="ui bottom attached pushable">

    <div class="ui visible left vertical sidebar menu">
        <%--<a class="item"> 首页 </a>--%>
        <%--<a class="item"> 集群监控 </a>--%>
        <%--<a class="item"> 统一配置 </a>--%>
        <%--<a class="item"> 应用管理 </a>--%>
        <%--<a class="item"> 关于 </a>--%>
        <c:forEach var="item" items="${vo.menus}">
            <a class="item"> ${item.name} </a>
        </c:forEach>

    </div>
    <div class="pusher">
        <div class="ui basic segment">
            <%@include file="main.jsp"%>
        </div>
    </div>
</div>

<script>
    $(function () {
        // showing multiple
        $('.visible.example .ui.sidebar')
                .sidebar({
                    context: '.visible.example .bottom.segment'
                })
                .sidebar('hide')
        ;
    });
</script>
