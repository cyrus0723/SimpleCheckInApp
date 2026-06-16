# 协作规范

这个仓库由两个人共同开发。目标很简单：大家可以同时做事，但不要互相覆盖代码，也不要把不稳定代码直接放进 `main`。

## 分支策略

本仓库使用下面的分支方式：

- `main`：稳定分支，只存放可运行版本。
- `feature/xxx`：新功能开发分支，例如 `feature/import-calendar`。
- `fix/xxx`：bug 修复分支，例如 `fix/heatmap-scroll`。
- `docs/xxx`：文档修改分支，例如 `docs/collaboration-guide`。

## 基本规则

- 禁止直接向 `main` 分支提交代码。
- 每个功能、修复或文档修改都要单独创建分支。
- 开发前先同步最新 `main`。
- 开发完成后通过 Pull Request 合并。
- Pull Request 合并前至少需要另一名开发者检查。
- `main` 分支必须一直保持可运行状态。
- 不要在一个分支里混合多个无关改动。

## 开始开发

先切回 `main`，同步最新代码：

```bash
git checkout main
git pull origin main
```

然后创建自己的开发分支：

```bash
git checkout -b feature/example
```

如果是修 bug：

```bash
git checkout -b fix/example-bug
```

如果只是改文档：

```bash
git checkout -b docs/update-readme
```

## 提交和推送

完成修改后先查看状态：

```bash
git status
```

提交代码：

```bash
git add .
git commit -m "feat: add example feature"
```

推送自己的分支：

```bash
git push origin feature/example
```

然后在 GitHub 上创建 Pull Request，请对方检查后再合并。

## 常用提交信息

可以使用简单的前缀：

- `feat:` 新功能
- `fix:` 修复 bug
- `docs:` 文档修改
- `chore:` 配置、构建、整理类修改

示例：

```bash
git commit -m "fix: keep heatmap scrolled to today"
git commit -m "docs: add collaboration guide"
```

## 合并前检查

合并 Pull Request 前，请确认：

- 代码是从功能分支提交的，不是直接提交到 `main`。
- 分支已经同步过最新 `main`。
- App 可以正常构建或运行。
- Pull Request 里写清楚了改了什么、为什么改、怎么测试。
- 至少另一名开发者已经 review。

## 如果 main 有更新

开发过程中如果 `main` 更新了，先把最新内容合到自己的分支：

```bash
git checkout main
git pull origin main
git checkout feature/example
git merge main
```

如果出现冲突，先解决冲突，再提交：

```bash
git status
git add .
git commit -m "chore: resolve merge conflict"
```

不确定怎么处理冲突时，先不要强行提交，和对方确认。
