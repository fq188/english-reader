# Requirements Document

## Introduction

英文文献阅读器（English Reader）是一款面向中国学生和科研人员的桌面端文献阅读软件，支持 Windows/macOS 双平台。核心目标是解决阅读英文 PDF 学术论文时的语言障碍，提供高效的翻译、笔记和文献管理能力。

## Glossary

- **TL;DR**：Too Long; Didn't Read，一句话概括论文核心
- **Chunking**：文本分块，将长文档切分为适合 LLM 处理的段落
- **Reference Pop-up**：参考文献弹窗，悬浮显示引用文献信息
- **生词本**：用户收藏的单词列表，支持导出 CSV/Anki 格式
- **SSE**：Server-Sent Events，服务端推送事件，用于流式输出

## Requirements

### Requirement 1: 文档阅读器 (P0)

**User Story:** 作为用户，我想要高效阅读 PDF 格式的学术论文，以便于专注阅读本身。

#### Acceptance Criteria

1. WHEN 用户打开 PDF 文件，THEN 系统 SHALL 使用 PDF.js 渲染文档并显示内容
2. WHEN 用户滚动页面，THEN 系统 SHALL 平滑滚动而非卡顿
3. The system SHALL 支持缩放功能（50%-200%）
4. The system SHALL 支持多标签页，允许同时打开多篇文档
5. WHEN 用户切换标签页，THEN 系统 SHALL 正确恢复该文档的阅读位置
6. WHILE 用户在阅读界面，THEN 系统 SHALL 显示当前页码和总页码

### Requirement 2: 本地书库管理 (P0)

**User Story:** 作为用户，我想要管理和组织我的文献库，以便于快速找到需要的文献。

#### Acceptance Criteria

1. WHEN 用户拖拽 PDF 文件到应用窗口，THEN 系统 SHALL 导入文件并显示导入成功提示
2. The system SHALL 自动提取文档元数据（标题、作者、年份、期刊名称）
3. The system SHALL 支持按文件夹分类文献
4. The system SHALL 支持为文献添加自定义标签
5. The system SHALL 支持关键词搜索文献（标题、作者）
6. The system SHALL 使用 SQLite 存储文献元数据、笔记和生词本

### Requirement 3: 划词翻译 (P0)

**User Story:** 作为用户，我想要选中单词或段落进行翻译，以便于理解不懂的内容。

#### Acceptance Criteria

1. WHEN 用户双击单词，THEN 系统 SHALL 弹出侧边栏显示音标和释义（调用有道或必应词典 API）
2. WHEN 用户选中段落或句子，THEN 系统 SHALL 在旁边显示翻译结果
3. The system SHALL 预留 DeepL 和 Google Translate API 接口
4. IF 网络请求失败，THEN 系统 SHALL 显示"翻译服务暂时不可用，请检查网络连接"
5. IF 翻译结果为空，THEN 系统 SHALL 显示"未找到翻译结果"

### Requirement 4: 参考文献弹窗 (P1)

**User Story:** 作为用户，我想要快速查看文献引用信息，而不必翻到文章末尾。

#### Acceptance Criteria

1. WHEN 系统检测到 `[数字]` 格式的引用标识，THEN 系统 SHALL 在该位置显示悬浮提示图标
2. WHEN 用户鼠标悬浮在引用标识上，THEN 系统 SHALL 弹出浮窗显示该参考文献的完整信息
3. WHEN 系统检测到 `(作者, 年份)` 格式的引用，THEN 系统 SHALL 同样显示参考文献弹窗
4. IF 引用文献不在参考文献列表中，THEN 系统 SHALL 显示"未找到对应参考文献"

### Requirement 5: 沉浸式分屏 (P1)

**User Story:** 作为用户，我想要左右分屏同时查看原文和笔记，以便于边读边记。

#### Acceptance Criteria

1. The system SHALL 支持左右分屏布局
2. WHILE 分屏模式开启，THEN 左侧 SHALL 显示 PDF 原文，右侧 SHALL 显示笔记区域
3. The system SHALL 右侧笔记支持 Markdown 格式编辑和渲染
4. The system SHALL 支持中英文对照翻译显示在右侧分屏

### Requirement 6: 生词本与复习 (P1)

**User Story:** 作为用户，我想要收藏生词并复习，以便于学习新词汇。

#### Acceptance Criteria

1. WHEN 用户点击"加入生词本"按钮，THEN 系统 SHALL 保存单词及其所在原文例句到 SQLite
2. The system SHALL 支持导出生词本为 CSV 格式
3. The system SHALL 支持导出生词本为 Anki 格式（包含单词、音标、释义、例句）
4. The system SHALL 在生词本列表中显示收藏的单词数量

### Requirement 7: 高亮与批注 (P1)

**User Story:** 作为用户，我想要高亮重要内容和添加批注，以便于整理阅读笔记。

#### Acceptance Criteria

1. The system SHALL 支持多种颜色高亮（黄色、绿色、蓝色、粉色）
2. The system SHALL 支持在页面侧边添加文本批注
3. The system SHALL 将所有高亮内容汇总显示在右侧笔记区
4. WHEN 用户删除高亮，THEN 系统 SHALL 从笔记区同步移除

### Requirement 8: AI 论文速读 (P2)

**User Story:** 作为用户，我想要快速了解论文的核心内容，以便于决定是否深入阅读。

#### Acceptance Criteria

1. The system SHALL 提供"AI 总结"标签页显示摘要信息
2. WHEN 用户点击"生成深度总结"按钮，THEN 系统 SHALL 调用 LLM API 生成结构化总结
3. IF 用户开启"导入时自动生成"，THEN 系统 SHALL 在后台自动生成总结
4. The summary SHALL 使用 Markdown 格式渲染，支持加粗、列表、分级标题
5. The summary SHALL 包含以下结构化内容：
   - 一句话核心 (TL;DR)
   - 研究背景与痛点 (Motivation)
   - 核心创新点 (Contributions)
   - 研究方法/实验设计 (Methodology)
   - 关键结论与数据 (Key Findings)
   - 局限性与未来方向 (Limitations & Future Work)

### Requirement 9: 进阶 AI 交互 (P2)

**User Story:** 作为用户，我想要基于论文内容提问，以便于深入理解细节。

#### Acceptance Criteria

1. The system SHALL 支持流式输出（SSE），打字机效果显示总结内容
2. The system SHALL 支持在总结页面输入追问问题
3. WHEN 用户点击"原文定位"链接，THEN 系统 SHALL 跳转到 PDF 对应位置
4. IF PDF 文本超出 LLM 上下文限制，THEN 系统 SHALL 使用 Chunking 策略优先提取 Abstract、Introduction、Conclusion

### Requirement 10: 界面本地化 (P0)

**User Story:** 作为中国用户，我想要软件界面使用中文，以便于无障碍使用。

#### Acceptance Criteria

1. The system SHALL 所有用户界面文字使用简体中文
2. The system SHALL 菜单、按钮、提示信息均使用中文
3. The system SHALL 错误信息使用中文描述

### Requirement 11: 窗口管理 (P0)

**User Story:** 作为用户，我想要自定义窗口大小和位置，以便于适应我的工作环境。

#### Acceptance Criteria

1. WHEN 用户启动应用，THEN 系统 SHALL 以默认尺寸（1200x800）显示窗口
2. The system SHALL 支持窗口最大化、最小化、关闭操作
3. The system SHALL 支持窗口拖拽改变大小
4. WHEN 用户关闭应用，THEN 系统 SHALL 记住窗口尺寸和位置供下次使用

## Constraints

1. 系统支持 Windows 10/11 和 macOS 12+ 操作系统
2. 翻译和 AI 功能依赖网络连接，离线状态下仅能使用本地功能
3. 所有文档处理在本地完成，不上传用户文档到任何服务器
4. PDF.js 用于前端 PDF 渲染，SQLite 用于本地数据存储
