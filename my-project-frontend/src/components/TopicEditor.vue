<script setup>
import {Check, Document} from "@element-plus/icons-vue"
import {reactive} from "vue";
import {Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue"; // 调整图片大小
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module"; // 上传图片
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import axios from "axios";
import {accessHeader} from "@/net/index.js";
import {ElMessage} from "element-plus";

defineProps({
  show: Boolean
})
// emit是用来向父组件传递事件的函数
const emit = defineEmits(['close'])

const editor = reactive({
  type: 1,
  title: '',
  content: '',
  loading: false
})
const types = [
  {id: 1, name: "日常闲聊", desc:'在这里分享自己的日常生活...'},
  {id: 2, name: "真诚交友", desc:'在这里寻找志同道合的朋友...'},
  {id: 3, name: "问题反馈", desc:'在这里提出你的问题或者建议...'},
  {id: 4, name: "恋爱官宣", desc:'在这里宣传你的恋爱关系...'},
  {id: 5, name: "踩坑记录", desc:'在这里记录你的踩坑经历, 防止其他人踩坑...'}
]

function submitTopic() {
  console.info(editor.content)
}

Quill.register('modules/imageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)

const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike","clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        { header: [1, 2, 3, 4, 5, 6, false] },
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        { indent: '-1' }, { indent: '+1' }
      ],
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      }
    },
    imageResize: {
      modules: [ 'Resize', 'DisplaySize' ]
    },
    ImageExtend: {
      action:  axios.defaults.baseURL + '/api/image/cache',
      name: 'file',
      size: 5,
      loading: true,
      accept: 'image/png, image/jpeg',
      response: (resp) => {
        if(resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization);
      },
      start: () => editor.uploading = true,
      success: () => {
        ElMessage.success('图片上传成功!')
        editor.uploading = false
      },
      error: () => {
        ElMessage.warning('图片上传失败，请联系管理员!')
        editor.uploading = false
      }
    }
  }
}
</script>

<template>
  <div>
    <el-drawer :model-value="show"
               direction="btt" :size="650" :close-on-click-modal="false"
               @close="emit('close')">
<!--     :close-on-click-modal="false" 为了防止点击遮罩层关闭 -->
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的帖子</div>
          <div style="font-size: 13px; color: grey">发表内容前, 请遵守相关法律法规, 请勿攻击他人</div>
        </div>
      </template>
      <div style="display: flex; gap: 10px">
        <div style="width: 150px">
          <el-select placeholder="选择主题类型..." v-model="editor.type">
            <el-option v-for="item in types" :label="item.name" :value="item.id"/>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input v-model="editor.title"  :placeholder="`${types[editor.type-1].desc}`" :prefix-icon="Document"
          style="height: 100%"/>
        </div>
      </div>
      <div style="margin-top: 15px; height: 460px; overflow: hidden; border-radius: 5px" v-loading="editor.loading" element-loading-text="正在上传图片, 请稍后...">
        <quill-editor v-model:content="editor.content" style="height: calc(100% - 45px)" placeholder="今天想分享点什么呢?"
                      content-type="delta"
                      :options="editorOption"/><!-- content-type="delta" 是为了兼容富文本以json格式保存-->
      </div>
      <div style="display: flex; justify-content: space-between; margin-top: 5px;">
        <div style=" color: grey; font-size: 13px">
          当前字数:666(最大支持字数:20000)
        </div>
        <div>
          <el-button type="success" :icon="Check" plain @click="submitTopic">立即发布</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<!--:deep是一个CSS选择器，它可以用来选择嵌套在Shadow DOM中的元素。在一些情况下，普通的CSS选择器无法穿透Shadow DOM，而:deep可以用来解决这个问题。不过需要注意的是，:deep选择器已经被废弃，推荐使用::slotted选择器来代替。-->
<style scoped>
:deep(.el-drawer) {
  width: 800px;
  margin: auto;
  border-radius: 15px 15px 0 0;
}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.ql-toolbar){
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}

:deep(.ql-container){
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}

:deep(.ql-editor.ql-blank::before){
  color: var(--el-text-color-placeholder);
  font-style: normal;
}

:deep(.ql-editor){
  font-size: 14px;
}
</style>