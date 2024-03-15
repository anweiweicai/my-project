<script setup>
import {Delta, QuillEditor} from "@vueup/vue-quill";
import {ref} from "vue";
import {post} from "@/net/index.js";
import {ElMessage} from "element-plus";

const props = defineProps({
  show: Boolean,
  tid: String,
  quote: Object
})

const content = ref()

const emit = defineEmits(['close', 'comment'])

const init = () => {
  content.value = new Delta()
}

function submitComment() {
  if (deltaToText(content.value).length > 2000){
    ElMessage.warning('评论内容过长, 请缩减内容! (2000字以内)')
  }
  if (deltaToText(content.value).length < 5){
    ElMessage.warning('评论内容过短, 请增加内容! (5字以上)')
  }
  else {
    post('api/forum/add-comment', {
      tid: props.tid,
      quote: props.quote ? props.quote.id : -1,
      content: JSON.stringify(content.value)
    }, () => {
      ElMessage.success('发表评论成功')
      emit('comment')
    })
  }

}

/**
 * 将 delta 转换为简单的文本
 * @param delta
 * @returns {string}
 */
function deltaToSimpleText(delta){
  let str = deltaToText(JSON.parse(delta))
  if(str.length > 35) str = str.substring(0, 35) + '...'
  return str
}

function deltaToText(delta){
  if (!delta?.ops) return ''
  let str = ''
  for (let op of delta.ops){
    str += op.insert
  }
  return str.replace(/\s/g,"")
}


</script>

<template>
  <div>
    <el-drawer :model-value="show" @close="emit('close')" @open="init"
               direction="btt" :size="270" :close-on-click-modal="false"
               :title="quote ? `回复: ${deltaToSimpleText(quote.content)}` : '发表评论'">
      <div>
        <div>
          <quill-editor style="height: 120px" v-model:content="content" placeholder="请发表友善的评论..."/>
        </div>
        <div style="margin-top: 10px; display: flex">
          <div style="flex: 1; font-size: 13px; color: grey">
            字数统计: {{deltaToText(content).length}} (最大支持2000字)
          </div>
          <el-button type="success" plain @click="submitComment">发表评论</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped lang="less">
:deep(.el-drawer) {
  width: 800px;
  margin: 50px auto;
  border-radius: 15px;
}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.el-drawer__body) {
  padding: 10px;
}
</style>