<%@ page contentType="text/html;charset=utf-8" language="java" %>
<div class="ui bottom attached pushable">

    <div class="ui visible left vertical sidebar menu">
        <c:forEach var="item" items="${vo.menus}">
            <a class="item" href="${item.url}"> ${item.name} </a>
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
