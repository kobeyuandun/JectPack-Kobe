# Android Lint Check

## 描述
运行 Android Lint 检查并分析结果，提供详细的修复建议和优先级排序。

## 何时使用
- 代码提交前检查
- CI/CD 流水线集成
- 定期代码质量审查
- 发布前质量检查

## 工具需求
- Bash: 执行 lint 命令
- Read: 读取 lint 报告
- Grep: 搜索特定问题

## 执行步骤

### 1. 运行 Lint 检查

```bash
# 完整的 lint 检查
./gradlew lint

# 特定模块
./gradlew :app:lint

# 特定变体
./gradlew lintDebug

# 生成 HTML 报告
./gradlew lint lintVitalRelease
```

### 2. 查看报告

```bash
# 报告位置
# app/build/reports/lint-results.html
# app/build/reports/lint-results.xml

# 查看致命问题
./gradlew lintVital
```

### 3. 分析常见 Lint 问题

#### 常见严重问题
- `MissingPermission` - 缺少权限声明
- `UnusedResource` - 未使用的资源
- `HardcodedText` - 硬编码文本
- `SimpleDateFormat` - 线程安全问题
- `Recycle` - 资源未回收

#### 性能相关问题
- `Overdraw` - 过度绘制
- `NestedWeights` - 嵌套权重
- `ObsoleteLayoutParam` - 过时的布局参数
- `MergeRootFrame` - 可用 merge 优化

### 4. 生成修复建议

### 5. 自动修复（可能）

```bash
# 使用 lint-auto-fix 功能
./gradlew lintFix
```

## 常见 Lint 检查项

### 安全问题
- `ExportedContentProvider` - ContentProvider 导出
- `ExportedService` - Service 导出
- `HardcodedDebugMode` - 硬编码的调试模式
- `UnencryptedSharedPreferences` - 未加密的 SharedPreferences

### 兼容性
- `OldTargetApi` - 目标 API 过旧
- `Deprecated` - 使用了过时的 API
- `InlinedApi` - API 内联问题

### 国际化
- `HardcodedText` - 硬编码文本
- `MissingTranslation` - 缺少翻译
- `ImpliedQuantity` - 数量字符串处理

### 性能
- `ViewHolder` - 未使用 ViewHolder
- `UselessLeaf` - 无用的叶子布局
- `UseCompoundDrawables` - 可用 CompoundDrawables 优化

### 可用性
- `ButtonStyle` - 按钮样式
- `TextViewEdits` - 可编辑的 TextView
- `UselessParent` - 无用的父布局

## 优先级分类

### 致命 (Fatal)
- 阻止发布
- 必须立即修复

### 错误 (Error)
- 影响功能
- 应该尽快修复

### 警告 (Warning)
- 潜在问题
- 建议修复

### 信息 (Info)
- 优化建议
- 可以选择性修复

## 输出格式

```markdown
## Android Lint 检查报告

### 执行概览
- 检查时间：[时间戳]
- 检查模块：[模块名]
- 总问题数：X

### 问题统计
| 严重程度 | 数量 |
|---------|------|
| Fatal   | X    |
| Error   | Y    |
| Warning | Z    |
| Info    | W    |

### 致命问题 (必须修复)

#### [问题类型]
- 文件：`path/to/file.kt`
- 位置：第 X 行
- 问题描述：[详细说明]
- 修复建议：
  ```kotlin
  // 修复代码示例
  ```

### 错误问题 (应该修复)

#### [问题类型]
- 文件：`path/to/file.kt`
- 位置：第 X 行
- 问题描述：[详细说明]
- 修复建议：

### 警告问题 (建议修复)

#### [问题类型]
- 文件：`path/to/file.kt`
- 问题描述：[详细说明]

### 优化建议

#### 性能优化
- [优化建议]

#### 资源优化
- [未使用资源清理]
- [图片压缩建议]

### 修复命令
```bash
# 自动修复部分问题
./gradlew lintFix

# 手动修复后重新检查
./gradlew lint
```

### 报告文件
- HTML 报告：`app/build/reports/lint-results.html`
- XML 报告：`app/build/reports/lint-results.xml`
```

## 自定义 Lint 规则

### 创建自定义规则
```kotlin
class MyCustomLintDetector : Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes() = listOf(UCallExpression::class.java)

    override fun visitMethodCall(
        context: JavaContext,
        node: UCallExpression,
        method: PsiMethod
    ) {
        // 自定义检查逻辑
    }
}
```

### 注册规则
```xml
// lint.xml
<lint>
    <issue id="MyCustomIssue" severity="error"/>
</lint>
```

## 最佳实践
- 在 CI/CD 中集成 Lint 检查
- 将 Lint 阈值配置到 build.gradle
- 定期更新 Lint 版本
- 对于第三方库问题，使用 `tools:ignore`
- 记录忽略 Lint 的原因
