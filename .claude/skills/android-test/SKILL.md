# Android Test Generator

## 描述
为 Android 代码生成完整的测试用例，包括单元测试、UI 测试和集成测试。遵循 Android 测试最佳实践。

## 何时使用
- 新功能开发完成后
- 为现有代码补充测试
- 重构前增加测试覆盖
- TDD 开发模式

## 工具需求
- Read: 读取待测试的文件
- Write: 创建测试文件
- Edit: 修改现有测试
- Bash: 运行测试命令

## 执行步骤

### 1. 分析待测试代码
- 读取目标源文件
- 识别类、函数及其职责
- 分析依赖关系
- 确定测试类型（单元测试/集成测试/UI测试）

### 2. 确定测试策略

#### 单元测试 (test/ 目录)
- ViewModel 测试
- Repository 测试
- Utility 函数测试
- 业务逻辑测试

#### 仪器测试 (androidTest/ 目录)
- UI 测试（Compose 或 View）
- 数据库测试
- 仪器集成测试

### 3. 生成测试用例

#### 测试框架选择
```kotlin
// 单元测试框架
- JUnit 5
- MockK (mocking)
- kotlinx-coroutines-test
- Robolectric (Android framework)

// UI 测试框架
- Compose Testing (for Jetpack Compose)
- Espresso (for Views)
- UI Automator
```

### 4. 编写测试代码

#### ViewModel 测试模板
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest {
    private lateinit var viewModel: MyViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // 初始化 mocks 和 viewModel
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when action, then expected result`() = runTest {
        // Given
        // When
        // Then
    }
}
```

#### Repository 测试模板
```kotlin
class MyRepositoryTest {
    private lateinit var repository: MyRepository
    private val mockApi = mockk<MyApi>()
    private val mockDao = mockk<MyDao>()

    @Before
    fun setup() {
        repository = MyRepository(mockApi, mockDao)
    }

    @Test
    fun `fetch data successfully returns data`() = runTest {
        // Given
        coEvery { mockApi.getData() } returns expectedData

        // When
        val result = repository.fetchData()

        // Then
        assertThat(result).isEqualTo(expectedData)
    }
}
```

#### Compose UI 测试模板
```kotlin
class MyComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `clicking button triggers action`() {
        // Given
        var clicked = false
        composeTestRule.setContent {
            MyComponent(onClick = { clicked = true })
        }

        // When
        composeTestRule.onNodeWithText("Click me").performClick()

        // Then
        assertThat(clicked).isTrue()
    }
}
```

## 测试覆盖清单

### 功能测试
- [ ] 正常流程（Happy Path）
- [ ] 边界条件（空值、最大最小值）
- [ ] 异常情况（网络错误、数据格式错误）
- [ ] 状态变化（Loading、Success、Error）

### ViewModel 测试
- [ ] 初始状态验证
- [ ] LiveData/StateFlow 发射验证
- [ ] 协程正确执行
- [ ] 错误处理正确

### Repository 测试
- [ ] API 调用成功
- [ ] API 调用失败
- [ ] 缓存机制正确
- [ ] 数据转换正确

### UI 测试
- [ ] 组件渲染正确
- [ ] 用户交互响应正确
- [ ] 状态变化显示正确
- [ ] 导航流程正确

## 运行测试

```bash
# 单元测试
./gradlew test
./gradlew testDebugUnitTest

# 特定类测试
./gradlew test --tests "com.example.MyViewModelTest"

# 带覆盖率
./gradlew testDebugUnitTestCoverage

# UI 测试
./gradlew connectedAndroidTest

# 特定设备
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.MyUiTest
```

## 输出格式

```markdown
## Android 测试生成报告

### 分析结果
- 目标文件：[文件路径]
- 测试类型：[单元测试/集成测试/UI测试]
- 建议测试文件：[测试文件路径]

### 生成的测试用例

#### 单元测试
- [测试名称]
  - 场景：[测试场景描述]
  - 代码：[测试代码]

#### 集成测试
- [测试名称]
  - 场景：[测试场景描述]
  - 代码：[测试代码]

#### UI 测试
- [测试名称]
  - 场景：[测试场景描述]
  - 代码：[测试代码]

### Mock 需求
- [需要 mock 的依赖列表]

### 运行命令
```bash
[执行测试的命令]
```

### 测试覆盖目标
- 当前覆盖率：X%
- 目标覆盖率：80%
```

## 最佳实践
- 遵循 AAA 模式（Arrange-Act-Assert）
- 使用描述性的测试名称
- 测试应该独立且快速
- 避免测试实现细节，测试行为
- 使用 Given-When-Then 注释
- 对于协程使用 runTest 和 TestDispatcher
- 使用 MockK 进行 mock
- 对于 LiveData/Flow 使用 Turbine
