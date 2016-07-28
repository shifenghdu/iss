<template>
  <div class="ui vertical menu accordion">
    <template v-for="menu in menus">
      <div class="item">
        <a @click="expand($index)" class="title"> <b>{{menu.name}}</b> <i class="dropdown icon"></i>
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
          name: 'update',
          children: [
            {
              name: 'update-c1'
            },
            {
              name: 'update-c2'
            }
          ]
        },
        {
          name: 'insert',
          children: [
            {
              name: 'insert-c1'
            },
            {
              name: 'insert-c2'
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
    .item {
      a:hover {
        text-decoration: none;
      }
      b {
        font-size: 10px;
      }
    }
</style>