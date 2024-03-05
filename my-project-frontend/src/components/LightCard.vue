<script setup>

</script>

<template>
  <div class="light-card">
    <slot/>
  </div>
</template>

<style scoped>
.light-card{
  background-color: var(--el-bg-color);
  border-radius: 10px;
  padding: 10px;
  margin-bottom: 10px;
}
</style>