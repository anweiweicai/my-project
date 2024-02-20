<script setup>

</script>

<template>
  <div style="width: 100vw; height: 100vh; overflow: hidden; display: flex">
    <div style="flex: 1; background-color: black">
<!-- `flex: 1`用于在flex容器中平均分配剩余空间给flex项-->
      <el-image style="width: 100%; height: 100%" fit="cover"
          src="/welcome.png"></el-image>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px; font-weight: bold;">欢迎来到本网站</div>
      <div style="margin-top: 10px">这是一个基于Spring和Vue的前后端分离的示例网站</div>
      <div style="margin-top: 5px">根据你的需求,完善这个网站吧!</div>
    </div>
    <div class="right-card">
      <router-view v-slot="{Component}">
        <transition name="el-fade-in-linear"  mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>

  </div>

</template>

<style scoped>

.welcome-title{
  text-align: right; /* 文字右对齐 */
  position: absolute;
  top: 30px;
  right: 430px;
  color: steelblue;
  text-shadow: 0 0 5px;
}

.right-card{
  width: 400px;
  z-index: 1;
  background-color: white;
}

</style>