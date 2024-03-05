<script setup>
import 'qweather-icons/font/qweather-icons.css' // 引入和风天气图标
</script>

<template>
  <div>
    <div style="display: flex; justify-content: space-between; margin: 10px 20px">
      <div style="font-size: 45px">
        <i class="qi-100-fill"></i>
      </div>
      <div style="font-weight: bold; text-align: center">
        <div style="font-size: 25px">20℃</div>
        <div style="font-size: 15px;">多云</div>
      </div>
      <div style="margin: 12px">
        <div style="font-size: 15px">中国 四川省</div>
        <div style="font-size: 14px; color: grey">成都市 双流区</div>
      </div>
    </div>
    <el-divider style="margin: 10px 0"/>
    <!--grid-template-columns: repeat(5, 1fr);：该属性定义了网格布局的列模板，其中repeat(5, 1fr)指定了创建5个等宽的列，每个列的宽度为网格容器的可用空间的1/5。-->
    <div style="display: grid; grid-template-columns: repeat(5, 1fr); text-align: center">
      <div v-for="item in 5">
        <div style="font-size: 13px">{{item}}时</div>
        <div style="font-size: 23px">
          <i class="qi-100-fill"></i>
        </div>
        <div style="font-size: 12px">20℃</div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>