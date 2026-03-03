# Claude Code 从 0 到 1 全攻略

> 视频来源：[Bilibili - 马克的技术工作坊](https://www.bilibili.com/video/BV14rzQB9EJj/)
> 视频时长：44分45秒
> 发布时间：2026-01-25

---

## 目录

- [第一部分：环境搭建与基础交互](#第一部分环境搭建与基础交互)
- [第二部分：复杂任务处理与终端控制](#第二部分复杂任务处理与终端控制)
- [第三部分：多模态与上下文管理](#第三部分多模态与上下文管理)
- [第四部分：高级功能扩展与定制](#第四部分高级功能扩展与定制)
- [附录：快速参考](#附录快速参考)

---

## 第一部分：环境搭建与基础交互

### 01:09 - 安装 Claude Code

#### macOS 安装

```bash
# 方法一：使用 npm（推荐）
npm install -g @anthropic-ai/claude-code

# 方法二：使用官方安装脚本
curl -fsSL https://claude.ai/install.sh | sh

# 方法三：使用 Homebrew
brew tap anthropic/claude-code
brew install claude-code
```

#### Windows 安装

```powershell
# 使用 PowerShell
irm https://claude.ai/install.ps1 | iex

# 或使用 winget
winget install anthropic.claude-code
```

#### Linux 安装

```bash
# 使用 npm
npm install -g @anthropic-ai/claude-code

# 或使用 curl
curl -fsSL https://claude.ai/install.sh | sh
```

#### 验证安装

```bash
# 检查版本
claude --version

# 查看帮助
claude --help

# 检查环境
claude doctor
```

---

### 01:43 - 登录与授权

#### 方式一：API Key 授权（推荐）

**步骤 1：获取 API Key**

1. 访问 [Anthropic Console](https://console.anthropic.com/)
2. 登录或注册账号
3. 进入 API Keys 页面
4. 创建新的 API Key
5. 复制保存 Key（只显示一次）

**步骤 2：配置 API Key**

```bash
# 设置环境变量（临时）
export ANTHROPIC_API_KEY="your-api-key-here"

# 添加到 shell 配置（永久）
echo 'export ANTHROPIC_API_KEY="your-api-key-here"' >> ~/.zshrc
source ~/.zshrc
```

或使用配置文件：

```json
// ~/.claude/config.json
{
  "apiKey": "your-api-key-here",
  "defaultModel": "claude-opus-4-6",
  "httpTimeout": 120000
}
```

#### 方式二：浏览器授权

```bash
# 启动授权流程
claude auth login

# 会自动打开浏览器
# 完成登录后自动保存授权信息
```

#### IDE 插件授权

**VSCode：**
1. 安装 "Claude Code" 扩展
2. 点击侧边栏 Claude 图标
3. 选择 "Sign in with Anthropic"

**JetBrains：**
1. 安装 "Claude Code" 插件
2. File > Settings > Tools > Claude Code
3. 点击 "Sign In" 按钮

---

### 02:55 - 第一个实战问题

#### 启动 Claude Code

```bash
# 在项目目录中启动
cd /path/to/project
claude

# 首次启动会显示欢迎信息
# Claude Code v1.x.x
# Ready for your instructions!
```

#### 第一个问题示例

```
你：请帮我分析这个项目的技术栈

Claude：
我来分析这个项目的技术栈...

[Glob 工具：查找配置文件]
找到：package.json, tsconfig.json, vite.config.ts

[Read 工具：读取配置]
分析结果：
- 前端框架：React 18.2.0
- 开发语言：TypeScript 5.0
- 构建工具：Vite 4.3
- 状态管理：Zustand 4.3
- 样式方案：Tailwind CSS 3.3
```

#### 基本对话技巧

1. **明确具体**：描述清楚你的需求
2. **上下文充足**：提供相关背景信息
3. **分步提问**：复杂问题拆解为小步骤
4. **及时反馈**：告诉 Claude 结果如何

---

### 03:12 - 三种模式详解

Claude Code 有三种工作模式，适应不同的使用场景：

#### 1. 默认模式 (Default Mode)

```
特点：
- 平衡响应速度和质量
- 完整的工具访问权限
- 适合日常开发使用

使用方式：
无需特殊设置，默认即是

适用场景：
✅ 日常代码编写
✅ Bug 修复
✅ 代码重构
✅ 功能开发
```

#### 2. 快速模式 (Fast Mode)

```
特点：
- 更快的响应速度
- 简化思考过程
- 可能牺牲一定质量

激活方式：
/fast
或快捷键：Cmd/Ctrl + Shift + F

适用场景：
✅ 快速查询
✅ 简单命令执行
✅ 代码补全
✅ 格式化代码
```

#### 3. 规划模式 (Plan Mode)

```
特点：
- 只分析不修改代码
- 生成详细执行计划
- 识别潜在风险
- 需要用户确认后才能执行

激活方式：
/plan
或连按两次：Shift + Tab

适用场景：
✅ 大型重构项目
✅ 架构设计
✅ 复杂 Bug 修复方案
✅ 技术栈迁移
✅ 性能优化方案
```

#### 规划模式工作流程

```
步骤 1：进入规划模式
你（Shift+Tab×2）：我想重构用户认证模块

步骤 2：Claude 分析规划
Claude：
我来分析这个重构任务...

[探索代码结构]
当前认证模块位于：src/auth/
- AuthService.ts (500行)
- AuthContext.tsx (300行)
- LoginView.tsx (400行)

[识别问题]
1. AuthService 职责过重
2. 状态管理分散
3. 缺少错误处理

[重构方案]
方案 A：拆分服务层
- 创建 ApiClient 基础类
- 拆分 AuthService 为多个服务

方案 B：引入状态管理库
- 使用 Zustand 统一状态管理

推荐：方案 A + B 结合

步骤 3：用户确认
你：使用方案 A，先拆分服务层

步骤 4：执行或继续规划
```

---

## 第二部分：复杂任务处理与终端控制

### 06:00 - 执行终端命令 (Bash)

Claude Code 可以直接执行终端命令，无需离开对话界面。

#### 基本用法

```bash
# 自然语言描述
你：运行开发服务器

Claude 自动执行：
[Bash] npm run dev

> VITE v4.3.0  ready in 234 ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

#### 常用命令示例

```bash
# 项目操作
"安装依赖"              → npm install
"启动开发服务器"        → npm run dev
"构建生产版本"          → npm run build
"运行测试"              → npm test

# Git 操作
"查看当前状态"          → git status
"查看最近的提交"        → git log --oneline -5
"创建新分支"            → git checkout -b feature/new-feature
"提交代码"              → git add . && git commit -m "feat: add new feature"

# 文件操作
"列出所有 TypeScript 文件" → find . -name "*.ts" -o -name "*.tsx"
"搜索包含 useEffect 的文件" → grep -r "useEffect" --include="*.tsx"
"查看文件大小"          → du -sh */*

# Docker 操作
"启动容器"              → docker-compose up -d
"查看运行中的容器"      → docker ps
"停止所有容器"          → docker-compose down
```

#### 命令链式执行

```bash
你：清理构建产物并重新构建

Claude：
[Bash] rm -rf dist && npm run build

删除 dist 目录...
✓ 完成
开始构建...
✓ 构建完成
```

#### 权限确认机制

```
Claude 执行命令前会提示：
[Bash] git push origin main

允许执行此命令？[Y/n]

输入 Y 确认，n 取消
```

---

### 06:49 - 使用规划模式 (Plan Mode)

规划模式是 Claude Code 的重要功能，用于复杂任务的方案设计。

#### 激活方式

```bash
# 方式 1：快捷键
连按两次 Shift + Tab

# 方式 2：命令
/plan

# 方式 3：启动时指定
claude --plan
```

#### 实战案例：重构项目结构

**场景**：现有项目结构混乱，需要重新组织

```
你（进入 Plan Mode）：请帮我重构这个项目的目录结构

Claude 分析：

当前结构分析：
src/
├── components/     # 混杂页面组件和通用组件
├── pages/          # 部分组件混在这里
├── utils/          # 包含业务逻辑
├── api.ts          # 所有 API 调用
└── App.tsx

问题识别：
1. 组件职责不清晰
2. API 调用分散
3. 缺少类型定义集中管理
4. 业务逻辑与 UI 混合

建议的新结构：
src/
├── components/     # 通用组件
│   ├── ui/        # 基础 UI 组件
│   └── business/  # 业务组件
├── pages/         # 页面组件
├── hooks/         # 自定义 Hooks
├── services/      # API 服务层
├── stores/        # 状态管理
├── utils/         # 纯工具函数
├── types/         # TypeScript 类型
└── constants/     # 常量配置

迁移步骤：
1. 创建新的目录结构
2. 迁移 components（优先级：高）
3. 创建 services 层（优先级：高）
4. 提取 hooks（优先级：中）
5. 整理 types（优先级：中）

预计文件改动：约 50 个文件
预计时间：30-60 分钟

是否继续执行此计划？
```

#### 规划模式的输出结构

```
1. 现状分析
   - 代码结构扫描
   - 依赖关系分析
   - 问题点识别

2. 方案设计
   - 目标结构
   - 迁移策略
   - 风险评估

3. 执行计划
   - 详细步骤
   - 文件清单
   - 优先级排序

4. 预期结果
   - 改进效果
   - 注意事项
```

---

### 11:06 - 跳过所有权限检测

对于信任的项目，可以跳过权限确认，提升效率。

#### 配置方式

**命令行参数**：
```bash
claude --dangerously-skip-permissions
```

**配置文件**：
```json
// ~/.claude/config.json
{
  "dangerouslySkipPermissions": true
}
```

**项目级配置**：
```json
// ./.claude/settings.json
{
  "dangerouslySkipPermissions": true
}
```

#### ⚠️ 风险与注意事项

```
风险场景：
1. Claude 可能执行危险命令
   - rm -rf 删除文件
   - git reset --hard 丢弃代码

2. 可能大量消耗 API 配额
   - 批量文件操作
   - 长时间运行的任务

3. 可能产生意外结果
   - 错误的代码修改
   - 不恰当的重构

安全建议：
✅ 仅在受信任的项目使用
✅ 确保代码有 Git 版本控制
✅ 定期检查 Claude 的操作
✅ 重要操作前手动确认
❌ 避免在生产环境使用
```

#### 选择性跳过权限

```json
// ./.claude/settings.json
{
  "permissions": {
    "allowedTools": ["Read", "Edit", "Write"],
    "dangerouslyAllowTools": ["Bash"],
    "allowedBashCommands": ["npm *", "git *", "ls *"],
    "blockedBashCommands": ["rm *", "rm -rf *"]
  }
}
```

---

### 13:53 - 后台任务管理 (Background Tasks)

后台任务允许长时间运行的操作不阻塞对话。

#### 运行后台任务

```bash
你：在后台运行开发服务器

Claude：
[Bash] npm run dev &

[Background Task started]
Task ID: task_abc123
Status: Running

您可以继续输入其他命令...
```

#### 任务管理命令

```bash
# 查看所有后台任务
/tasks

输出：
┌─────────────────────────────────────┐
│ Background Tasks                    │
├──────────────┬──────────┬───────────┤
│ Task ID      │ Status   │ Command   │
├──────────────┼──────────┼───────────┤
│ task_abc123  │ Running  │ npm dev   │
│ task_def456  │ Failed   │ npm test  │
└──────────────┴──────────┴───────────┘

# 查看特定任务输出
/task task_abc123

# 停止任务
/stop task_abc123

# 等待任务完成
/wait task_abc123
```

#### 使用场景

```bash
# 场景 1：开发服务器
"启动后台开发服务器"
→ npm run dev &

# 场景 2：文件监听
"监听文件变化自动编译"
→ npm run build:watch &

# 场景 3：测试服务
"启动测试数据库"
→ docker-compose up -d db &

# 场景 4：定时任务
"每小时运行一次数据备份"
→ (while true; do backup.sh; sleep 3600; done) &
```

#### 后台任务配置

```json
// ./.claude/settings.json
{
  "backgroundTasks": {
    "maxConcurrent": 3,
    "autoRestart": false,
    "logFile": ".claude/tasks.log"
  }
}
```

---

## 第三部分：多模态与上下文管理

### 15:35 - 版本回滚 (Rewind)

Rewind 功能可以回溯会话历史，回到之前的对话状态。

#### 使用 Rewind

```bash
# 方法 1：命令回滚
/rewind

# 方法 2：指定轮数
/rewind 5  # 回滚 5 轮对话

# 方法 3：UI 操作
在对话历史中点击消息旁的 "Rewind" 按钮
```

#### 回滚示例

```
对话历史：
[1] 你：分析这个组件
    Claude：[分析结果]

[2] 你：重构它
    Claude：[执行重构]

[3] 你：添加错误处理
    Claude：[添加错误处理]

[4] 你：添加单元测试
    Claude：[添加测试]

[5] 你：回滚到重构之前
    /rewind 3

→ 回滚到 [2]，[3][4][5] 的操作被撤销
```

#### 应用场景

```
1. 方向偏离
   Claude 理解错误，越走越远
   → Rewind 重新开始

2. 尝试不同方案
   方案 A 不满意，尝试方案 B
   → Rewind 到分支点

3. 误操作恢复
   Claude 执行了不想要的操作
   → Rewind 恢复

4. 对话分支探索
   想看看不同的对话方向
   → Rewind 到某个点重新开始
```

---

### 19:42 - 图片处理

Claude Code 具备强大的图像理解和处理能力。

#### 支持的图片任务

```
📸 截图分析
- UI 界面分析
- 错误信息截图
- 设计稿评审

🎨 设计稿转代码
- Figma 设计稿
- Sketch 设计稿
- 手绘原型图

📊 图表数据提取
- 数据可视化图表
- 统计图形
- 流程图

🖼️ 图片操作
- 尺寸调整建议
- 格式转换建议
- 优化建议
```

#### 使用方式

**方式 1：拖拽图片**
```
1. 截取图片
2. 直接拖到 Claude Code 窗口
3. 提出你的需求
```

**方式 2：指定路径**
```bash
"分析 /path/to/screenshot.png 中的问题"
```

#### 实战案例

**案例 1：设计稿转 React 代码**

```
你：[拖拽 Figma 设计稿截图]
    请根据这个设计稿生成 React + Tailwind CSS 代码

Claude：
我来分析这个设计稿...

[识别元素]
- 导航栏：Logo + 菜单 + 用户头像
- 主内容区：左侧列表 + 右侧详情
- 卡片组件：标题 + 描述 + 操作按钮
- 配色方案：主色 #3B82F6，背景 #F3F4F6

[生成代码]
创建文件：src/components/Dashboard.tsx

import React from 'react';

export default function Dashboard() {
  return (
    <div className="min-h-screen bg-gray-100">
      {/* 导航栏 */}
      <nav className="bg-white shadow-sm">
        {/* ... */}
      </nav>

      {/* 主内容 */}
      <main className="container mx-auto p-6">
        {/* ... */}
      </main>
    </div>
  );
}
```

**案例 2：错误截图诊断**

```
你：[拖拽错误截图]
    这个错误是什么原因？

Claude：
[分析错误信息]
错误类型：TypeError
错误消息：Cannot read property 'map' of undefined
错误位置：UserList.tsx:23

[原因分析]
data 可能为 undefined 时调用了 .map()
需要添加可选链或默认值

[修复建议]
const items = data?.users ?? []
{items.map(user => ...)}
```

**案例 3：图表数据提取**

```
你：[拖拽柱状图]
    请提取这个图表中的数据

Claude：
[识别图表数据]
X轴（月份）：1月, 2月, 3月, 4月, 5月, 6月
Y轴（销售额）：120, 150, 180, 220, 200, 280

[数据格式]
const chartData = {
  labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
  values: [120, 150, 180, 220, 200, 280]
};

[趋势分析]
整体呈上升趋势，6月达到峰值 280
相比 1月增长 133%
```

---

### 21:09 - 安装 MCP Server

MCP (Model Context Protocol) 是 Claude Code 的扩展协议。

#### MCP 简介

```
MCP 允许 Claude 连接外部服务：
- Figma：设计稿协作
- GitHub：代码仓库管理
- 数据库：直接查询数据
- 搜索引擎：获取实时信息
- 自定义服务：扩展 Claude 能力
```

#### 以 Figma MCP Server 为例

**步骤 1：安装 Figma MCP**

```bash
# 全局安装
npm install -g @figma/mcp-server

# 或使用 npx（无需安装）
npx @figma/mcp-server
```

**步骤 2：获取 Figma Token**

```
1. 登录 Figma
2. 点击头像 > Settings
3. 进入 Personal Access Tokens
4. 点击 "Generate new token"
5. 复制 token（只显示一次）
```

**步骤 3：配置 MCP**

```json
// ~/.claude/settings.json
{
  "mcpServers": {
    "figma": {
      "command": "node",
      "args": ["/path/to/figma-mcp-server/index.js"],
      "env": {
        "FIGMA_ACCESS_TOKEN": "your-figma-token"
      }
    }
  }
}
```

或使用 npx：
```json
{
  "mcpServers": {
    "figma": {
      "command": "npx",
      "args": ["-y", "@figma/mcp-server"],
      "env": {
        "FIGMA_ACCESS_TOKEN": "your-figma-token"
      }
    }
  }
}
```

**步骤 4：重启 Claude Code**

```bash
# 退出并重启
claude

# 验证 MCP 加载
你：列出可用的 MCP 工具

Claude：
可用的 MCP 工具：
- figma_read_file: 读取 Figma 文件
- figma_get_components: 获取组件列表
- figma_get_styles: 获取样式信息
```

#### 使用 Figma MCP

```bash
# 读取 Figma 文件
你：获取这个 Figma 文件的信息：
    https://www.figma.com/file/xxx/My-Design

Claude：
[Figma MCP] 正在读取文件...

文件信息：
- 名称：My Design
- 页面数：3
- 组件数：24
- 主要画板：Desktop, Mobile, Tablet

# 生成代码
你：根据 Desktop 画板生成 Flutter 代码

Claude：
[Figma MCP] 读取画板信息...
[生成 Flutter 代码...]

创建文件：lib/pages/main_page.dart
```

#### 其他常用 MCP Server

| MCP Server | 用途 | 安装 |
|------------|------|------|
| GitHub | 仓库管理、PR 操作 | `npx @modelcontextprotocol/server-github` |
| Filesystem | 增强文件操作 | 内置 |
| Brave Search | 网页搜索 | `npx @modelcontextprotocol/server-brave-search` |
| PostgreSQL | 数据库查询 | `npx @modelcontextprotocol/server-postgres` |
| SQLite | 本地数据库 | `npx @modelcontextprotocol/server-sqlite` |

---

### 21:39 - 恢复历史会话 (Resume)

Resume 功能可以恢复之前的对话会话。

#### 恢复会话

```bash
# 启动时选择
claude

显示会话列表：
┌────────────────────────────────────┐
│ Recent Sessions                    │
├──────────────┬─────────┬───────────┤
│ 项目         │ 时间    │ 消息数    │
├──────────────┼─────────┼───────────┤
│ jetpack-kobe │ 2小时前 │ 156       │
│ my-app       │ 昨天    │ 89        │
│ another-proj │ 3天前   │ 234       │
└──────────────┴─────────┴───────────┘

选择要恢复的会话...

# 或使用命令
/resume

# 或指定会话
/resume <session-id>
```

#### 会话持久化

```
自动保存的内容：
✅ 对话历史
✅ 项目上下文
✅ 任务状态
✅ 文件变更记录

不保存的内容：
❌ 敏感数据（API Key 等）
❌ 临时文件内容
```

#### 使用场景

```
1. 意外关闭恢复
   系统崩溃或意外关闭
   → Resume 恢复工作状态

2. 多项目切换
   同时开发多个项目
   → Resume 快速切换上下文

3. 长时间任务
   跨多天的复杂任务
   → Resume 继续工作
```

---

### 23:06 - 使用 MCP 工具还原设计稿

结合 MCP Server，可以直接从 Figma 获取设计信息并生成代码。

#### 完整流程

**步骤 1：分享 Figma 文件**

```
1. 在 Figma 中打开设计稿
2. 点击 "Share" 按钮
3. 设置访问权限为 "Anyone with the link can view"
4. 复制链接
```

**步骤 2：让 Claude 分析**

```bash
你：请分析这个 Figma 设计稿：
    https://www.figma.com/file/xxx/User-Dashboard

Claude：
[Figma MCP] 正在连接...
[Figma MCP] 读取文件信息...

设计稿分析：
文件名：User Dashboard
页面数：1
画板数量：3

画板 "Desktop"：
- 尺寸：1440 × 900
- 背景：#FFFFFF
- 组件：12 个
  - NavigationBar (顶部导航)
  - SideMenu (侧边菜单)
  - UserCard (用户卡片) × 4
  - StatsCard (统计卡片) × 3
  - ChartArea (图表区域)

配色方案：
- 主色：#6366F1 (Indigo-500)
- 次色：#8B5CF6 (Violet-500)
- 背景：#F8FAFC (Slate-50)
- 文字：#1E293B (Slate-800)

字体样式：
- 标题：Inter 24px Semibold
- 正文：Inter 14px Regular
- 说明：Inter 12px Regular
```

**步骤 3：生成代码**

```bash
你：请根据这个设计生成 React + Tailwind CSS 代码

Claude：
正在生成代码...

[创建文件]
✓ src/components/Dashboard.tsx
✓ src/components/NavigationBar.tsx
✓ src/components/SideMenu.tsx
✓ src/components/UserCard.tsx
✓ src/components/StatsCard.tsx
✓ src/styles/tailwind.config.js

[提取样式]
✓ 添加自定义颜色到 Tailwind 配置

组件已创建，是否需要预览？
```

**步骤 4：迭代优化**

```bash
你：请添加 hover 效果和移动端响应式

Claude：
正在优化组件...

[修改 UserCard]
✓ 添加 hover:scale-105 效果
✓ 添加 transition-transform

[修改布局]
✓ 添加 md:grid-cols-2
✓ 添加 lg:grid-cols-4

优化完成！
```

---

### 24:49 - 上下文压缩与清除

Claude Code 会自动管理上下文，但也可以手动控制。

#### 查看上下文状态

```bash
/context

输出：
当前会话信息：
- 消息数：47
- Token 使用：24,580 / 200,000
- 预计剩余：约 150 轮对话
- 压缩状态：已自动压缩 15%

主要上下文来源：
- 项目文件：23 个
- 工具调用：34 次
- 对话历史：47 轮
```

#### 上下文管理命令

```bash
# 清除当前会话
/clear

清除所有对话历史，但保留项目上下文

# 重置会话
/reset

完全重置，清除所有状态

# 查看统计
/stats

显示详细的 token 使用统计
```

#### 优化 Token 使用

```
技巧 1：使用 CLAUDE.md
→ 将项目信息写入文件，避免重复解释

技巧 2：定期清理
→ 长时间会话后使用 /clear

技巧 3：聚焦问题
→ 明确具体的提问，减少来回

技巧 4：批量处理
→ 将多个小问题合并为一个请求

技巧 5：使用规划模式
→ 先规划再执行，减少试错
```

#### Token 计费

```
Claude Opus 4.6：
- 输入：$3 / 百万 tokens
- 输出：$15 / 百万 tokens

Claude Sonnet 4.6：
- 输入：$0.30 / 百万 tokens
- 输出：$1.50 / 百万 tokens

预估：
- 日常开发：每天约 $0.5-2
- 重度使用：每天约 $5-10
```

---

### 26:30 - 项目记忆文件 (CLAUDE.md)

CLAUDE.md 是项目的"长期记忆"，让 Claude 记住项目规范。

#### 文件位置与优先级

```
优先级（从高到低）：

1. 企业策略
   /Library/Application Support/ClaudeCode/CLAUDE.md

2. 项目记忆（推荐）
   ./CLAUDE.md
   ./.claude/CLAUDE.md

3. 子目录配置
   ./src/CLAUDE.md
   ./components/CLAUDE.md

4. 个人全局配置
   ~/.claude/CLAUDE.md

5. 本地配置（不提交 Git）
   ./CLAUDE.local.md
```

#### CLAUDE.md 完整模板

```markdown
# [项目名称]

## 项目概述
[项目描述：用途、目标用户、核心功能]

本项目是 [简短描述]，主要解决 [问题]。

## 技术栈

### 前端
- **框架**：React 18.2.0
- **语言**：TypeScript 5.0
- **构建**：Vite 4.3
- **样式**：Tailwind CSS 3.3
- **状态**：Zustand 4.3
- **路由**：React Router 6.10

### 后端
- **框架**：Node.js + Express 4.18
- **数据库**：PostgreSQL 14
- **ORM**：Prisma 4.15
- **认证**：JWT + bcrypt

### 开发工具
- **包管理**：pnpm 8.6
- **代码规范**：ESLint + Prettier
- **提交规范**：Commitlint
- **测试**：Vitest + Testing Library

## 项目结构

```
项目根目录/
├── src/
│   ├── components/     # 通用组件
│   │   ├── ui/        # 基础 UI 组件
│   │   └── business/  # 业务组件
│   ├── pages/         # 页面组件
│   ├── hooks/         # 自定义 Hooks
│   ├── services/      # API 服务层
│   ├── stores/        # Zustand 状态
│   ├── utils/         # 工具函数
│   ├── types/         # TypeScript 类型
│   ├── constants/     # 常量定义
│   └── styles/        # 全局样式
├── public/            # 静态资源
├── tests/             # 测试文件
└── docs/              # 文档
```

## 代码规范

### 命名规范
- **组件**：PascalCase (如 `UserProfile.tsx`)
- **函数**：camelCase (如 `getUserData`)
- **常量**：UPPER_SNAKE_CASE (如 `API_BASE_URL`)
- **类型/接口**：PascalCase，以 I 开头 (如 `IUserData`)

### 文件组织
- 一个文件一个组件
- 组件文件包含：类型定义、组件、样式（如需要）
- 相关文件放在同一目录

### 代码风格
- **禁止使用 console.log**，使用 `logger.info()`
- 使用 TypeScript 严格模式
- 优先使用函数式组件 + Hooks
- 样式优先使用 Tailwind CSS 类名

### 注释规范
```typescript
/**
 * 函数功能描述
 * @param param1 - 参数1说明
 * @returns 返回值说明
 */
function example(param1: string): boolean {
  // 实现代码
}
```

## 常用命令

### 开发
```bash
pnpm dev              # 启动开发服务器
pnpm dev:mock         # 启动带 mock 数据的开发服务器
pnpm dev:debug        # 启动调试模式
```

### 构建
```bash
pnpm build            # 生产构建
pnpm build:analyze    # 构建并分析包大小
pnpm build:preview    # 构建并预览
```

### 测试
```bash
pnpm test             # 运行测试
pnpm test:watch       # 监听模式
pnpm test:coverage    # 生成覆盖率报告
pnpm test:e2e         # E2E 测试
```

### 代码质量
```bash
pnpm lint             # ESLint 检查
pnpm lint:fix         # 自动修复
pnpm format           # Prettier 格式化
pnpm type-check       # TypeScript 类型检查
```

### Git
```bash
pnpm commit           # 交互式提交（遵循规范）
pnpm changelog        # 生成变更日志
```

## 特殊说明

### 环境变量
所有环境变量定义在 `.env.local`，参考 `.env.example`

### API 配置
- 基础 URL：从环境变量读取
- 请求拦截器：自动添加认证 Token
- 响应拦截器：统一错误处理

### 状态管理
- 全局状态使用 Zustand
- 组件本地状态使用 useState
- 服务端状态使用 React Query

### Git 工作流
1. 从 main 创建功能分支
2. 开发并提交代码
3. 创建 PR 到 main
4. Code Review 通过后合并

### 分支命名
- `feature/xxx` - 新功能
- `fix/xxx` - Bug 修复
- `refactor/xxx` - 重构
- `docs/xxx` - 文档

## ⚠️ 重要规则

**禁止操作：**
- ❌ 禁止直接修改 `src/types/api.d.ts`（自动生成）
- ❌ 禁止使用 `any` 类型
- ❌ 禁止在组件中直接调用 API（使用 services 层）
- ❌ 禁止将敏感信息提交到 Git

**必须遵守：**
- ✅ 所有 API 变更必须更新类型定义
- ✅ 新功能必须包含单元测试
- ✅ 提交前必须通过 lint 和 type-check
- ✅ 敏感配置使用环境变量

## 已知问题

1. 某些第三方组件类型定义不完整
   - 解决：使用 `// @ts-ignore` 临时跳过

2. 热更新在某些情况下不生效
   - 解决：重启开发服务器

## 相关链接

- 设计稿：[Figma 链接]
- API 文档：[文档链接]
- Wiki：[内部 Wiki]
```

#### CLAUDE.local.md（敏感信息）

```markdown
# 本地配置

## 环境变量
```bash
API_BASE_URL=http://localhost:3000
API_KEY=dev-secret-key
```

## 本地开发说明
- 数据库端口：5433
- Mock 数据：`mocks/` 目录
```

---

## 第四部分：高级功能扩展与定制

### 29:46 - Hook

Hook 是事件触发机制，在特定操作时自动执行自定义逻辑。

#### Hook 类型

| Hook 类型 | 触发时机 | 常见用途 |
|-----------|----------|----------|
| `PreToolUse` | 工具调用前 | 日志记录、权限控制 |
| `PostToolUseFailure` | 工具失败后 | 错误通知、自动重试 |
| `SessionEnd` | 会话结束时 | 清理、报告生成 |
| `PermissionRequest` | 权限请求时 | 自动授权/拒绝 |
| `SubagentStart` | 子代理启动时 | 初始化环境 |

#### 配置步骤

**步骤 1：打开 Hook 配置**

```bash
/hooks

选择：PreToolUse
```

**步骤 2：添加匹配器**

```
选择：+ Add new matcher...
输入：Bash  # 匹配所有 Bash 命令
```

**步骤 3：添加 Hook 命令**

```
选择：+ Add new hook...
输入：echo "$(date): $(COMMAND)" >> .claude/commands.log
```

**步骤 4：选择存储位置**

```
选择：Project settings  # 项目级配置
```

#### Hook 配置示例

**日志记录 Hook**：

```json
// ./.claude/settings.json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "echo \"[$(date)] ${COMMAND}\" >> .claude/bash-history.log"
          }
        ]
      },
      {
        "matcher": "Edit",
        "hooks": [
          {
            "type": "command",
            "command": "npx eslint ${FILE} --fix"
          }
        ]
      }
    ]
  }
}
```

**安全防护 Hook**：

```json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "prompt",
            "prompt": "⚠️ 确认执行删除操作？",
            "condition": "${COMMAND} matches rm.*"
          }
        ]
      }
    ]
  }
}
```

**自动测试 Hook**：

```json
{
  "hooks": {
    "PostToolUseFailure": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "notify-send 'Claude Code' '命令失败: ${COMMAND}'"
          }
        ]
      }
    ]
  }
}
```

#### Hook 条件语法

```json
{
  "condition": "${COMMAND} matches .*\\.test\\.js"  // 正则匹配
  "condition": "${FILE} endsWith .tsx"             // 后缀匹配
  "condition": "${RESULT} contains error"          // 包含匹配
  "condition": "${USER} == admin"                  // 值匹配
}
```

---

### 33:16 - Agent Skill

Skill 是教 Claude 执行特定任务的"技能包"。

#### 创建 Skill

**方式 1：使用 Skill Creator**

```bash
/skill create

Claude 会引导你：
1. 技能的用途是什么？
2. 需要哪些工具？
3. 什么情况下使用这个技能？

自动生成技能结构
```

**方式 2：手动创建**

```
skills/
└── pr-review/
    ├── SKILL.md       # 技能定义
    ├── schema.json    # 参数定义（可选）
    └── examples/      # 示例（可选）
```

#### SKILL.md 结构

```markdown
# 技能名称

## 描述
[这个技能能做什么]

## 何时使用
- 场景 1：[描述]
- 场景 2：[描述]

## 工具需求
- Read: 读取文件
- Edit: 编辑文件
- Bash: 执行命令

## 执行步骤
1. [第一步]
2. [第二步]
3. [第三步]

## 注意事项
- [注意点 1]
- [注意点 2]

## 示例

**输入**：
```
[用户请求示例]
```

**输出**：
```
[期望的执行结果]
```
```

#### 实战 Skill：代码审查

```markdown
# Code Review 技能

## 描述
自动审查 Pull Request 中的代码变更，检查代码质量、规范合规性和潜在问题。

## 何时使用
- 创建 Pull Request 后
- 提交代码前自检
- 代码质量检查

## 工具需求
- Read: 读取修改的文件
- Bash: 运行 linter 和测试
- Grep: 搜索特定模式

## 执行步骤

1. 获取变更文件列表
   ```bash
   git diff --name-only main...HEAD
   ```

2. 检查每个文件
   - 代码风格是否符合 ESLint 规则
   - 是否有 console.log 调试代码
   - 是否有敏感信息（API Key、密码等）
   - 是否有明显的 bug 或安全问题

3. 运行检查
   ```bash
   npm run lint
   npm run type-check
   npm test -- --changed
   ```

4. 生成审查报告

## 检查清单

### 代码质量
- [ ] 代码风格符合规范
- [ ] 变量命名清晰
- [ ] 函数职责单一
- [ ] 逻辑清晰易懂

### 安全检查
- [ ] 无 SQL 注入风险
- [ ] 无 XSS 风险
- [ ] 无敏感信息泄露
- [ ] 输入验证完整

### 测试覆盖
- [ ] 新功能有单元测试
- [ ] 测试覆盖率达到要求
- [ ] 边界情况已测试

### 文档
- [ ] 复杂逻辑有注释
- [ ] API 变更更新文档
- [ ] README 已更新（如需要）

## 输出格式

```markdown
## 代码审查报告

### 变更概览
- 文件数：X
- 新增行数：Y
- 删除行数：Z

### 发现的问题

#### 🔴 严重问题（必须修复）
- [问题描述]
  - 文件：src/example.ts
  - 行号：23
  - 建议：[修复建议]

#### 🟡 建议改进（推荐修复）
- [问题描述]
  - 文件：src/example.ts
  - 行号：45
  - 建议：[改进建议]

#### ✅ 做得好的地方
- [值得肯定的地方]

### 统计信息
- ESLint 错误：X
- TypeScript 错误：Y
- 测试通过率：Z%

### 总结
[整体评价和建议]
```

## 注意事项
- 重点关注安全相关代码
- 新代码需要完整测试
- 保持代码风格一致性
```

#### 使用 Skill

```bash
# 方式 1：命令调用
/skill pr-review

# 方式 2：自然语言触发
"请帮我审查当前的代码变更"

# Claude 会：
# 1. 识别需要使用 pr-review 技能
# 2. 按照 SKILL.md 执行
# 3. 生成审查报告
```

---

### 36:00 - SubAgent

SubAgent 是专业的"子代理"，各自专注于特定领域。

#### 内置 SubAgent

| SubAgent | 专长 | 使用场景 |
|----------|------|----------|
| `Explore` | 代码库探索 | 理解项目结构、查找文件、分析依赖 |
| `Plan` | 方案规划 | 架构设计、重构方案制定 |
| `general-purpose` | 通用任务 | 复杂多步骤任务 |

#### 使用内置 SubAgent

```bash
你：使用 Explore agent 分析这个项目的状态管理实现

Claude：
正在启动 Explore SubAgent...

[Explore SubAgent]
正在探索代码库...
找到状态管理相关文件：
- src/stores/userStore.ts
- src/stores/cartStore.ts
- src/stores/productStore.ts
- hooks/useStore.ts

分析完成：
使用 Zustand 4.3 实现状态管理...
[详细分析结果]

返回主会话，整合结果...
```

#### 创建自定义 SubAgent

**配置位置**：`./.claude/settings.json`

```json
{
  "subagents": {
    "security-expert": {
      "description": "专注于代码安全审查的专家",
      "tools": ["Read", "Grep", "Bash"],
      "systemPrompt": "你是一位资深安全专家。你的任务是审查代码中的安全问题，包括但不限于：\n\n1. 注入漏洞（SQL、NoSQL、命令注入）\n2. XSS 和 CSRF 攻击\n3. 敏感信息泄露\n4. 不安全的加密实现\n5. 权限控制问题\n\n请给出具体的安全建议和修复方案。",
      "permissions": {
        "dangerouslySkipPermissions": false,
        "allowedTools": ["Read", "Grep", "Bash"]
      }
    },
    "test-engineer": {
      "description": "专注于编写测试用例的工程师",
      "tools": ["Read", "Write", "Edit", "Bash"],
      "systemPrompt": "你是一位测试工程师专家。你的任务是为代码编写完整的测试用例，包括：\n\n1. 单元测试：测试独立函数和组件\n2. 集成测试：测试模块间交互\n3. 边界测试：测试极限情况\n4. 错误处理：测试异常流程\n\n请使用 Vitest 和 Testing Library。",
      "permissions": {
        "allowedTools": ["Read", "Write", "Edit", "Bash"]
      }
    },
    "performance-analyst": {
      "description": "专注于性能分析和优化的专家",
      "tools": ["Read", "Bash"],
      "systemPrompt": "你是一位性能优化专家。你的任务是分析代码性能问题并给出优化建议，包括：\n\n1. 算法复杂度分析\n2. 内存使用分析\n3. 渲染性能优化\n4. 网络请求优化\n5. 构建体积优化\n\n请给出具体的优化方案和预期效果。",
      "permissions": {
        "allowedTools": ["Read", "Bash"]
      }
    },
    "api-specialist": {
      "description": "专注于 API 设计和文档的专家",
      "tools": ["Read", "Write", "Edit"],
      "systemPrompt": "你是一位 API 设计专家。你的任务是：\n\n1. 设计 RESTful API 接口\n2. 定义清晰的请求/响应结构\n3. 编写 API 文档\n4. 确保 API 的一致性和规范性\n\n请使用 OpenAPI 规范。",
      "permissions": {
        "allowedTools": ["Read", "Write", "Edit"]
      }
    }
  }
}
```

#### 使用自定义 SubAgent

```bash
# 安全审查
你：请用 security-expert 审查 AuthService.ts

[Security-Expert SubAgent]
正在分析 AuthService.ts...

发现安全问题：
1. 密码使用 MD5 加密（不安全）
   建议：使用 bcrypt
2. SQL 查询使用字符串拼接
   建议：使用参数化查询
3. JWT Secret 硬编码
   建议：使用环境变量

# 测试编写
你：请用 test-engineer 为 UserService 编写测试

[Test-Engineer SubAgent]
正在创建测试文件...

创建文件：src/services/__tests__/UserService.test.ts
测试用例：
- getUserById: 正常情况
- getUserById: 不存在的用户
- createUser: 成功创建
- createUser: 重复邮箱
- updateUser: 正常更新
- deleteUser: 成功删除
```

#### SubAgent 最佳实践

```
1. 职责单一
   每个SubAgent只做一件事
   → security-expert 只做安全审查
   → test-engineer 只写测试

2. 工具精简
   只给必要的工具
   → security-expert 不需要 Write 工具

3. 描述清晰
   systemPrompt 要明确角色和任务
   → "你是一位资深安全专家..."

4. 合理授权
   根据需要配置权限
   → 敏感操作不跳过权限
```

---

### 39:17 - Skill 与 SubAgent 的区别

理解 Skill 和 SubAgent 的区别，有助于选择合适的工具。

#### 核心区别

| 维度 | Skill | SubAgent |
|------|-------|----------|
| **本质** | 任务流程模板 | 独立的 AI 助手 |
| **上下文** | 共享主会话 | 独立上下文空间 |
| **记忆** | 无独立记忆 | 有独立对话历史 |
| **工具配置** | 继承主 Agent | 可自定义工具集 |
| **权限** | 继承主 Agent | 可独立配置 |
| **复杂度** | 简单任务脚本 | 复杂决策代理 |
| **配置方式** | SKILL.md (Markdown) | settings.json |
| **创建难度** | 低（写文档） | 中（写 JSON 配置） |
| **灵活性** | 固定流程 | 智能决策 |
| **成本** | 低（无额外上下文）| 中（独立上下文） |

#### 形象比喻

```
Skill 就像"菜谱"：
- 步骤固定
- 照着做就行
- 适合重复性任务

SubAgent 就像"专业顾问"：
- 可以思考决策
- 根据情况调整
- 适合专业领域问题
```

#### 选择决策树

```
需要自动化某个任务？
├─ 是 → 是否需要智能决策？
│   ├─ 否 → 使用 Skill
│   └─ 是 → 是否需要专业领域知识？
│       ├─ 是 → 使用 SubAgent
│       └─ 否 → 使用主 Agent
└─ 否 → 直接对话
```

#### 组合使用示例

```
场景：代码提交前检查

主 Agent（协调者）：
┌─────────────────────────────────┐
│ 1. [Skill: GitStatus]           │ → 获取变更文件
│ 2. [SubAgent: SecurityExpert]   │ → 安全审查
│ 3. [SubAgent: TestEngineer]     │ → 运行测试
│ 4. [Skill: CommitMessage]       │ → 生成提交信息
└─────────────────────────────────┘

各司其职，协同工作
```

#### 实战对比

**任务：审查并修复代码安全问题**

**使用 Skill**：
```markdown
# SKILL.md
1. 扫描文件
2. 检查安全问题
3. 生成报告
4. 提供修复建议
```
→ 固定流程，适合标准化检查

**使用 SubAgent**：
```json
{
  "systemPrompt": "你是一位安全专家，分析代码安全问题并提供修复方案..."
}
```
→ 智能分析，适合复杂安全评估

---

### 40:38 - Plugin

Plugin 为 Claude Code 提供扩展能力。

#### IDE 插件

**VSCode 插件**

```bash
# 安装
code --install-extension anthropic.claude-code

# 或在 VSCode 中
1. 按 Ctrl+Shift+X 打开扩展
2. 搜索 "Claude Code"
3. 点击安装
```

**JetBrains 插件**

```
1. File > Settings > Plugins
2. 搜索 "Claude Code"
3. 安装并重启
```

#### 插件功能对比

| 功能 | CLI | VSCode | JetBrains |
|------|-----|--------|----------|
| 对话交互 | ✅ | ✅ | ✅ |
| 文件编辑 | ✅ | ✅ | ✅ |
| 代码预览 | ❌ | ✅ | ✅ |
| 侧边栏 | ❌ | ✅ | ✅ |
| 快捷键 | ✅ | ✅ | ✅ |
| 多文件编辑 | ✅ | ✅ | ✅ |

#### 插件使用建议

```
VSCode 插件适合：
✅ 前端开发
✅ 实时预览需求
✅ 喜欢可视化界面

JetBrains 插件适合：
✅ Java/Kotlin 开发
✅ 企业级项目
✅ 重度 IDE 用户

CLI 适合：
✅ 服务器操作
✅ 脚本自动化
✅ 极简主义者
```

---

## 附录：快速参考

### 快捷键速查

| 快捷键 | 功能 |
|--------|------|
| `Shift + Tab` × 2 | 进入规划模式 |
| `Cmd/Ctrl + Shift + F` | 切换快速模式 |
| `Cmd/Ctrl + C` | 中止当前操作 |
| `Cmd/Ctrl + D` | 退出 Claude Code |

### 命令速查

| 命令 | 功能 |
|------|------|
| `/plan` | 进入规划模式 |
| `/fast` | 切换快速模式 |
| `/clear` | 清除对话历史 |
| `/reset` | 重置会话 |
| `/rewind [n]` | 回滚 n 轮对话 |
| `/resume` | 恢复历史会话 |
| `/tasks` | 查看后台任务 |
| `/hooks` | 配置 Hook |
| `/skills` | 列出可用 Skill |
| `/context` | 查看上下文状态 |
| `/stats` | 查看统计信息 |

### 配置文件位置

```
~/.claude/
├── config.json           # 全局配置
├── CLAUDE.md             # 个人全局记忆
└── settings.json         # 全局设置

./.claude/
├── CLAUDE.md             # 项目记忆
├── CLAUDE.local.md       # 本地配置（不提交）
├── settings.json         # 项目设置
├── skills/               # 自定义 Skill
│   └── skill-name/
│       └── SKILL.md
└── hooks/                # Hook 日志
```

### 学习资源

- **官方文档**：https://docs.anthropic.com/claude-code
- **MCP 协议**：https://modelcontextprotocol.io
- **示例项目**：https://github.com/anthropics/claude-code-examples
- **问题反馈**：https://github.com/anthropics/claude-code/issues

---

**文档版本**：v3.0
**更新时间**：2026-03-02
**基于视频**：[Bilibili - 马克的技术工作坊](https://www.bilibili.com/video/BV14rzQB9EJj/)
