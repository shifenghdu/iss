<template>
  <div id="app">
    <template v-if="isLogin">
      <head>
        <div class="home-header">
          <div>
              <h1>
                  <i class="settings icon"></i>
                  ISS管理系统  <span>德宝科技有限公司</span>
              </h1>
              <div class="home-header-welcome">
                  <span>欢迎, </span><a href="javascript: void(0);">{{loginUser.name}}</a><span> | </span><a href="${baseUrl}/auth/logout">登出</a>
              </div>
          </div>
      </div>
      </head>
      <p>status: login</p>
    </template>
    <template v-else>
      <!-- 登录界面 -->
      <div class="login-panel ui three column stackable grid">
        <div class="column"></div>
        <div class="column">
          <form id="login" class="ui fluid form segment">
            <div class="field">
              <label class="">用户名</label>
              <div class="ui left icon input">
                <input type="text" name="name" placeholder="" :bind="userName"> <i class="user icon"></i>
              </div>
            </div>
            <div class="field">
              <label class="">密码</label>
              <div class="ui left icon input">
                <input type="password" name="password" placeholder="" :bind="password"> <i class="lock icon"></i>
              </div>
            </div>
            <div class="inline field">
              <div class="ui checkbox">
                <input type="checkbox" name="terms" :bind="savePass">
                <label>记住密码</label>
              </div>
            </div>
            <div class="inline field">
              <a class="fluid ui blue button" @click="doLogin">登录</a>
            </div>
          </form>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import Hello from './components/Hello'

export default {
  data: function () {
    return {
      isLogin: false,
      userName: 'admin',
      password: '123',
      loginUser: {
        name: 'admin'
      }
    }
  },
  components: {
    Hello
  },
  methods: {
    doLogin: function (event) {
      console.log(this.userName)
      this.isLogin = true
    }
  }
}
</script>

<style lang="scss">
@import "common";

//登录标题
.login-header {
  color: $COLOR_BLANK;
  background-color: $COLOR_BASE;
  h1 {
    height: 60px;
    line-height: 60px;
    margin-left: 5px;
  }
}

.login-panel {
  margin-top: 30px !important;
}

.home-header {
  color: $COLOR_BLANK;
  background-color: $COLOR_BASE;
  h1 {
    height: 30px;
    line-height: 30px;
    margin-left: 5px;
    font-size: 17px;
    span{
      margin: 20px;
      font-size: 10px;
    }
  }
  .home-header-welcome {
    position: absolute;
    top: 5px;
    right: 10px;
    font-size: 10px;
    a {
      line-height: 11px;
      color: $COLOR_BLANK;
    }
    a:hover {
      text-decoration: underline;
    }
  }
}

</style>