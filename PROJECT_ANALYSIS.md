# 项目分析报告：JetPackKobe

## 1. 项目概览
JetPackKobe 是一个 Android 项目，旨在展示现代 Android 开发实践，重点在于使用 **Android Jetpack** 组件。它采用了模块化架构，将核心应用逻辑与通用库及工具分离开来。

## 2. 模块结构

该项目包含三个主要模块：

### **:app** (应用模块)
*   **类型**: Android 应用程序 (Application)
*   **包名**: `com.jetpack.kobe`
*   **描述**: 包含应用的主要业务逻辑和用户界面 (UI)。
*   **入口点**: `SplashActivity`, `MainActivity`。
*   **依赖**: 严重依赖 `:jplib` 获取基础架构和基类。
*   **主要包结构**:
    *   `ui`: 具体的页面实现。
    *   `base`: 应用特定的基类。
    *   `http`: 网络请求实现细节。
    *   `bean`: 数据模型。

### **:jplib** (库模块)
*   **类型**: Android 库 (Library)
*   **包名**: `com.jetpack.jplib`
*   **描述**: 核心基础架构层，提供 Activity/Fragment/ViewModel 的基类、网络配置和工具函数。它重新导出了许多第三方库，以便应用模块可以直接使用它们。
*   **核心能力**:
    *   **网络**: 封装了 Retrofit + OkHttp + RxJava/Coroutines 的设置。
    *   **基础架构**: 提供 Activity、Fragment 和 ViewModel 的基类。
    *   **工具**: 通用工具类。

### **buildSrc** (构建逻辑)
*   **类型**: Kotlin 模块
*   **描述**: 以集中的类型安全方式管理构建依赖和版本。
*   **文件**:
    *   `Version.kt`: 定义 SDK 版本（Min: 21, Target: 29）和应用版本代码。
    *   `ThirdParty.kt`: 定义外部库依赖（Retrofit, Glide 等）。
    *   `Android.kt` & `Kotlin.kt`: 定义 AndroidX 和 Kotlin 依赖。

## 3. 技术栈

### 语言与核心
*   **Kotlin**: 主要编程语言。
*   **Coroutines (协程)**: 用于异步编程。
*   **RxJava 2**: 响应式编程（可能正在过渡或与协程共存）。

### Android Jetpack (架构组件)
*   **Lifecycle**: `LiveData`, `ViewModel`。
*   **Navigation**: 支持单 Activity 架构。
*   **Room**: 本地数据库（根据依赖项推断）。
*   **Paging**: 用于分页加载数据。
*   **DataBinding**: 启用 UI 绑定。
*   **ViewPager2**: 现代分页支持。

### 网络与数据
*   **Retrofit 2**: REST 客户端。
*   **OkHttp**: HTTP 客户端。
*   **PersistentCookieJar**: Cookie 管理。
*   **Gson**: JSON 序列化。

### UI 与用户体验
*   **Glide**: 图片加载。
*   **SmartRefreshLayout**: 下拉刷新功能。
*   **BaseRecyclerViewAdapterHelper**: 简化的 RecyclerView 适配器。
*   **Material Design**: UI 组件。
*   **ConstraintLayout**: 布局引擎。
*   **Banner**: 图片轮播。
*   **MagicIndicator**: ViewPager 指示器。

### 工具与质量
*   **EventBus**: 发布/订阅事件总线。
*   **LeakCanary**: 内存泄漏检测（仅 Debug 模式）。
*   **BlockCanary**: UI 线程性能监控。
*   **权限管理**: `EasyPermissions`, `RxPermissions`。

## 4. 构建配置

*   **Gradle 配置**:
    *   使用 `basic.gradle` 在模块间共享配置。
    *   **构建类型**: `release`, `debug`, 和 `local`。
    *   **Min SDK**: 21 (Android 5.0)
    *   **Target SDK**: 29 (Android 10)

## 5. 架构模式

该项目遵循 **MVVM (Model-View-ViewModel)** 架构模式，利用 Jetpack 的 ViewModel 和 LiveData 实现。
*   **View**: Activity/Fragment（在 `:app` 和 `:jplib` 中）观察数据变化。
*   **ViewModel**:以此持有 UI 状态和业务逻辑。
*   **Model**: 仓库模式（Repository pattern，由 `http` 和 `bean` 包暗示），处理来自网络 (Retrofit) 或数据库 (Room) 的数据。
