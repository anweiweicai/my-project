<script setup>
import {Check, Document} from "@element-plus/icons-vue"
import {computed, reactive, ref} from "vue";
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue"; // 调整图片大小
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module"; // 上传图片
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import axios from "axios";
import {accessHeader, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import ColorDot from "@/components/ColorDot.vue";
import {useStore} from "@/store/index.js";

const props = defineProps({
  show: Boolean,
  defaultTitle: {
    default: '',
    type: String
  },
  defaultContent: {
    default: '',
    type: String
  },
  defaultType: {
    default: 1,
    type: Number
  },
  submitButton: {
    default: '立即发布',
    type: String
  },
  submit: {
    default: (editor, success) => {
      post('/api/forum/create-topic', {
        type: editor.type,
        title: editor.title,
        content: editor.content
      }, () => {
        ElMessage.success('帖子发布成功!')
        success()
      })
    },
    type: Function
  }
})
// emit是用来向父组件传递事件的函数
const emit = defineEmits(['close', 'success'])
const store = useStore()
const refEditor = ref()
const editor = reactive({
  type: 1,
  title: '',
  content: '',
  loading: false
})

/**
 *  初始化富文本
 */
function initEditor() {
  if (props.defaultContent) {
    editor.content = new Delta(JSON.parse(props.defaultContent))
  }else {
    refEditor.value.setContents('', 'user')
  }
  editor.title = props.defaultTitle
  editor.type = props.defaultType
}

/**
 * 将富文本delta转换为纯文本
 * @param delta
 * @returns {string}
 */
function deltaToText(delta){
  if (!delta.ops) return  ''
  let str = ''
  for (let op of delta.ops) {
    str += op.insert
  }
  return str.replace(/\s/g, '')
}

/**
 * 计算富文本的字数
 * @type {ComputedRef<number>}
 */
const contentLength = computed(() => {
  return deltaToText(editor.content).length
})
function submitTopic() {
  const text = deltaToText(editor.content)
  if(text.length > 20000){
    ElMessage.warning('字数超出限制, 无法发布!')
    return
  }
  if (!editor.title) {
    ElMessage.warning('帖子标题不能为空!')
    return
  }
  if (!editor.type) {
    ElMessage.warning('请选择主题类型!')
    return
  }
  if (!editor.content) {
    ElMessage.warning('帖子内容不能为空!')
    return
  }
  props.submit(editor,() => emit('success'))
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
      maxSize: 3 * 1024 * 1024,
      size: 5,
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
               @close="emit('close')" @open="initEditor">
<!--     :close-on-click-modal="false" 为了防止点击遮罩层关闭 -->
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的帖子</div>
          <div style="font-size: 13px; color: grey">发表内容前, 请遵守相关法律法规, 请勿攻击他人</div>
        </div>
      </template>
      <div style="display: flex; gap: 10px">
        <div style="width: 150px">
          <el-select placeholder="选择主题类型..." v-model="editor.type" :disabled="!store.forum.types.length">
            <el-option v-for="item in store.forum.types.filter(type => type.id > 0)" :value="item.id" :label="item.name">
              <div>
                <color-dot :color="item.color"/>
                <span :style="{color: item.color}" style="margin-left: 10px ">{{ item.name }}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input v-model="editor.title"  :placeholder="`添加帖子标题: ${store.forum.types[editor.type].desc}`" :prefix-icon="Document"
          style="height: 100%" maxlength="30"/>
        </div>
      </div>
      <div style="margin-top: 15px; height: 460px; overflow: hidden; border-radius: 5px" v-loading="editor.uploading" element-loading-text="正在上传图片, 请稍后...">
        <quill-editor v-model:content="editor.content" style="height: calc(100% - 45px)" placeholder="今天分享点什么呢..."
                      content-type="delta" ref="refEditor"
                      :options="editorOption"/><!-- content-type="delta" 是为了兼容富文本以json格式保存-->
      </div>
      <div style="display: flex; justify-content: space-between; margin-top: 5px;">
        <div style=" color: grey; font-size: 13px">
          当前字数:{{ contentLength }}(最大支持字数:20000)
        </div>
        <div>
          <el-button type="success" :icon="Check" plain @click="submitTopic" >{{ submitButton }}</el-button>
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


</style>