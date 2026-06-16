# GitHub 两人协作指南

这份文档给新手看，目标是让两个人可以一起开发，但不会互相覆盖代码，也不会把没测试好的内容直接放进 `main`。

## 1. 邀请共同开发者

仓库拥有者在 GitHub 网页上操作：

1. 打开仓库页面。
2. 进入 `Settings`。
3. 进入 `Collaborators`。
4. 点击 `Add people`。
5. 输入对方的 GitHub 用户名或邮箱。
6. 发送邀请。

对方接受邀请后，就可以参与开发。

## 2. 权限建议

普通共同开发者建议给 `Write` 权限。

`Write` 权限可以：

- 拉取代码。
- 推送自己的功能分支。
- 创建 Pull Request。
- review 对方的代码。

不要一开始就给 `Admin` 权限。`Admin` 可以修改仓库设置、删除保护规则、管理权限，风险更高。除非对方也负责仓库管理，否则 `Write` 就够了。

## 3. 为什么不要两个人都直接 push main

`main` 应该只放稳定版本。

如果两个人都直接 push `main`，容易出现这些问题：

- A 的代码还没测试完，B 又基于它继续开发，问题会被放大。
- 两个人同时改同一个文件，后 push 的人可能覆盖前一个人的修改。
- 出 bug 后不容易知道是哪次修改引入的。
- 没有人 review，明显问题也可能直接进入稳定分支。

所以规则是：不要直接 push `main`。先开自己的分支，完成后提交 Pull Request。

## 4. 每次开发前先同步 main

开始写代码前，先拿到最新代码：

```bash
git checkout main
git pull origin main
```

这样可以减少冲突，也能避免基于旧代码开发。

## 5. 创建自己的功能分支

新功能用 `feature/xxx`：

```bash
git checkout -b feature/import-data
```

修 bug 用 `fix/xxx`：

```bash
git checkout -b fix/heatmap-scroll
```

改文档用 `docs/xxx`：

```bash
git checkout -b docs/update-guide
```

分支名尽量短一点，但要能看懂做什么。

## 6. 提交自己的修改

修改完成后查看状态：

```bash
git status
```

提交：

```bash
git add .
git commit -m "feat: add import data"
```

推送到 GitHub：

```bash
git push origin feature/import-data
```

## 7. 提交 Pull Request

推送分支后，到 GitHub 仓库页面创建 Pull Request。

Pull Request 里要写清楚：

- 本次改了什么。
- 为什么要改。
- 怎么测试。
- 是否影响已有功能。
- 希望对方重点看哪些文件。

不要只写“更新一下”或“修复问题”。对方需要知道你具体做了什么。

## 8. Review 对方代码

review 时重点看这些：

- 代码是不是解决了 Pull Request 里说的问题。
- 有没有明显会崩溃或影响旧功能的地方。
- 有没有把无关文件也一起改了。
- App 是否还能正常运行。
- 文档或说明是否需要同步更新。

如果有问题，直接在 Pull Request 里留言。语气简单清楚就行，例如：

```text
这里如果日期为空会不会崩溃？建议加一个空值判断。
```

如果没问题，就 approve。

## 9. 合并 Pull Request

合并前确认：

- 至少一名开发者已经 review。
- CI 通过。
- Pull Request 没有冲突。
- 这次修改范围清楚。

合并后可以删除这个功能分支，保持分支列表干净。

## 10. 如果发生 merge conflict

merge conflict 通常是两个人改了同一个文件的同一部分。

处理方式：

1. 先不要慌，也不要随便覆盖文件。
2. 把最新 `main` 合到自己的分支：

```bash
git checkout main
git pull origin main
git checkout feature/import-data
git merge main
```

3. 打开冲突文件，找到类似这样的内容：

```text
<<<<<<< HEAD
你的修改
=======
main 上的修改
>>>>>>> main
```

4. 手动整理成最终想保留的内容。
5. 删除 `<<<<<<<`、`=======`、`>>>>>>>` 这些标记。
6. 重新构建或运行项目。
7. 提交冲突解决：

```bash
git add .
git commit -m "chore: resolve merge conflict"
git push origin feature/import-data
```

如果不知道该保留哪一边，先问对方。

## 11. 推荐的每日开发流程

每天开始：

```bash
git checkout main
git pull origin main
git checkout -b feature/today-task
```

开发中：

```bash
git status
git add .
git commit -m "feat: describe your change"
git push origin feature/today-task
```

提交 Pull Request 后：

- 等对方 review。
- 根据评论修改。
- CI 通过后再合并。

合并后：

```bash
git checkout main
git pull origin main
```

然后再开始下一个分支。

## 12. 最重要的规则

- 不直接 push `main`。
- 一个任务一个分支。
- 合并前让对方看一眼。
- `main` 必须保持能运行。
