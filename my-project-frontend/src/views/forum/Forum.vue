<script setup>
import {useStore} from "@/store/index.js";
import {get} from "@/net/index.js";

const store = useStore()

/**
 * 获取论坛类型
 */
get('/api/forum/types', data => {
  const array = []
  array.push({name: '全部', id: 0, color: 'linear-gradient(45deg, white, red, orange, gold, green, blue)'})
  data.forEach(d => array.push(d))
  store.forum.types = array
})

</script>

<template>
  <div>
    <router-view v-slot="{Component}">
      <transition name="el-fade-in-linear" mode="out-in">
        <!--     keep-alive 保存组件, 避免重复加载 -->
        <keep-alive include="TopicList">
          <component :is="Component"/>
        </keep-alive>
      </transition>
    </router-view>
    <el-tooltip content="回到顶部" effect="light" placement="left">
      <el-backtop target=".main-content-page .el-scrollbar__wrap" :right="20" :bottom="80"></el-backtop>
    </el-tooltip>
  </div>
</template>

<style scoped>

</style>