<template>
  <div id="app">
    <template v-if="isLogin">
      <head>
        <div class="home-header">
          <div>
              <h1>
                  <i class="settings icon"></i>
                  ISS管理系统  <span></span>
              </h1>
              <div class="home-header-welcome">
                  <span>欢迎, </span><a href="javascript: void(0);">{{loginUser.name}}</a><span> | </span><a @click="doLogout">登出</a>
              </div>
          </div>
        </div>
      </head>
      <div class="home-workspace">
        <div class="ui breadcrumb">
          <a class="section">首页</a>
          <i class="right angle icon divider"></i>
          <a class="section">集群管理</a>
          <i class="right angle icon divider"></i>
          <div class="active section">节点状态</div>
        </div>
        <div class="home-main-view">
          <div class="home-side-bar">
            <accordion-menu></accordion-menu>
          </div>
          <p>status: login</p>
      </div>
      </div>
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
                <input type="text" name="name" placeholder="" :value="userName"> <i class="user icon"></i>
              </div>
            </div>
            <div class="field">
              <label class="">密码</label>
              <div class="ui left icon input">
                <input type="password" name="password" placeholder="" :value="password"> <i class="lock icon"></i>
              </div>
            </div>
            <div class="inline field">
              <div class="ui checkbox">
                <input type="checkbox" name="terms" v-model="savePass">
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
import AccordionMenu from './components/AccordionMenu'

export default {
  data: function () {
    return {
      isLogin: false,
      userName: 'admin',
      password: '123',
      savePass: true,
      loginUser: {
        name: 'admin'
      }
    }
  },
  components: {
    AccordionMenu
  },
  methods: {
    doLogin: function (event) {
      this.isLogin = true
    },
    doLogout: function (event) {
      this.isLogin = false
    }
  }
}
</script>

<style lang="scss">
@import "common";

.login-panel {
  margin-top: 100px !important;
}

.home-header {
  width: 100%;
  height: 40px;
  color: $COLOR_BLANK;
  background-color: $COLOR_BASE;
  h1 {
    float: left;
    height: 40px;
    line-height: 40px;
    margin-left: 20px;
    font-size: 17px;
    span{
      margin: 20px;
      font-size: 10px;
    }
  }
  .home-header-welcome {
    float: right;
    font-size: 13px;
    height: 40px;
    line-height: 40px;
    margin-right: 20px;
    a {
      // line-height: 40px;
      color: $COLOR_BLANK;
    }
    a:hover {
      text-decoration: underline;
    }
  }
}
.home-workspace {
  position: absolute;
  bottom: 0px;
  top: 40px;
  left: 0px;
  right: 0px;
  background-color: $COLOR_GRAY_LIGHT;
  .home-main-view {
    // margin-top: 5px;
    background-color: $COLOR_BLANK;
    height: 100%;
    .home-side-bar {
      float: left;
      height: 100%;
      border-right: 1px solid $COLOR_GRAY; 
    }
  }
  .breadcrumb {
    font-size: 13px;
    margin-left: 20px;
    height: 20px;
    width: 100%;
    line-height: 20px;
  }
}

</style>