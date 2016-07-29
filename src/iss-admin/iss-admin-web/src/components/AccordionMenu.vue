<template>
  <div class="ui vertical menu accordion">
    <template v-for="menu in menus">
      <div class="item" :class="[ current == $index ? 'active': '' ]">
        <a @click="expand($index)" class="title">
         <i class="icon accordion-menu-icon" :class="menu.icon"></i>
         <span>{{menu.name}}</span>
         <i class="icon" :class="[ current == $index ? 'caret down' : 'caret left']"></i>
        </a>
        <div class="content menu" :class="[ current == $index ? 'active': '' ]">
          <template v-for="c in menu.children">
            <a class="item">{{c.name}}</a>
          </template>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
export default {
  data: function () {
    return {
      current: -1,
      menus: [
        {
          name: '集群管理',
          icon: 'cubes',
          children: [
            {
              name: '节点状态'
            },
            {
              name: '服务查询'
            },
            {
              name: '调用统计'
            },
            {
              name: '警告查询'
            }
          ]
        },
        {
          name: '配置中心',
          icon: 'settings',
          children: [
            {
              name: '配置管理'
            }
          ]
        }
      ]
    }
  },
  methods: {
    expand: function (index) {
      if (this.current === index) {
        this.current = -1
      } else {
        this.current = index
      }
    }
  }
}
</script>

<style lang="scss">
@import "../common";
  $ITEM_HEIGHT: 15px;
  .ui.vertical.menu.accordion {
    border-right: none;
    border-radius: 0px;
    box-shadow: none;
    .item {
      .title {
        margin-left: 20px;
        height: $ITEM_HEIGHT;
        line-height: $ITEM_HEIGHT;
        color: $COLOR_BASE;
        .accordion-menu-icon {
          float: left !important;
          margin-right: 10px;
        }
        i {
          float: right;
        }
      }
      .content {
        margin-top: 12px;
        padding-top: 0;
        //background-color: $COLOR_GRAY_LIGHT;
        margin-bottom: -13px;
        .item {
          color: $COLOR_BASE;
          margin: 0;
          padding: 0;
          height: 35px;
          line-height: 35px;
          padding-left: 60px;
          border-top: 1px solid $COLOR_GRAY;
        }
      }
      a:hover {
        text-decoration: none;
      }
    }
  }
</style>