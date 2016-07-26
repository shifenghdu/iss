<%@ page contentType="text/html;charset=utf-8" language="java" %>
<div class="ui bottom attached pushable">

    <div class="ui visible left vertical sidebar menu">
        <c:forEach var="item" items="${vo.menus}">
            <c:choose>
                <c:when test="${item.id == 1}">
                    <a class="item" href="${item.url}"> ${item.name} </a>
                </c:when>
                <c:otherwise>
                    <a class="item" href="${item.url}?mid=${item.id}"> ${item.name} </a>
                </c:otherwise>
            </c:choose>
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
