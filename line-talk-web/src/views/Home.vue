<template>
  <div class="home vh-100 d-flex justify-content-center align-items-center">
    <div class="border rounded bg-dark p-2 text-white shadow-lg" style="width: 40rem">
      <div class="py-2 font-weight-bold d-flex justify-content-between">
        <div class="">聊天室 v1.00</div>
        <div class="small">
          <div @click="refsh" class="btn btn-primary text-white btn-sm" href="#">
            刷新
          </div>
          状态: <span class="text-success">{{ state }}</span>
        </div>
      </div>
      <div
        class="websocket-body py-3 bg-white p-2 rounded text-dark"
        style="height: 20rem"
        ref="websocketBody"
      >
        <div
          class="card p-3 mb-2"
          :class="item.name == name ? 'text-right' : ' text-left'"
          v-for="(item, index) in msgArr"
          :key="index"
        >
          <div class="text-success">
            {{ item.name }}
          </div>
          <div class="small">{{ item.msg }}</div>
          <div class="small">{{ timestampFormat(item.time) }}</div>
        </div>
      </div>
      <div class="row no-gutters">
        <div class="col-3 pr-2">
          <input
            type="text"
            class="form-control form-control-sm mt-2"
            placeholder="名字"
            v-model="name"
          />
        </div>
        <div class="col-9">
          <input
            v-model="msg"
            @keydown.enter="sendBtn"
            type="text"
            class="form-control form-control-sm mt-2"
            placeholder="说点什么~"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      ws: "ws://localhost:9000/ws", // 连接地址
      websocket: null, // websocket对象
      name: "", // 用户名字
      msg: "", // 消息
      msgArr: [], // 收到消息队列
      resFlag: [
        // 状态 map
        { name: "开启!", state: WebSocket.OPEN },
        { name: "已经关闭!", state: WebSocket.CLOSED },
        { name: "正在关闭!", state: WebSocket.CLOSING },
        { name: "正在连接!", state: WebSocket.CONNECTING },
      ],
      // 当前状态
      readyState: WebSocket.CONNECTING,
    };
  },
  mounted() {
    this.init();
    this.getRandomName();
  },
  computed: {
    // 状态转换
    state() {
      let stateItem = this.resFlag.find((val) => val.state == this.readyState);
      return stateItem && stateItem.name;
    },
  },
  beforeDestroy() {
    if (this.websocket) {
      this.websocket.removeEventListener("message", this.getMessage);
      this.websocket.removeEventListener("close", this.stateChange);
      this.websocket.removeEventListener("error", this.stateChange);
      this.websocket.removeEventListener("open", this.stateChange);
    }
  },

  methods: {
    // 初始化
    init() {
      this.websocket = new WebSocket(this.ws);
      this.websocket.addEventListener("message", this.getMessage);
      this.websocket.addEventListener("close", this.stateChange);
      this.websocket.addEventListener("error", this.stateChange);
      this.websocket.addEventListener("open", this.stateChange);
    },
    // 刷新
    refsh() {
      alert("链接已刷新!");
      this.websocket && this.websocket.close();
      this.init();
    },
    // 随机获取名字
    getRandomName() {
      this.name = `游客(${Date.now()})`;
    },
    // 更新状态
    stateChange() {
      this.readyState = this.websocket.readyState;
    },
    sendBtn() {
      if (this.readyState == WebSocket.OPEN) {
        this.sendMessage();
      } else {
        alert("链接主机失败!");
      }
    },
    // 收到信息
    getMessage(result) {
      let json = this.messageToJson(result.data);
      this.msgArr.push(json);
      this.bodyToBottom();
    },
    // 消息移动到最底部
    bodyToBottom() {
      this.$nextTick(() => {
        let webSocket = this.$refs.websocketBody;
        console.log(webSocket.scrollHeight);
        webSocket.scrollTop = webSocket.scrollHeight;
      });
    },
    // 发送消息
    sendMessage() {
      this.websocket.send(
        // name 用户名 msg 消息 time 发送时间
        this.jsonToMessage({ name: this.name, msg: this.msg, time: Date.now() })
      );
      this.msg = "";
    },
    // 消息转换 json => str
    jsonToMessage(json) {
      return JSON.stringify(json);
    },
    // 消息转换 str => json
    messageToJson(msg) {
      return JSON.parse(msg);
    },
    // 时间格式化
    timestampFormat(stamp) {
      let time = new Date(stamp);
      return `${time.getHours()}:${time.getMinutes()}.${time.getSeconds()}`;
    },
  },
};
</script>

<style lang="less">
.websocket-body {
  overflow-y: scroll;
}
</style>
