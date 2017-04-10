
## 0. 最终效果

![demo](https://github.com/kompasim/android-wechat-tool/raw/master/demo.png)


## 1. Accessibility介绍

> 许多Android使用者因为各种情况导致他们要以不同的方式与手机交互。对于那些由于视力、听力或其它身体原因导致不能方便使用Android智能手机的用户，Android提供了Accessibility功能和服务帮助这些用户更加简单地操作设备，包括文字转语音、触觉反馈、手势操作、轨迹球和手柄操作。开发者可以搭建自己的Accessibility服务，这可以加强应用的可用性，例如声音提示，物理反馈，和其他可选的操作模式。

## 2. android无障碍功能

> 它的具体实现是通过AccessibilityService服务运行在后台中，通过AccessibilityEvent接收指定事件的回调。这样的事件表示用户在界面中的一些状态转换，例如：焦点改变了，一个按钮被点击，等等。这样的服务可以选择请求活动窗口的内容的能力。简单的说AccessibilityService就是一个后台监控服务，当你监控的内容发生改变时，就会调用后台服务的回调方法

## 3. AccessibilityService使用

编写自己的Service类，重写onServiceConnected()方法、onAccessibilityEvent()方法和onInterrupt()方法
```java
public class QHBAccessibilityService extends AccessibilityService {

    /**
     * 当启动服务的时候就会被调用
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    /**
     * 监听窗口变化的回调
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        //根据事件回调类型进行处理
    }

    /**
     * 中断服务的回调
     */
    @Override
    public void onInterrupt() {

    }
}
```
下面是对AccessibilityService中常用的方法的介绍

* disableSelf()：禁用当前服务，也就是在服务可以通过该方法停止运行
* findFoucs(int falg)：查找拥有特定焦点类型的控件
* getRootInActiveWindow()：如果配置能够获取窗口内容,则会返回当前活动窗口的根结点
* getSeviceInfo()：获取当前服务的配置信息
* onAccessibilityEvent(AccessibilityEvent event)：有关AccessibilityEvent事件的回调函数，系统通过sendAccessibiliyEvent()不断的发送AccessibilityEvent到此处
* performGlobalAction(int action)：执行全局操作，比如返回，回到主页，打开最近等操作
* setServiceInfo(AccessibilityServiceInfo info)：设置当前服务的配置信息
* getSystemService(String name)：获取系统服务
* onKeyEvent(KeyEvent event)：如果允许服务监听按键操作，该方法是按键事件的回调，需要注意，这个过程发生了系统处理按键事件之前
* onServiceConnected()：系统成功绑定该服务时被触发，也就是当你在设置中开启相应的服务，系统成功的绑定了该服务时会触发，通常我们可以在这里做一些初始化操作
* onInterrupt()：服务中断时的回调

## 4. 声明服务

既然是个后台服务，那么就需要我们在manifests中配置该服务信息
```xml
<service
    android:name=".AccessibilityService.QHBAccessibilityService"
    android:enabled="true"
    android:exported="true"
    android:label="@string/label"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
</service>
```
我们必须注意：任何一个信息配置错误，都会使该服务无反应

* android:label：在无障碍列表中显示该服务的名字
* android:permission：需要指定BIND_ACCESSIBILITY_SERVICE权限，这是4.0以上的系统要求的
* intent-filter：这个name是固定不变的


## 5. 配置服务参数

配置服务参数是指：配置用来接受指定类型的事件，监听指定package，检索窗口内容，获取事件类型的时间等等。其配置服务参数有两种方法：
* 方法一：安卓4.0之后可以通过meta-data标签指定xml文件进行配置
* 方法二：通过代码动态配置参数

### 方法一

在原先的manifests中增加meta-data标签指定xml文件

```xml
<service
    android:name=".AccessibilityService.QHBAccessibilityService"
    android:enabled="true"
    android:exported="true"
    android:label="@string/label"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>

    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>
```

接下来是accessibility_service_config文件的配置

```xml
<?xml version="1.0" encoding="utf-8"?>
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeNotificationStateChanged|typeWindowStateChanged|typeWindowContentChanged|typeWindowsChanged"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagDefault"
    android:canRetrieveWindowContent="true"
    android:description="@string/description"
    android:notificationTimeout="100"
    android:packageNames="com.tencent.mm" />
```

下面是对xml参数的介绍

* accessibilityEventTypes：表示该服务对界面中的哪些变化感兴趣，即哪些事件通知，比如窗口打开，滑动，焦点变化，长按等。具体的值可以在AccessibilityEvent类中查到，如typeAllMask表示接受所有的事件通知
* accessibilityFeedbackType：表示反馈方式，比如是语音播放，还是震动
* canRetrieveWindowContent：表示该服务能否访问活动窗口中的内容。也就是如果你希望在服务中获取窗体内容，则需要设置其值为true
* description：对该无障碍功能的描述，具体体现在下图 

* notificationTimeout：接受事件的时间间隔，通常将其设置为100即可
* packageNames：表示对该服务是用来监听哪个包的产生的事件，这里以微信的包名为例

### 方法二

通过代码为我们的AccessibilityService配置AccessibilityServiceInfo信息，这里我们可以抽取成一个方法进行设置

```java
private void settingAccessibilityInfo() {
    String[] packageNames = {"com.tencent.mm"};
    AccessibilityServiceInfo mAccessibilityServiceInfo = new AccessibilityServiceInfo();
    // 响应事件的类型，这里是全部的响应事件（长按，单击，滑动等）
    mAccessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
    // 反馈给用户的类型，这里是语音提示
    mAccessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
    // 过滤的包名
    mAccessibilityServiceInfo.packageNames = packageNames;
    setServiceInfo(mAccessibilityServiceInfo);
}
```

在这里涉及到了AccessibilityServiceInfo类，AccessibilityServiceInfo类被用于配置AccessibilityService信息，该类中包含了大量用于配置的常量字段及用来xml属性，常见的有：accessibilityEventTypes，canRequestFilterKeyEvents，packageNames等等



## 6. 启动服务

这里我们需要在无障碍功能里面手动打开该项功能，否则无法继续进行，通过下面代码可以打开系统的无障碍功能列表

```java
Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
startActivity(intent);
```

## 6. 处理事件信息

由于我们监听了事件的通知栏和界面等信息，当我们指定packageNames的通知栏或者界面发生变化时，会通过onAccessibilityEvent回调我们的事件，接着进行事件的处理

```java
@Override
public void onAccessibilityEvent(AccessibilityEvent event) {
    int eventType = event.getEventType();
    //根据事件回调类型进行处理
    switch (eventType) {
        //当通知栏发生改变时
        case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:

            break;
        //当窗口的状态发生改变时
        case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

            break;
    }
}
```

当我们微信收到通知时，状态栏会有一条推送信息到达，这个时候就会被TYPE_NOTIFICATION_STATE_CHANGED监听，执行里面的内容，当我们切换微信界面时，或者使用微信时，这个时候就会被TYPE_WINDOW_STATE_CHANGED监听，执行里面的内容

AccessibilityEvent的方法

* getEventType()：事件类型
* getSource()：获取事件源对应的结点信息
* getClassName()：获取事件源对应类的类型，比如点击事件是有某个Button产生的，那么此时获取的就是Button的完整类名
* getText()：获取事件源的文本信息，比如事件是有TextView发出的,此时获取的就是TextView的text属性。如果该事件源是树结构，那么此时获取的是这个树上所有具有text属性的值的集合
* isEnabled()：事件源(对应的界面控件)是否处在可用状态
* getItemCount()：如果事件源是树结构，将返回该树根节点下子节点的数量


## 7. 获取节点信息

获取了界面窗口变化后，这个时候就要获取控件的节点。整个窗口的节点本质是个树结构，通过以下操作节点信息

* 获取窗口节点（根节点）
```java
AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
```

* 获取指定子节点（控件节点）

```java
//通过文本找到对应的节点集合
List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
//通过控件ID找到对应的节点集合，如com.tencent.mm:id/gd
List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
```

## 8. 模拟节点点击

当我们获取了节点信息之后，对控件节点进行模拟点击、长按等操作，AccessibilityNodeInfo类提供了performAction()方法让我们执行模拟操作，具体操作可看官方文档介绍，这里列举常用的操作

```java
//模拟点击
accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//模拟长按
accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
//模拟获取焦点
accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
//模拟粘贴
accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
```

## 9.  原理分析

1. 收到微信红包的推送信息，在推送信息中判断是否出现”[微信红包]”的消息提示，如果出现则点击进入聊天界面
2. 通过遍历窗口树节点，发现带有”领取红包”字样的节点，则点击进入，即红包，弹出抢红包界面
3. 在抢红包界面，通过ID获取”开”按钮的节点，则打开红包
4. 在红包详情页面，通过ID获取返回键按钮的节点，点击并返回微信聊天界面


## 10. 注意事项

1. 由于微信每个版本的按钮ID都是不一样的，在我们的程序中是需要去修改按钮ID，以达到版本的适配
2. 在获取控件ID的时候，注意其布局是否可点击，否则获取不可点击的控件，会使程序无反应

## 11. 获取控件ID

当我们手机接入USB线时，在Android Device Monitor中的选择设备并开启Dump View Hierarchy for UI Automator工具，通过它可以获取控件信息

## 12. 遇到的一些问题

* 1 : 
> Q: `AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();`总是报空指针，问一下博主遇到过这个问题吗?
> A: 我也遇到你这样的问题了，我觉得是“配置服务参数”那里的，我刚开始用在代码里面配置，也是返回null，我感觉是没有写这个android:canRetrieveWindowContent="true"，但是没有找到android:canRetrieveWindowContent="true"对应的设置，所以我就改成在xml里面配置了，就可以了。

* 2 :
> Q: 请问这些类名com.tencent.mm.ui.LauncherUI是如何获取的？
> A: String className = event.getClassName().toString();使用Log打印出来，打开微信看Log信息

## 13. 其他

[抢红包程序介绍](/2017/04/10/android-wechat-rob/)

[原文：Hensen_的博客](http://img.blog.csdn.net/20161121130452281)

[源码；Github仓库](https://github.com/kompasim/android-wechat-tool)


