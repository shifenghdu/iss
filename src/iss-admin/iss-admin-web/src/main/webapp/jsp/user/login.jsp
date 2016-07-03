<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../header.jsp" %>
<div class="login-header">
    <h1>
        <i class="settings icon"></i>
        ISS管理-用户登录
    </h1>
</div>
<div class="login-panel ui three column stackable grid">
    <div class="column"></div>
    <div class="column">
        <form id="login" class="ui fluid form segment">
            <div class="field">
                <label class="">用户名</label>
                <div class="ui left icon input">
                    <input type="text" name="name" placeholder="">
                    <i class="user icon"></i>
                </div>
            </div>
            <div class="field">
                <label class="">密码</label>
                <div class="ui left icon input">
                    <input type="password" name="password" placeholder="">
                    <i class="lock icon"></i>
                </div>
            </div>
            <div class="inline field">
                <div class="ui checkbox">
                    <input type="checkbox" name="terms">
                    <label>记住密码</label>
                </div>
            </div>
            <div class="inline field">
                <button type="submit" class="fluid ui blue button">登录</button>
            </div>
        </form>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function(){
//            $('.ui.form').form({
//                userName: {
//                    identifier : 'userName',
//                    rules: [
//                        {
//                            type   : 'empty',
//                            prompt : 'Please enter a username'
//                        }
//                    ]
//                },
//                password: {
//                    identifier : 'password',
//                    rules: [
//                        {
//                            type   : 'empty',
//                            prompt : 'Please enter a password'
//                        },
//                        {
//                            type   : 'length[6]',
//                            prompt : 'Your password must be at least 6 characters'
//                        }
//                    ]
//                }
//            });

        $('.ui.form').submit(function(e){
            var formData = $('.ui.form input').serializeArray(); //or .serialize();
            $.post('login',formData,function (response) {
                console.log(response);
            });
        });
        //checkbox init
        $('.ui.checkbox').checkbox();
    });
</script>

<%@ include file="../footer.jsp" %>
