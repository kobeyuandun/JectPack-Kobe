# Android Migration Helper

## 描述
帮助进行 Android 常见迁移任务，如 Kotlin Synthetics 迁移到 ViewBinding、Java 迁移到 Kotlin、旧版 API 迁移等。

## 何时使用
- 从 Kotlin Synthetics 迁移到 ViewBinding
- 从 Java 迁移到 Kotlin
- 升级 Android SDK/Gradle
- 迁移到 Jetpack 库
- 迁移到 Material Design 3

## 工具需求
- Read: 读取源文件
- Write: 创建新文件
- Edit: 修改文件
- Bash: 执行迁移命令
- Grep: 搜索待迁移的代码

## 支持的迁移类型

### 1. Kotlin Synthetics → ViewBinding

#### 步骤
1. 启用 ViewBinding
```gradle
// build.gradle (app module)
android {
    buildFeatures {
        viewBinding true
    }
}
```

2. 识别所有使用 Synthetics 的文件
```bash
grep -r "kotlinx.android.synthetic" --include="*.kt" .
```

3. 迁移代码
```kotlin
// 旧代码
import kotlinx.android.synthetic.main.activity_main.*

button.text = "Hello"

// 新代码
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.button.text = "Hello"
}

override fun onDestroy() {
    super.onDestroy()
    // 避免内存泄漏
    if (::binding.isInitialized) {
        binding.root.unbind()
    }
}
```

### 2. Java → Kotlin

#### 歁骤
1. 使用 Android Studio 的自动转换
   - Code → Convert Java File to Kotlin File

2. 手动优化转换后的代码
   - 使用 Kotlin 惯用法
   - 使用数据类
   - 使用扩展函数
   - 使用协程替代回调

```kotlin
// Java 代码
public class User {
    private String name;
    private int age;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    // ...
}

// Kotlin 代码
data class User(
    var name: String? = null,
    var age: Int = 0
)
```

### 3. findViewById → ViewBinding/DataBinding

```kotlin
// 旧代码
val textView = findViewById<TextView>(R.id.textView)
textView.text = "Hello"

// ViewBinding
binding.textView.text = "Hello"

// DataBinding
binding.text = "Hello"
```

### 4. AsyncTask → 协程

```kotlin
// 旧代码
class MyTask : AsyncTask<Void, Void, Result>() {
    override fun doInBackground(vararg params: Void): Result {
        // 耗时操作
        return result
    }

    override fun onPostExecute(result: Result) {
        // 更新 UI
    }
}

// 新代码（协程）
lifecycleScope.launch {
    val result = withContext(Dispatchers.IO) {
        // 耗时操作
    }
    // 更新 UI
}
```

### 5. RxJava → Flow

```kotlin
// RxJava
observable
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe({ result ->
        // 处理结果
    }, { error ->
        // 处理错误
    })

// Flow
flow
    .flowOn(Dispatchers.IO)
    .catch { error ->
        // 处理错误
    }
    .collect { result ->
        // 处理结果
    }
```

### 6. Support Library → AndroidX

```bash
# 使用迁移工具
./gradlew migrateToAndroidX
```

### 7. LiveData → StateFlow/SharedFlow

```kotlin
// LiveData
private val _data = MutableLiveData<Data>()
val data: LiveData<Data> = _data

// StateFlow
private val _data = MutableStateFlow<Data?>(null)
val data: StateFlow<Data?> = _data.asStateFlow()

// 在 UI 中收集
lifecycleScope.launch {
    viewModel.data.collect { data ->
        // 更新 UI
    }
}
```

### 8. onActivityResult → Activity Result API

```kotlin
// 旧代码
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        // 处理结果
    }
}

// 新代码
val startForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 处理结果
        }
    }

startForResult.launch(intent)
```

### 9. SharedPreferences → DataStore

```kotlin
// SharedPreferences
val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
with(sharedPrefs.edit()) {
    putString("key", "value")
    apply()
}

// DataStore Preferences
val Context.dataStore by preferencesDataStore("settings")

suspend fun saveValue(context: Context, key: String, value: String) {
    context.dataStore.edit { preferences ->
        preferences[stringPreferencesKey(key)] = value
    }
}
```

## 执行步骤

### 1. 分析当前代码
```bash
# 查找待迁移的代码模式
grep -r "kotlinx.android.synthetic" --include="*.kt"
grep -r "findViewById" --include="*.kt"
grep -r "AsyncTask" --include="*.kt"
grep -r "android.support" --include="*.kt"
```

### 2. 生成迁移计划
- 列出所有需要修改的文件
- 按优先级排序
- 识别潜在风险

### 3. 执行迁移
- 按模块分批迁移
- 每次迁移后运行测试
- 验证功能正常

### 4. 验证
```bash
# 编译检查
./gradlew assembleDebug

# 运行测试
./gradlew test

# Lint 检查
./gradlew lint
```

## 输出格式

```markdown
## Android 迁移方案

### 迁移类型
[迁移类型名称]

### 分析结果
- 涉及文件数：X
- 需要修改的类：Y
- 预计工作量：[时间估计]

### 迁移计划

#### 第一阶段：准备工作
1. [ ] 备份当前代码
2. [ ] 更新 build.gradle 配置
3. [ ] 同步依赖

#### 第二阶段：代码迁移
1. [ ] 迁移文件列表
   - `path/to/file1.kt`
   - `path/to/file2.kt`

2. [ ] 迁移示例
   ```kotlin
   // 迁移前后代码对比
   ```

#### 第三阶段：验证
1. [ ] 编译检查
2. [ ] 运行测试
3. [ ] 手动测试

### 风险评估
- [潜在风险]
- [缓解措施]

### 回滚方案
- Git 提交前创建备份分支
```

## 最佳实践
- 迁移前确保代码有 Git 版本控制
- 分批迁移，避免大量代码一次性变更
- 每次迁移后运行完整测试
- 保留旧代码的注释对比，便于审查
- 更新相关文档
- 通知团队成员迁移变更
