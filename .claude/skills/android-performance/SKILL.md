# Android Performance Analyzer

## 描述
分析 Android 应用性能问题，提供优化建议，包括启动速度、UI 渲染、内存使用、网络请求等方面。

## 何时使用
- 应用出现卡顿或 ANR
- 启动速度慢
- 内存占用高
- 电池消耗快
- 定期性能检查

## 工具需求
- Read: 读取代码文件
- Grep: 搜索性能相关代码模式
- Bash: 运行性能分析工具

## 分析维度

### 1. 启动性能分析

#### 冷启动优化
```bash
# 使用 logcat 查看启动时间
adb logcat | grep "Displayed"

# 或使用 ADB 命令
adb shell am start -W [packageName]/[activityName]
```

#### 常见问题
- Application 初始化耗时
- 主 Activity 初始化耗时
- 首屏布局复杂
- 同步初始化组件

#### 优化建议
```kotlin
// 1. 延迟初始化非关键组件
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 必要的初始化
        initializeEssential()

        // 延迟初始化
        lifecycleScope.launch {
            delay(1000)
            initializeNonEssential()
        }
    }
}

// 2. 使用 splash screen
// 3. 异步加载首屏数据
```

### 2. UI 渲染性能

#### 检查过度绘制
```bash
# 开发者选项 > 调试 GPU 过度绘制
```

#### 检查掉帧
```bash
# 开发者选项 > GPU 渲染模式分析 > 在屏幕上显示为条形图
```

#### 常见问题
- 布局层级过深
- 不必要的背景绘制
- 主线程耗时操作

#### 优化建议
```kotlin
// 1. 使用 ConstraintLayout 减少层级
// 2. 移除不必要的背景
// 3. 使用 ViewStub 延迟加载
<ViewStub
    android:id="@+id/stub_import"
    android:layout="@layout/import_panel"
    android:inflatedId="@+id/panel_import"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom" />

// 4. RecyclerView 使用 DiffUtil
// 5. 使用 Glide 的 RequestOptions 优化图片加载
```

### 3. 内存分析

#### 检测内存泄漏
```bash
# 使用 Android Studio Profiler
# 或使用 LeakCanary
dependencies {
    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.12'
}
```

#### 常见内存泄漏场景
```kotlin
// ❌ 非静态内部类持有外部类引用
class MyActivity : AppCompatActivity() {
    inner class MyInnerClass {
        // 持有 MyActivity 的引用
    }
}

// ✅ 使用静态内部类 + WeakReference
class MyActivity : AppCompatActivity() {
    private class MyInnerClass(activity: MyActivity) {
        private val activityRef = WeakReference(activity)
    }
}

// ❌ 长生命周期对象持有短生命周期对象
object MySingleton {
    var context: Context? = null // 泄漏！

    // ✅ 使用 ApplicationContext
    var appContext: Context? = null
}

// ❌ 未取消的协程
// ✅ 使用 viewModelScope 或 lifecycleScope
```

### 4. 网络性能

#### 优化建议
```kotlin
// 1. 使用缓存
@Headers("Cache-Control: max-age=640000")
@GET("api/data")
suspend fun getData(): Response<Data>

// 2. 批量请求
// 3. 压缩请求数据
// 4. 使用 HTTP/2 或 HTTP/3
// 5. 预取关键数据

// 4. 使用 OkHttp 缓存
val cache = Cache(cacheDir, 10 * 1024 * 1024) // 10MB
val client = OkHttpClient.Builder()
    .cache(cache)
    .build()
```

### 5. 数据库性能

#### 优化建议
```kotlin
// 1. 使用索引
@Entity(indices = [Index(value = ["name"])])
data class User(@ColumnInfo(name = "name") val name: String)

// 2. 批量操作
@Insert
suspend fun insertAll(users: List<User>)

// 3. 使用事务
database.withTransaction {
    // 多个数据库操作
}

// 4. 避免在主线程查询
// 5. 使用 Flow 实现响应式更新
@Query("SELECT * FROM users")
fun getAllUsers(): Flow<List<User>>
```

### 6. 图片加载优化

```kotlin
// Glide 优化
Glide.with(context)
    .load(url)
    .placeholder(placeholder)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .override(targetWidth, targetHeight) // 指定尺寸
    .into(imageView)

// 1. 使用合适格式（WebP 优先）
// 2. 提供多倍图
// 3. 使用缩略图
// 4. 禁用内存缓存（对于频繁变化的图片）
```

### 7. APK 大小优化

```bash
# 分析 APK
./gradlew assembleDebug
# 使用 Android Studio APK Analyzer

# 常见优化
# 1. 启用代码混淆
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
        }
    }
}

# 2. 启用 App Bundle
# 3. 动态功能模块
# 4. 资源压缩
# 5. 优化图片和字体
```

## 检查清单

### 启动性能
- [ ] Application onCreate 耗时 < 100ms
- [ ] 首屏 Activity 启动 < 500ms
- [ ] 首屏数据加载 < 1s
- [ ] 避免同步初始化

### UI 性能
- [ ] 布局层级 < 5 层
- [ ] 无明显掉帧（16ms/帧）
- [ ] 过度绘制 < 2x
- [ ] RecyclerView 使用 ViewHolder 和 DiffUtil

### 内存性能
- [ ] 无内存泄漏
- [ ] 峰值内存合理
- [ ] 及时释放资源
- [ ] Bitmap 正确回收

### 网络性能
- [ ] 启用请求缓存
- [ ] 图片有磁盘缓存
- [ ] 批量请求减少往返
- [ ] 使用高效的序列化

### 电池性能
- [ ] 合理使用 WorkManager
- [ ] 避免频繁唤醒
- [ ] 定位服务按需使用
- [ ] 后台任务优化

## 性能分析命令

```bash
# 启动时间
adb shell am start -W com.example.app/.MainActivity

# CPU 使用率
adb shell top -n 1 | grep [packageName]

# 内存信息
adb shell dumpsys meminfo [packageName]

# 网络统计
adb shell cat /proc/net/xt_qtaguid/stats

# GPU 渲染
adb shell setprop debug.hwui.profile true
adb shell setprop debug.hwui.show_dirty_regions true
```

## 输出格式

```markdown
## Android 性能分析报告

### 分析概览
- 应用版本：[版本号]
- 分析时间：[时间戳]
- 测试设备：[设备信息]

### 启动性能
- 冷启动时间：X ms（目标：< 1000ms）
- 热启动时间：Y ms（目标：< 300ms）
- 首屏时间：Z ms

#### 问题与建议
- [问题描述]
  - 影响：[影响说明]
  - 优化建议：[具体方案]
  - 预期改善：[预期效果]

### UI 渲染性能
- 平均帧率：X fps
- 掉帧次数：Y
- 过度绘制：Z%

#### 问题与建议
- [问题描述]

### 内存性能
- 堆内存：X MB
- Native 内存：Y MB
- 检测到泄漏：[是/否]

#### 内存泄漏分析
- [泄漏位置]
  - 修复建议：[修复方案]

### 网络性能
- 平均请求时间：X ms
- 缓存命中率：Y%
- 图片加载时间：Z ms

### APK 大小
- 当前大小：X MB
- 优化后大小：Y MB
- 减少：Z%

### 优化建议优先级

#### 高优先级（立即处理）
1. [优化项]
   - 原因：[原因]
   - 方案：[方案]

#### 中优先级（近期处理）
1. [优化项]

#### 低优先级（长期优化）
1. [优化项]

### 性能目标
| 指标 | 当前 | 目标 | 状态 |
|-----|------|------|------|
| 启动时间 | 1200ms | <1000ms | ❌ |
| 帧率 | 50fps | 60fps | ⚠️ |
| 内存峰值 | 200MB | <150MB | ❌ |
```

## 性能监控集成

### Firebase Performance
```gradle
dependencies {
    implementation platform('com.google.firebase:firebase-bom:xx.x.x')
    implementation 'com.google.firebase:firebase-perf'
}
```

### 自定义性能追踪
```kotlin
// Firebase Performance
val trace = Firebase.performance.newTrace("screen_load")
trace.start()
// ... 屏幕加载代码
trace.stop()

// 性能日志记录
android {
    buildConfigField "String", "PERF_API_KEY", "\"${getPerfApiKey()}\""
}
```

## 最佳实践
- 使用 Profiler 定期分析性能
- 建立性能基线和监控指标
- 关键路径代码优先优化
- 优化后进行 A/B 测试
- 持续监控生产环境性能
