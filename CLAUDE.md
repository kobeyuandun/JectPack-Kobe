# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此代码库中工作时提供指导。

## 项目概述

JetPackKobe 是一个展示 Jetpack 组件最佳实践的 Android 应用。这是一个基于 Kotlin 构建的"玩安卓"内容应用，主要功能是文章阅读。

**架构模式**：MVVM + 单 Activity 架构 + Repository 模式 + 模块化设计

## 模块结构

- **`:app`** - 主应用模块 (`com.jetpack.kobe`)
  - 包含 UI、业务逻辑、网络层和 ViewModels
  - 使用 WanAndroid API (https://www.wanandroid.com) 获取数据

- **`:jplib`** - 核心库模块 (`com.jetpack.jplib`)
  - 提供基础类：`BaseApplication`、`BaseActivity`、`BaseFragment`、`BaseLoadingActivity`
  - 共享工具类和网络配置
  - 包含 `BaseViewModel`（带协程错误处理）和 `BaseRepository`（带 IO 上下文切换）

- **`:buildSrc`** - 构建配置
  - 通过 `Version.kt`、`ThirdParty.kt`、`Android.kt`、`Kotlin.kt` 集中管理依赖版本

## 构建命令

```bash
# 构建项目
./gradlew build

# 运行单元测试
./gradlew test
./gradlew :app:test
./gradlew :jplib:test

# 运行设备测试
./gradlew connectedAndroidTest

# Lint 检查
./gradlew lint

# 生成 APK
./gradlew assembleDebug    # 输出: app/build/outputs/apk/debug/玩安卓-debug.apk
./gradlew assembleRelease  # 输出: app/build/outputs/apk/release/玩安卓.apk
```

## 核心架构模式

### 导航流程
- 单个 `MainActivity` 托管 `MainFragment`
- `MainFragment` 包含 ViewPager2，共有 5 个标签页：首页、项目、广场、公众号、我的
- 底部导航栏用于标签切换

### 基类继承体系
- 所有 Activity 继承 `BaseActivity` 或 `BaseLoadingActivity`（来自 `:jplib`）
- 所有 Fragment 继承 `BaseFragment`（来自 `:jplib`）

### 数据层
- Repository 模式抽象数据源
- ViewModels 处理业务逻辑，通过 LiveData 暴露状态
- Retrofit + OkHttp 进行网络请求，支持持久化 Cookie 存储
- `RetrofitManager` 提供单例 API 服务实例

### 核心功能
- **语音通话**：`VoiceCallActivity` 集成 Agora RTC SDK (4.0.1)，带波纹动画 UI
- **视频流**：`VideoFeedFragment` 使用 ExoPlayer (2.19.1)
- **聊天**：`ChatFragment` 和 `ComposeChatFragment` 消息界面
- **启动页**：`SplashActivity` 应用初始化

## 技术栈

- **Kotlin 1.8.22** + 协程 1.7.3
- **Jetpack**: Navigation 2.7.6、Lifecycle (LiveData, ViewModel)、Paging 3.2.1、DataBinding、Compose
- **网络**: Retrofit 2.9.0、OkHttp 4.9.0、Gson
- **图片加载**: Glide 4.11.0
- **UI**: Material Design、ViewPager2、SmartRefreshLayout、ConstraintLayout、Jetpack Compose
- **语音**: Agora Voice SDK 4.0.1
- **视频**: ExoPlayer 2.19.1

## 依赖管理

所有第三方库版本在 `:buildSrc` 中集中管理：
- `ThirdParty.kt` - 外部库版本（Retrofit、OkHttp、Glide、Agora、ExoPlayer 等）
- `Version.kt` - SDK 版本（最低 21，目标 34）、Compose 版本
- 在 `buildSrc` 中添加/更新依赖，而非在各个模块的 build 文件中

## 构建配置

- **最低 SDK**: 21 (Android 5.0)
- **目标 SDK**: 34 (Android 14)
- **编译 SDK**: 34
- **构建变体**: debug、release、local
- **Java 工具链**: Java 17
- Release 构建启用代码混淆
- 启用 MultiDex

## 构建配置文件

- `basic.gradle` - 所有模块共享的构建配置
- 定义了 DataBinding、BuildConfig 和带 BASE_URL 的构建变体

## 网络层

- `RetrofitFactory` - 使用 OkHttp 创建 Retrofit 单例
- `RetrofitManager` - 管理 API 服务实例
- 通过 `PersistentCookieJar` 实现 Cookie 持久化
- HTTP 日志拦截器用于调试
- Base URL: `https://www.wanandroid.com`

## 关键文件位置

- 基类：`jplib/src/main/java/com/jetpack/jplib/base/`
- 网络：`app/src/main/java/com/jetpack/kobe/http/`
- UI 片段：`app/src/main/java/com/jetpack/kobe/ui/`
- ViewModels：与功能包同目录（如 `ui/main/home/HomeVM.kt`）
- Repositories：与功能包同目录（如 `ui/main/home/HomeRepo.kt`）

## 已知问题与注意事项

1. **BlockCanary 和 LeakCanary 已禁用** - Manifest 兼容性问题，在 `jplib/build.gradle` 中已注释
2. **Room 依赖已添加但未完全实现** - Room runtime 和 KTX 已包含但使用较少
