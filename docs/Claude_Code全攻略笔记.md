# Claude Code 从 0 到 1 全攻略学习笔记

> 视频来源：[Bilibili - Claude Code 从 0 到 1 全攻略](https://www.bilibili.com/video/BV14rzQB9EJj/)
> 视频标题：Claude Code 从 0 到 1 全攻略：MCP / SubAgent / Agent Skill / Hook / 图片 / 上下文处理/ 后台任务

---

## 一、环境搭建与基础交互

### 1.1 安装 Claude Code
**时间点**：01:09

#### macOS / Linux 安装
```bash
# 使用 npm 安装
npm install -g @anthropic-ai/claude-code

# 或使用官方安装脚本
curl -fsSL https://claude.ai/install.sh | sh
```

#### Windows 安装
```powershell
# 使用 PowerShell 安装
irm https://claude.ai/install.ps1 | iex
```

#### 安装前检查
```bash
# 检查 Node.js 环境（需要 Node.js 18+）
npm list -g --depth=0

# 如果没有 Node.js，先安装
# macOS: brew install node
# Windows: 访问 nodejs.org 下载安装
```

#### 验证安装
```bash
claude --version
claude --help
```

---

### 1.2 登录与授权
**时间点**：01:43

#### 方式一：API Key 登录（推荐）
```bash
# 1. 访问 https://console.anthropic.com 获取 API Key
# 2. 设置环境变量
export ANTHROPIC_API_KEY="your-api-key-here"

# 3. 或在配置文件中保存
# macOS/Linux: ~/.claude/config.json
# Windows: %APPDATA%\ClaudeCode\config.json
```

配置文件格式：
```json
{
  "apiKey": "your-api-key-here",
  "defaultModel": "claude-opus-4-6"
}
```

#### 方式二：浏览器授权
```bash
# 启动授权流程
claude auth login

# 会自动打开浏览器，完成 Anthropic 账号登录
# 登录成功后自动保存授权信息
```

#### IDE 集成授权（VSCode / JetBrains）
1. 安装 Claude Code 插件
2. 点击插件图标进行授权
3. 首次使用需登录 Anthropic 账号

---

### 1.3 第一个实战问题
**时间点**：02:55

#### 启动 Claude Code
```bash
# 在项目目录中启动
cd /path/to/your/project
claude

# 或直接用单次命令模式
claude "帮我检查这个项目的代码结构"
```

#### 第一个问题示例
```
你：请帮我分析这个项目的技术栈

Claude 会：
1. 读取项目结构
2. 分析 package.json / build.gradle 等配置文件
3. 识别使用的技术和框架
4. 给出技术栈报告
```

---

### 1.4 三种模式详解
**时间点**：03:12

#### 1. 默认模式 (Default Mode)
- **描述**：标准交互模式，平衡响应速度和质量
- **适用场景**：日常开发、问题解答、代码修改
- **激活方式**：默认模式，无需特殊操作

#### 2. 自动模式 (Fast Mode)
- **描述**：快速响应模式，减少思考时间
- **适用场景**：简单查询、快速命令执行
- **激活方式**：使用 `/fast` 命令或快捷键

#### 3. 规划模式 (Plan Mode)
- **描述**：只规划不执行，安全探索方案
- **适用场景**：复杂重构、架构设计、需要仔细分析的任务
- **激活方式**：
  ```bash
  # 方法1：双击快捷键
  连续按两次 Shift + Tab

  # 方法2：命令激活
  /plan

  # 方法3：启动时指定
  claude --plan
  ```

**规划模式特性**：
- ✅ 只分析不修改
- ✅ 生成详细执行计划
- ✅ 识别潜在风险
- ❌ 不会编辑文件
- ❌ 不会执行命令

---

## 二、复杂任务处理与终端控制

### 2.1 执行终端命令 (Bash)
**时间点**：06:00

#### 基本用法
Claude Code 会自动调用 Bash 工具执行命令：

```bash
# 你可以直接要求
"请运行 npm install 安装依赖"

# Claude 会自动执行
Bash: npm install
```

#### 常用命令示例
```bash
# 项目操作
"运行项目" -> npm run dev
"构建项目" -> npm run build
"运行测试" -> npm test

# Git 操作
"查看当前分支" -> git branch
"提交代码" -> git add . && git commit -m "xxx"

# 文件操作
"列出所有 .kt 文件" -> find . -name "*.kt"
"搜索包含 'MainActivity' 的文件" -> grep -r "MainActivity"
```

#### 命令执行最佳实践
1. **明确具体**：描述清楚要执行的具体命令
2. **逐步执行**：复杂任务分步骤执行
3. **检查结果**：每步执行后检查输出
4. **错误处理**：遇到错误时让 Claude 分析原因

---

### 2.2 使用规划模式 (Plan Mode)
**时间点**：06:49

#### 激活方式
```bash
# 连续按两次 Shift + Tab
# 或使用 /plan 命令
```

#### 规划模式工作流程

**步骤1：进入规划模式**
```bash
你（进入 Plan Mode）：我需要重构用户登录模块
```

**步骤2：Claude 分析规划**
```
Claude 会输出：
1. 分析现有登录代码结构
2. 识别问题和改进点
3. 提出重构方案
4. 列出需要修改的文件
5. 评估潜在风险
6. 给出实施步骤
```

**步骤3：审核计划**
- 检查计划是否合理
- 提出修改建议
- 确认方案

**步骤4：执行或退出**
- 确认后退出规划模式开始执行
- 或继续调整计划

#### 适用场景
- 🔧 大型重构项目
- 🏗️ 新功能架构设计
- 🐛 复杂 bug 修复方案
- 📦 依赖升级评估
- 🔄 技术栈迁移

---

### 2.3 跳过权限检测
**时间点**：11:06

#### dangerously-skip-permissions 参数

**用途**：跳过所有工具调用的权限确认，自动执行

**使用方式**：
```bash
# 启动时添加参数
claude --dangerously-skip-permissions

# 或在配置中设置
# ~/.claude/config.json
{
  "dangerouslySkipPermissions": true
}
```

#### ⚠️ 风险提示

**潜在风险**：
- Claude 可能执行危险命令（如 rm -rf）
- 可能修改重要文件
- 可能消耗大量 API 额度

**安全建议**：
1. 仅在受信任的项目中使用
2. 使用 Git 版本控制，方便回滚
3. 定期检查 Claude 的操作历史
4. 生产环境谨慎使用

#### 适用场景
- ✅ 个人学习项目
- ✅ 快速原型开发
- ✅ 信任 Claude 的操作
- ❌ 生产环境
- ❌ 重要业务项目

---

### 2.4 后台任务管理 (Background Tasks)
**时间点**：13:53

#### 运行后台任务

**方式1：后台命令执行**
```bash
# 在命令后添加 &
"npm run dev &"  # 后台运行开发服务器

# 或使用 nohup
"nohup npm start &"
```

**方式2：使用 run_in_background 参数**
```bash
# Claude Code 内置支持
"在后台运行测试并持续监控"
# Claude 会使用 run_in_background=true 参数
```

#### 任务状态监控

**查看后台任务**：
```bash
# 使用 /tasks 命令查看运行中的任务
/tasks

# 查看特定任务输出
/task <task-id>
```

**任务操作**：
```bash
# 停止后台任务
/stop <task-id>

# 等待任务完成
/wait <task-id>
```

#### 使用场景
- 长时间运行的测试
- 开发服务器启动
- 文件监控
- 定时任务

---

## 三、多模态与上下文管理

### 3.1 版本回滚 (Rewind)
**时间点**：15:35

#### Rewind 功能

**用途**：回溯会话历史，恢复到之前的对话状态

**使用方法**：
```bash
# 方法1：使用 /rewind 命令
/rewind

# 方法2：指定回滚轮数
/rewind 5  # 回滚 5 轮对话

# 方法3：回滚到特定消息
# 在 UI 中点击历史消息旁的 "Rewind" 按钮
```

#### 应用场景
- Claude 理解偏离了方向
- 想要尝试不同的解决方案
- 误操作后恢复
- 分支探索不同思路

#### 注意事项
- Rewind 后的后续对话会产生新的分支
- 原分支的历史仍可访问
- API 消耗按实际使用计算

---

### 3.2 图片处理
**时间点**：19:42

#### Claude Code 图像能力

**支持的图片任务**：
- 📸 读取和分析截图
- 🎨 设计稿转代码
- 📊 图表数据提取
- 🖼️ UI 界面分析

#### 使用方式

**方式1：直接拖拽图片**
```
1. 截取设计稿/界面截图
2. 直接拖拽到 Claude Code 窗口
3. 提出需求，如：
   "请根据这个设计稿生成 React 代码"
   "分析这个界面的问题"
```

**方式2：指定图片路径**
```bash
"请分析 /path/to/screenshot.png 中的布局结构"
```

#### 实战案例

**案例1：设计稿转代码**
```
你：[上传 Figma 设计稿截图]
    请根据这个设计稿生成前端代码，使用 React + Tailwind CSS

Claude 会：
1. 分析布局结构
2. 识别颜色和样式
3. 生成对应的组件代码
4. 添加响应式支持
```

**案例2：UI 问题诊断**
```
你：[上传问题界面截图]
    这个界面有什么问题？

Claude 会：
1. 分析 UI 布局
2. 识别设计问题
3. 给出改进建议
```

---

### 3.3 MCP Server（以 Figma 为例）
**时间点**：21:09

#### MCP (Model Context Protocol) 简介

MCP 是 Claude Code 的扩展协议，用于连接外部服务和工具。

#### 安装 Figma MCP Server

**步骤1：安装 MCP Server**
```bash
# 全局安装 Figma MCP 服务器
npm install -g @figma/mcp-server

# 或使用 npx（无需安装）
npx @figma/mcp-server
```

**步骤2：获取 Figma Access Token**
```
1. 登录 Figma
2. 进入 Settings > Account > Personal Access Tokens
3. 创建新 Token
4. 复制 Token
```

**步骤3：配置 MCP Server**
```json
// ~/.claude/settings.json
{
  "mcpServers": {
    "figma": {
      "command": "npx",
      "args": ["-y", "@figma/mcp-server"],
      "env": {
        "FIGMA_ACCESS_TOKEN": "your-figma-token-here"
      }
    }
  }
}
```

**步骤4：验证配置**
```bash
# 重启 Claude Code
claude

# 测试 Figma 连接
"列出我的 Figma 文件"
```

#### MCP Server 使用示例

```bash
# 获取 Figma 文件信息
"获取这个 Figma 文件的详细信息：[file-url]"

# 从 Figma 生成代码
"根据这个 Figma 设计生成 Flutter 代码"

# 提取设计规范
"从 Figma 文件中提取颜色规范和字体配置"
```

#### 其他常用 MCP Server

| MCP Server | 用途 | 安装命令 |
|------------|------|----------|
| GitHub | 管理 GitHub 仓库 | `npx @modelcontextprotocol/server-github` |
| Filesystem | 增强文件操作 | 内置 |
| Brave Search | 网络搜索 | `npx @modelcontextprotocol/server-brave-search` |
| PostgreSQL | 数据库操作 | `npx @modelcontextprotocol/server-postgres` |

---

### 3.4 恢复历史会话 (Resume)
**时间点**：21:39

#### Resume 功能

**用途**：恢复之前中断的会话，继续对话

**使用方法**：
```bash
# 方法1：启动时选择会话
claude
# 会显示最近会话列表，选择要恢复的会话

# 方法2：使用 /resume 命令
/resume

# 方法3：指定会话 ID
/resume <session-id>
```

#### 会话持久化

Claude Code 会自动保存：
- 对话历史
- 上下文信息
- 项目状态
- 任务进度

#### 应用场景
- 意外关闭后恢复
- 切换项目后返回
- 长时间任务的断点续传

---

### 3.5 使用 MCP 工具还原设计稿
**时间点**：23:06

#### 实战案例：Figma 设计稿转代码

**步骤1：获取 Figma 文件 URL**
```
在 Figma 中打开设计稿
复制分享链接（如：https://www.figma.com/file/xxx）
```

**步骤2：让 Claude 分析设计**
```bash
你：请分析这个 Figma 设计：https://www.figma.com/file/xxx

Claude 会：
1. 通过 MCP 连接 Figma
2. 读取设计稿结构
3. 分析组件层次
4. 识别样式属性
```

**步骤3：生成代码**
```bash
你：根据这个设计生成代码，使用技术栈：React + TypeScript + Tailwind

Claude 会：
1. 生成组件代码
2. 提取样式变量
3. 创建响应式布局
4. 添加必要注释
```

**步骤4：完善和调整**
```bash
你：请添加 hover 效果，并优化移动端显示

Claude 会继续完善代码
```

---

### 3.6 上下文压缩与清除
**时间点**：24:49

#### 上下文管理策略

**上下文压缩**：
- Claude Code 自动压缩历史对话
- 保留关键信息，丢弃冗余内容
- 减少_token_消耗

**手动管理上下文**：

```bash
# 清除当前会话上下文
/clear

# 重置会话
/reset

# 查看当前上下文大小
/context
```

#### 优化 Token 使用技巧

1. **使用 CLAUDE.md**：将项目信息写入文件，避免重复解释
2. **定期清除**：长时间会话后使用 `/clear`
3. **聚焦问题**：明确具体的提问，减少来回对话
4. **使用规划模式**：先规划再执行，减少试错成本

#### Token 使用监控

```bash
# 查看当前会话的 token 使用情况
/stats

# 会显示：
# - 已使用 token 数
# - 剩余 token 预算
# - 预计可用轮数
```

---

### 3.7 项目记忆文件 (CLAUDE.md)
**时间点**：26:30

#### CLAUDE.md 文件位置与优先级

Claude Code 按以下优先级加载配置：

| 优先级 | 位置 | 说明 |
|--------|------|------|
| 1（最高） | `/Library/Application Support/ClaudeCode/CLAUDE.md` | 企业策略配置 |
| 2 | `./CLAUDE.md` 或 `./.claude/CLAUDE.md` | **项目记忆（推荐）** |
| 3 | `./subdir/CLAUDE.md` | 子目录配置 |
| 4 | `~/.claude/CLAUDE.md` | 个人全局配置 |
| 5（最低） | `./CLAUDE.local.md` | 本地配置（不提交 Git） |

#### CLAUDE.md 推荐模板

```markdown
# <项目名称>

## 项目概述
[简要描述项目功能、目标和业务场景]

## 技术栈
- **前端**：React 18 + TypeScript + Tailwind CSS
- **后端**：Node.js + Express + PostgreSQL
- **构建工具**：Vite
- **测试框架**：Jest + Testing Library

## 项目结构
```
src/
├── components/    # 通用组件
├── pages/         # 页面组件
├── hooks/         # 自定义 Hooks
├── utils/         # 工具函数
├── api/           # API 接口
└── types/         # TypeScript 类型定义
```

## 代码规范
- 使用 TypeScript 严格模式
- **禁止使用 console.log**，使用 logger 工具
- 组件使用函数式组件 + Hooks
- 样式优先使用 Tailwind CSS
- 命名规范：
  - 组件：PascalCase（如 `UserProfile.tsx`）
  - 函数：camelCase（如 `getUserData`）
  - 常量：UPPER_SNAKE_CASE（如 `API_BASE_URL`）

## 常用命令
```bash
# 开发
npm run dev          # 启动开发服务器
npm run dev:mock     # 启动 mock 数据服务器

# 构建
npm run build        # 生产构建
npm run build:analyze # 构建并分析包大小

# 测试
npm test             # 运行测试
npm run test:watch   # 监听模式
npm run test:coverage # 生成覆盖率报告

# 代码质量
npm run lint         # ESLint 检查
npm run format       # Prettier 格式化
npm run type-check   # TypeScript 类型检查
```

## 注意事项
- 所有 API 请求需经过统一的 request 拦截器
- 环境变量统一放在 `.env.local` 文件中
- **禁止将敏感信息提交到 Git**
- 提交代码前必须通过 lint 和 type-check

## 特殊说明
- 本项目使用 monorepo 结构，子项目位于 `packages/` 目录
- 状态管理使用 Zustand
- 路由使用 React Router v6
```

#### CLAUDE.local.md（敏感信息）

```markdown
# 本地配置（不提交到 Git）

## 环境变量
- API_BASE_URL: http://localhost:3000
- AUTH_TOKEN: your-dev-token

## 本地开发说明
- 本地数据库端口：5433
- Mock 服务器端口：3001
```

#### 最佳实践

1. **持续更新**：项目演进时同步更新 CLAUDE.md
2. **具体明确**：提供清晰的规则和示例
3. **模块化管理**：大型项目在子目录添加独立配置
4. **团队共享**：项目 CLAUDE.md 提交到 Git
5. **敏感隔离**：使用 `.local.md` 存储本地配置

---

## 四、高级功能扩展与定制

### 4.1 Hook
**时间点**：29:46

#### Hook 简介

Hook 是一种事件触发机制，允许在特定事件发生时自动执行自定义命令。

#### 支持的 Hook 类型

| Hook 类型 | 触发时机 | 用途 |
|-----------|----------|------|
| `PreToolUse` | 工具调用前 | 阻止危险操作、添加日志 |
| `PostToolUseFailure` | 工具调用失败后 | 错误处理、重试逻辑 |
| `SessionEnd` | 会话结束时 | 清理工作、生成报告 |
| `PermissionRequest` | 权限请求时 | 自动授权/拒绝特定工具 |
| `SubagentStart` | 子代理启动时 | 初始化子代理环境 |
| `Setup` | 仓库初始化时 | 项目初始化配置 |

#### Hook 配置步骤

**步骤1：打开 Hook 配置**
```bash
# 运行 /hooks 命令
/hooks

# 选择要配置的 Hook 类型，如 PreToolUse
```

**步骤2：添加匹配器**
```
选择：+ Add new matcher...

输入要匹配的工具：
- Bash          # 匹配所有 Bash 命令
- Edit          # 匹配所有文件编辑
- *             # 匹配所有工具
```

**步骤3：添加 Hook 命令**
```
选择：+ Add new hook...

输入要执行的命令：
- echo "$(date): Running Bash command" >> ~/.claude/command.log
```

**步骤4：选择存储位置**
```
选择：
- User settings    # 用户级，所有项目生效
- Project settings # 项目级，当前项目生效（推荐团队使用）
```

**步骤5：保存配置**

#### 配置文件示例

```json
// ~/.claude/settings.json 或 ./.claude/settings.json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "echo \"[Claude] Executing: ${COMMAND}\" >> .claude/command.log"
          }
        ]
      },
      {
        "matcher": "Edit",
        "hooks": [
          {
            "type": "command",
            "command": "npm run lint-staged"
          }
        ]
      }
    ],
    "PostToolUseFailure": [
      {
        "matcher": "*",
        "hooks": [
          {
            "type": "command",
            "command": "notify-send 'Claude Code' 'Tool failed: ${TOOL}'"
          }
        ]
      }
    ]
  }
}
```

#### 实战案例

**案例1：自动运行 Lint**
```json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Edit",
        "hooks": [
          {
            "type": "command",
            "command": "npx eslint ${FILE --fix}"
          }
        ]
      }
    ]
  }
}
```

**案例2：阻止删除操作**
```json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "prompt",
            "prompt": "确认执行此 Bash 命令？",
            "condition": "${COMMAND} matches rm.*"
          }
        ]
      }
    ]
  }
}
```

**案例3：自动提交记录**
```json
{
  "hooks": {
    "SessionEnd": [
      {
        "matcher": "*",
        "hooks": [
          {
            "type": "command",
            "command": "echo \"Session ended at $(date)\" >> .claude/sessions.log"
          }
        ]
      }
    ]
  }
}
```

---

### 4.2 Agent Skill
**时间点**：33:16

#### Skill 简介

Skill 是教 Claude 执行特定任务的"技能包"，本质是一个 Markdown 文件（SKILL.md）。

#### 创建 Skill 的两种方式

**方式1：使用 Skill Creator（推荐新手）**

```bash
# 启动 Skill Creator
/skill create

# Claude 会引导你：
1. Skill 的用途是什么？
2. 需要哪些工具？
3. 什么情况下应该使用这个 Skill？

# 自动生成：
- 技能文件夹
- SKILL.md 文件
- 配置文件
```

**方式2：手动创建**

```
skills/
└── my-skill/
    ├── SKILL.md      # 技能定义文件
    ├── schema.json   # 参数定义（可选）
    └── example/      # 示例文件（可选）
```

#### SKILL.md 模板

```markdown
# 技能名称

## 描述
[简要描述这个技能的功能和用途]

## 何时使用
- 场景1：[描述具体使用场景]
- 场景2：[描述具体使用场景]

## 工具需求
- Read: 读取文件
- Edit: 编辑文件
- Bash: 执行命令

## 执行步骤
1. [第一步操作]
2. [第二步操作]
3. [第三步操作]

## 注意事项
- [需要注意的点]
- [特殊情况的说明]

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

#### 实战案例：PR 审查 Skill

**文件结构**：
```
skills/
└── pr-review/
    └── SKILL.md
```

**SKILL.md 内容**：
```markdown
# PR Review 技能

## 描述
自动审查 Pull Request，检查代码质量、规范合规性和潜在问题。

## 何时使用
- 创建 Pull Request 后
- 需要代码审查时
- 提交前自检

## 工具需求
- Read: 读取修改的文件
- Bash: 运行 linter 和测试
- Grep: 搜索特定模式

## 执行步骤
1. 获取当前分支修改的文件列表
2. 检查每个修改的文件：
   - 代码风格是否符合规范
   - 是否有明显的 bug
   - 是否有安全隐患
   - 注释是否充分
3. 运行 linter 检查
4. 运行相关测试
5. 生成审查报告

## 检查项
- [ ] 代码风格符合 ESLint 规则
- [ ] 没有 console.log 或调试代码
- [ ] 敏感信息未提交
- [ ] 测试覆盖新功能
- [ ] 变更文档已更新（如需要）

## 输出格式
```markdown
## PR 审查报告

### 修改文件
- [x] 文件1
- [x] 文件2

### 问题发现
#### 🔴 严重问题
- [问题描述]

#### 🟡 建议改进
- [改进建议]

#### ✅ 通过项
- [通过的检查项]

### 总结
[整体评价和建议]
```
```

#### 使用 Skill

```bash
# 调用 Skill
/skill pr-review

# 或自然语言触发
"请帮我审查当前的 PR"

# Claude 会自动：
1. 识别需要使用 pr-review 技能
2. 按照 SKILL.md 定义的步骤执行
3. 生成审查报告
```

---

### 4.3 SubAgent
**时间点**：36:00

#### SubAgent 简介

SubAgent 是"子代理"，类似于专业开发团队中的专职工程师，每个 SubAgent 专注于特定领域。

#### 内置 SubAgent

| SubAgent | 专长 | 适用场景 |
|----------|------|----------|
| `Explore` | 代码库探索 | 理解项目结构、查找文件 |
| `Plan` | 方案规划 | 架构设计、重构方案 |
| `general-purpose` | 通用任务 | 复杂多步骤任务 |

#### SubAgent vs 主 Agent

| 特性 | 主 Agent | SubAgent |
|------|----------|----------|
| 上下文 | 共享主会话 | 独立上下文 |
| 工具配置 | 全部工具 | 精简专用工具 |
| 权限 | 继承主设置 | 可独立配置 |
| 用途 | 综合协调 | 专职任务 |

#### 使用内置 SubAgent

```bash
# Claude 会自动决定何时使用 SubAgent
# 也可以明确指定

你：使用 Explore agent 分析这个项目的数据流

Claude 会：
1. 启动 Explore SubAgent
2. SubAgent 专注探索代码库
3. 收集信息后返回主 Agent
4. 主 Agent 整合结果
```

#### 创建自定义 SubAgent

**配置位置**：`~/.claude/settings.json` 或 `./.claude/settings.json`

```json
{
  "subagents": {
    "security-reviewer": {
      "description": "专注于代码安全审查的子代理",
      "tools": ["Read", "Grep", "Bash"],
      "systemPrompt": "你是一个安全审查专家。你的任务是审查代码中的安全问题，包括但不限于：SQL注入、XSS、CSRF、敏感信息泄露等。",
      "permissions": {
        "dangerouslySkipPermissions": false
      }
    },
    "test-writer": {
      "description": "专注于编写测试用例的子代理",
      "tools": ["Read", "Write", "Edit", "Bash"],
      "systemPrompt": "你是一个测试工程师。你的任务是为现有代码编写完整的单元测试和集成测试。",
      "permissions": {
        "allowedTools": ["Read", "Write", "Edit", "Bash"]
      }
    },
    "documenter": {
      "description": "专注于生成文档的子代理",
      "tools": ["Read", "Write"],
      "systemPrompt": "你是一个技术文档撰写专家。你的任务是为代码生成清晰、完整的文档，包括 API 文档、使用示例等。"
    }
  }
}
```

#### 使用自定义 SubAgent

```bash
# 方式1：自然语言触发
你：请用 security-reviewer 检查这个文件的安全性

# 方式2：明确指定
你：使用 test-writer 为 AuthService 编写测试

# Claude 会：
# 1. 启动对应的 SubAgent
# 2. SubAgent 在独立上下文中工作
# 3. 完成后返回结果
```

#### SubAgent 最佳实践

1. **职责单一**：每个 SubAgent 只做一件事
2. **工具精简**：只给必要的工具，避免混乱
3. **描述清晰**：systemPrompt 要明确角色和任务
4. **合理授权**：根据需要配置权限

---

### 4.4 Skill 与 SubAgent 的区别
**时间点**：39:17

#### 核心区别对比

| 维度 | Skill | SubAgent |
|------|-------|----------|
| **本质** | 任务流程模板 | 独立的 AI 助手 |
| **上下文** | 共享主会话 | 独立上下文 |
| **工具** | 使用主 Agent 工具 | 可配置独立工具集 |
| **记忆** | 无独立记忆 | 有独立对话历史 |
| **复杂度** | 简单任务脚本 | 复杂决策代理 |
| **配置方式** | SKILL.md 文件 | settings.json 配置 |
| **创建难度** | 低（Markdown） | 中（JSON 配置） |

#### 选择指南

**使用 Skill 的场景**：
- ✅ 固定流程的任务（如 PR 审查）
- ✅ 简单重复性操作
- ✅ 不需要独立上下文
- ✅ 快速创建和分享

**使用 SubAgent 的场景**：
- ✅ 需要深度分析的任务
- ✅ 需要独立记忆空间
- ✅ 需要限制工具权限
- ✅ 专业领域的复杂决策

#### 组合使用

```
主 Agent (协调者)
    ├── Skill: 生成 commit message
    ├── SubAgent: Explore (探索代码)
    ├── SubAgent: Security (安全审查)
    └── Skill: 生成文档
```

**示例流程**：
```bash
你：帮我重构这个模块，并生成相关文档

Claude 处理流程：
1. [SubAgent: Explore] 分析当前代码结构
2. [Plan Mode] 制定重构方案
3. [主 Agent] 执行重构
4. [SubAgent: Security] 审查安全性
5. [Skill: Documenter] 生成文档
```

---

### 4.5 Plugin
**时间点**：40:38

#### Plugin 系统

Plugin 是 Claude Code 的扩展机制，类似于 VS Code 插件。

#### 常用 Plugin

| Plugin | 功能 | 安装方式 |
|--------|------|----------|
| **VSCode 插件** | IDE 内集成 | VSCode 扩展市场 |
| **JetBrains 插件** | IntelliJ/PyCharm 集成 | JetBrains Marketplace |
| **GitHub Copilot 集成** | 与 Copilot 配合 | 配置文件启用 |

#### VSCode 插件安装

```bash
# 方式1：VSCode 扩展市场搜索
1. 打开 VSCode
2. 进入扩展面板
3. 搜索 "Claude Code"
4. 点击安装

# 方式2：命令行安装
code --install-extension anthropic.claude-code
```

#### JetBrains 插件安装

```
1. 打开 IntelliJ IDEA / PyCharm / Android Studio
2. 进入 File > Settings > Plugins
3. 搜索 "Claude Code"
4. 安装并重启 IDE
```

#### Plugin 使用示例

**VSCode 中使用**：
```
1. 打开项目
2. 右侧 Claude Code 面板
3. 直接在侧边栏对话
4. 代码变更实时显示在编辑器
```

**JetBrains 中使用**：
```
1. 打开项目
2. Tools > Claude Code
3. 打开 Claude Code 窗口
4. 与项目代码交互
```

---

## 五、知识点总结

### 核心概念对比

| 特性 | Skill | SubAgent | MCP | Hook |
|------|-------|----------|-----|------|
| **主要用途** | 任务流程模板 | 专业 AI 助手 | 外部服务集成 | 事件触发 |
| **适用场景** | 固定流程任务 | 专业领域分析 | API/工具调用 | 自动化控制 |
| **复杂度** | 低 | 高 | 中 | 中 |
| **上下文** | 共享主会话 | 独立上下文 | - | - |
| **配置方式** | SKILL.md | settings.json | settings.json | settings.json |
| **创建难度** | 简单 | 中等 | 中等 | 中等 |

### 学习路径建议

```
第一阶段：基础入门（1-2周）
├── 安装和配置 Claude Code
├── 掌握基本对话和命令执行
├── 理解三种模式的区别和使用
└── 学会使用 Rewind 和 Resume

第二阶段：进阶使用（2-3周）
├── 规划模式的应用
├── 后台任务管理
├── 图片处理能力
└── 上下文管理优化

第三阶段：高级功能（3-4周）
├── MCP Server 集成
├── Hook 配置和自动化
├── 创建自定义 Skill
└── SubAgent 开发

第四阶段：工程化实践
├── CLAUDE.md 项目配置
├── 团队协作规范
├── 完整的开发工作流
└── 性能优化和成本控制
```

### 最佳实践清单

#### 日常开发
- [ ] 使用 CLAUDE.md 记录项目信息
- [ ] 重要操作前使用 Plan Mode
- [ ] 定期使用 `/clear` 清理上下文
- [ ] 使用 Git 分支进行实验性开发

#### 团队协作
- [ ] 项目级 CLAUDE.md 提交到 Git
- [ ] 敏感信息使用 `.local.md` 文件
- [ ] 共享 Skill 和 SubAgent 配置
- [ ] 统一 Hook 规范

#### 安全防护
- [ ] 生产环境谨慎使用 `dangerously-skip-permissions`
- [ ] 重要操作配置 Hook 预警
- [ ] 定期检查 Claude 的操作历史
- [ ] 使用 Git 方便回滚

---

## 六、参考资料

### 官方资源
- [Claude Code 官方文档](https://docs.anthropic.com/claude-code)
- [Claude API 控制台](https://console.anthropic.com)
- [MCP 协议规范](https://modelcontextprotocol.io)

### 推荐教程
- [Claude Code Hooks 入门教程](https://m.blog.csdn.net/gitblog_00035/article/details/148758595)
- [Claude Code Skills 完整指南](https://juejin.cn/post/7596181746061656091)
- [Claude Code SubAgent 实战指南](https://m.blog.csdn.net/qq_32483009/article/details/157183611)
- [Figma MCP 使用指南](https://juejin.cn/post/7572390974458953728)

### 相关视频
- [Agent Skill 从使用到原理，一次讲清](https://www.bilibili.com/video/)
- [【闪客】一口气拆穿Skill/MCP/RAG/Agent底层逻辑](https://www.bilibili.com/video/)
- [15分钟Claude Code小白入门](https://www.bilibili.com/video/)

---

**文档生成时间**：2026-03-02
**笔记作者**：Claude Code 自动生成
**文档版本**：v2.0（详细操作版）
