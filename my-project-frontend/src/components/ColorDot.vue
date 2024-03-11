<script setup>
defineProps({
  color: String
})
</script>

<template>
  <div class="dot" :style="{background: color}">

  </div>
</template>
<!--inline-block是行内块元素，相当于display:inline-->
<style scoped>
.dot{
  width: 10px;
  height: 10px;
  border-radius: 5px;
  display: inline-block;
}
</style>