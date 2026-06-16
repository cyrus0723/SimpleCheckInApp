# Simple Check-In / 简单打卡 / シンプルチェックイン

## 开发声明 / 開発について / Development

从 2026 年 6 月 16 日开始，这个项目会由我和好友 XJJ 一起开发。

2026 年 6 月 16 日から、このプロジェクトは私と友人 XJJ が一緒に開発します。

Starting on June 16, 2026, I will develop this project together with my friend XJJ.

## 中文

简单打卡是一个轻量级 Android 打卡应用，用于记录每天是否完成一次习惯、任务或学习目标。应用不依赖后端，所有数据都保存在本机。

### 功能

- 点击“今日打卡”记录当天。
- 同一天只能打卡一次，已打卡后按钮会禁用。
- 近 365 天热力图展示打卡记录。
- 可以自定义记录开始日期，不再限制只能查看近一年。
- 绿色方块表示已打卡，灰色方块表示未打卡。
- 今天会显示绿色边框，当前选中的日期会显示黑色边框。
- 点击任意日期可以查看当天状态。
- 支持补打卡和删除某一天的记录。
- 支持多选日期后一次性补打卡。
- 支持导入文本补打卡，例如 `2025年6月1,3,5,6,7日` 或 `2025-06-01, 2025-06-03`。
- 显示自定义起始日期以来的打卡次数、最近一次打卡、距离上次打卡天数和平均打卡间隔。
- 支持将打卡记录导出为 CSV 文本，并通过系统分享面板发送到其他应用。
- 使用 `SharedPreferences` 本地保存数据，关闭 App 后不会丢失。

### 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- SharedPreferences
- Minimum SDK 26

### 运行方式

1. 使用 Android Studio 打开 `SimpleCheckInApp` 目录。
2. 等待 Gradle Sync 完成。
3. 连接 Android 设备或启动模拟器。
4. 点击 Run 运行 `app`。

### APK

调试 APK 通常生成在：

```text
app/build/outputs/apk/debug/app-debug.apk
```

### 数据说明

打卡日期以 `yyyy-MM-dd` 格式保存在本机 `SharedPreferences` 中。卸载应用会清除本地数据。

导出的 CSV 格式示例：

```csv
date
2026-06-01
2026-06-03
```

---

## 日本語

シンプルチェックインは、毎日の習慣、タスク、学習目標の達成状況を記録するための軽量な Android アプリです。バックエンドは不要で、すべてのデータは端末内に保存されます。

### 機能

- 「今日チェックイン」ボタンで当日の記録を追加。
- 同じ日は一度だけチェックイン可能。記録済みの場合、ボタンは無効になります。
- 過去 365 日の記録をヒートマップで表示。
- 記録の開始日を自由に設定でき、過去 1 年だけに限定されません。
- 緑のマスはチェックイン済み、灰色のマスは未チェックイン。
- 今日の日付には緑の枠、選択中の日付には黒い枠を表示。
- 任意の日付をタップして、その日の状態を確認。
- 過去の日付への追加チェックインと記録削除に対応。
- 複数の日付を選択して、一括で追加チェックインできます。
- テキスト入力から日付をインポートできます。例：`2025年6月1,3,5,6,7日` または `2025-06-01, 2025-06-03`。
- 設定した開始日以降のチェックイン回数、直近のチェックイン日、前回からの日数、平均間隔を表示。
- チェックイン記録を CSV テキストとして書き出し、Android の共有シートから他のアプリへ送信可能。
- `SharedPreferences` にローカル保存するため、アプリを閉じてもデータは保持されます。

### 技術スタック

- Kotlin
- Jetpack Compose
- Material 3
- SharedPreferences
- Minimum SDK 26

### 実行方法

1. Android Studio で `SimpleCheckInApp` ディレクトリを開きます。
2. Gradle Sync が完了するまで待ちます。
3. Android 端末を接続するか、エミュレーターを起動します。
4. Run をクリックして `app` を実行します。

### APK

デバッグ APK は通常、次の場所に生成されます。

```text
app/build/outputs/apk/debug/app-debug.apk
```

### データについて

チェックイン日は `yyyy-MM-dd` 形式で端末内の `SharedPreferences` に保存されます。アプリをアンインストールするとローカルデータは削除されます。

CSV 出力例：

```csv
date
2026-06-01
2026-06-03
```

---

## English

Simple Check-In is a lightweight Android app for tracking daily habits, tasks, or study goals. It does not require a backend; all data is stored locally on the device.

### Features

- Tap “Today Check-In” to record the current day.
- Each day can be checked in only once. After check-in, the button is disabled.
- A 365-day heatmap shows recent check-in history.
- The record start date is customizable, so the view is no longer limited to one year.
- Green squares mean checked in, and gray squares mean not checked in.
- Today is highlighted with a green border, while the selected date has a black border.
- Tap any date to inspect its status.
- Add a missed check-in or delete a record for a selected date.
- Select multiple dates and add missed check-ins in one action.
- Import dates from text, such as `2025年6月1,3,5,6,7日` or `2025-06-01, 2025-06-03`.
- Shows check-in count since the custom start date, the latest check-in date, days since the latest check-in, and the average interval.
- Export records as CSV text and send them to other apps through the Android share sheet.
- Data is saved locally with `SharedPreferences`, so it remains after closing the app.

### Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- SharedPreferences
- Minimum SDK 26

### Run

1. Open the `SimpleCheckInApp` directory in Android Studio.
2. Wait for Gradle Sync to finish.
3. Connect an Android device or start an emulator.
4. Click Run and launch `app`.

### APK

The debug APK is usually generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Data

Check-in dates are stored locally in `SharedPreferences` using the `yyyy-MM-dd` format. Uninstalling the app removes the local data.

CSV export example:

```csv
date
2026-06-01
2026-06-03
```
