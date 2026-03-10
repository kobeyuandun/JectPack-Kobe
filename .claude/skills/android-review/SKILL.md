# Android Code Review

## 描述
对 Android 代码进行全面审查，检查代码质量、规范合规性、潜在问题和 Android 最佳实践。

## 何时使用
- Pull Request 代码审查
- 提交代码前自检
- 代码质量检查
- 团队代码评审

## 工具需求
- Read: 读取文件
- Grep: 搜索特定模式
- Bash: 执行 lint 和检查命令

## 执行步骤

### 1. 获取变更文件
```bash
git diff --name-only main...HEAD
```

### 2. 代码质量检查

#### Kotlin 代码规范
- 检查是否符合 Kotlin 编码规范
- 检查是否有未使用的 import
- 检查变量命名是否清晰
- 检查函数是否遵循单一职责原则

#### Android 最佳实践
- 检查是否正确处理生命周期
- 检查是否有内存泄漏风险（如非静态内部类、持有 Context）
- 检查是否正确使用 LiveData/Flow
- 检查是否有主线程耗时操作

#### Jetpack 组件使用
- ViewModel 中是否持有 View 或 Activity 引用
- LiveData 是否正确使用.postValue() vs setValue()
- 是否正确使用 DataBinding/ViewBinding
- Room 数据库操作是否在后台线程

### 3. 安全检查
- 是否有硬编码的敏感信息（API Key、密码等）
- Log 输出是否包含敏感信息
- 网络请求是否使用 HTTPS
- 是否有 SQL 注入风险

### 4. 资源检查
- 硬编码字符串是否应该放到 strings.xml
- 图片资源是否存在多倍图（mdpi、hdpi、xhdpi 等）
- 颜色值是否应该放到 colors.xml

### 5. 运行静态检查
```bash
./gradlew lint
./gradlew detekt    # 如果配置了 detekt
```

## 检查清单

### 代码质量
- [ ] 代码风格符合 Kotlin 规范
- [ ] 变量命名清晰有意义
- [ ] 函数职责单一，长度适中（< 50 行）
- [ ] 避免深层嵌套（不超过 3 层）
- [ ] 正确使用 Kotlin 特性（let、apply、with 等）

### 架构设计
- [ ] MVVM 架构分层清晰
- [ ] Repository 模式正确使用
- [ ] View 和 ViewModel 交互规范
- [ ] 数据流向清晰（单向数据流）

### 生命周期与内存
- [ ] 无内存泄漏风险
- [ ] 正确处理生命周期事件
- [ ] 协程作用域选择正确（viewModelScope、lifecycleScope）
- [ ] 取消不必要的订阅

### UI 相关
- [ ] 避免在 ViewGroup 上使用 background（性能问题）
- [ ] RecyclerView 使用了 ViewHolder 模式
- [ ] 避免过度嵌套布局
- [ ] 正确使用 ConstraintLayout

### 性能优化
- [ ] 避免主线程进行 I/O 操作
- [ ] 图片加载使用了 Glide/Coil
- [ ] 列表使用了 DiffUtil
- [ ] 避免频繁的对象创建

## 输出格式

```markdown
## Android 代码审查报告

### 变更概览
- 文件数：X
- Kotlin 文件：Y
- XML 文件：Z

### 发现的问题

#### 🔴 严重问题（必须修复）
- [问题描述]
  - 文件：path/to/file.kt
  - 行号：23
  - 问题：[详细说明]
  - 建议：[修复建议]

#### 🟡 建议改进（推荐修复）
- [问题描述]
  - 文件：path/to/file.kt
  - 建议：[改进建议]

#### ℹ️ 代码规范提示
- [规范提示]
  - 文件：path/to/file.kt

#### ✅ 做得好的地方
- [值得肯定的地方]

### 静态检查结果
- Lint 错误：X
- Lint 警告：Y
- Detekt 问题：Z（如果可用）

### 总结
[整体评价和改进建议]
```

## 注意事项
- 重点关注生命周期相关的代码
- 注意协程的使用是否正确
- 检查是否有内存泄漏的潜在风险
- 保持代码风格一致性
- 考虑可维护性和可测试性
