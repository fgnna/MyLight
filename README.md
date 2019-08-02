松果倾诉Android
=
（待补充）

IM和网络电话模块说明
-
IM功能主要应用在[松语](#)模块，内含多个模块合集：IM通信(环信、花火)，网络音视频通话(游密、声网)。
另外还有一个[电台](#)模块，是IM和网络电话结合使用的。
(如有错漏，请及时修正)
>#### 模块结构说明: 
>*  [/msg_call](#)：整个IM及音视频通信模块都集成到这个包里面
>    * [./agora](#)：声网调用类
>    * [./youme](#)：游密调用类
>    * [./sig](#)  ：网络语音的通信封装和实现
>    * [./state](#)  ：状态机，语音通话/电台通话过程中的状态处理
>    * [./msg](#)  ：IM消息对像类型，及IM客户端主要调用类
>    * [./socket](#)  ：一个独立的，与服务端交换信息的长连接
>* [/coco_im](#)  ：花火IM, [/msg_call](#)的依赖包,这个模块是公司自研IM系统(纯java编写)
>#### 主要类及业务说明:
>
>* 松语普通聊天（文字、图片表情、短语音）：使用环信与花火混搭通信，主要用环信，一但通信连接失败就走花火通道
>   >1. [EMClient.java](#) : 环信 SDK 主类 登录登出初始化等操作
>   >2. [TuohnIMClient.java](#) : 花火 SDK主类
>   >3. [ImHelper.java](#) : 两个IM的包装辅助类，登出登入初始化，做一些消息发送接收等操作
>
>* 松语语音聊天:使用游密与声网混搭，以声网为主通讯，当声网通讯服务异常时切换到游密（这块目前好像是由后台控制,前端判断后台参数来判断调用哪一个）
   有部份倾听者是有视频通话功能的，也是由后台去控制。
>
>   >1. [SGCall_V2.java](#) : **[核心类](#)该类作为音视频通话的统一调用者，包含当前通电话状态查询，通电话发起调用，
        状态的监听及回调处理，在涉及音视频通信的功能都会调用到该类**
>   >
>   >* [SigFactroyManager.java](#) : 用于获取对应供应商的电话统一回调实现类的实例，并在一定情况下
>   >   >*  [CallSig.java](#) : 统一的网络电话功能调用接口，最终状态回调处理由[./state](#)的实现类处理
>   >   >    *  [AgoraSig.java](#) : 声网网络电话实现类,声网是默认使用，如果声网登录失败或特殊条件下更换为HXSig.java处理
>   >   >    *  [HXSig.java](#) : 环信网络电话实现类，某些特定情况下才会使用，暂不清楚，可能是后台设定
>   >   >    *  [SGCallSig.java](#) : 游密网络电话实现类
>   >2. [MinaSocket.java](#) : **这是一个独立与我们服务进行通信的Socket,由[SocketUtil.java](#)封装调用，在[ReceiveMsgService.java](#)中进行初始化，
        其作用是接收来自服务端的一些自定义事件，比如通知后开启电台，接收通知，备份聊天记录，上传log等。**
>* 语音电台直播:电台模块为主播/倾诉者语音聊天直播,收听观众房间内文字聊天（声网/游密）
>   >* [AudioFactroyManager.java](#) : 决定使用哪一个供应商的实例,同样也是由**[SGCall_V2.java](#)**统一调用
>   >   >* [IAudioSDK.java](#) : 统一供应商公共接口封装
>   >   >    *  [YmrtcSDK.java](#) : 游密供应商实现类
>   >   >    *  [AgoraSDK.java](#) : 声网供应商实现类
>

其他
-
