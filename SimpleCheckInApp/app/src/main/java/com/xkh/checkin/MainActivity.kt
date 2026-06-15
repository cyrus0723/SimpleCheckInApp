package com.xkh.checkin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {
    private val prefsName = "checkin_prefs"
    private val keyDates = "checkin_dates"
    private val keyStartDate = "record_start_date"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheckInApp(
                loadDates = { loadCheckInDates() },
                saveDates = { saveCheckInDates(it) },
                loadStartDate = { loadStartDate() },
                saveStartDate = { saveStartDate(it) }
            )
        }
    }

    private fun loadCheckInDates(): Set<String> {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return prefs.getStringSet(keyDates, emptySet())?.toSet() ?: emptySet()
    }

    private fun saveCheckInDates(dates: Set<String>) {
        getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(keyDates, dates)
            .apply()
    }

    private fun loadStartDate(): LocalDate {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val saved = prefs.getString(keyStartDate, null)
        return saved?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            ?: LocalDate.now().minusDays(364)
    }

    private fun saveStartDate(date: LocalDate) {
        getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            .edit()
            .putString(keyStartDate, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .apply()
    }
}

@Composable
fun CheckInApp(
    loadDates: () -> Set<String>,
    saveDates: (Set<String>) -> Unit,
    loadStartDate: () -> LocalDate,
    saveStartDate: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    var checkInDates by remember { mutableStateOf(loadDates()) }
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    var recordStartDate by remember { mutableStateOf(loadStartDate().coerceAtMost(today)) }
    var startDateInput by remember { mutableStateOf(recordStartDate.format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var startDateError by remember { mutableStateOf<String?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importInput by remember { mutableStateOf("2025年6月1,3,5,6,7日") }
    var importError by remember { mutableStateOf<String?>(null) }
    var multiSelectMode by remember { mutableStateOf(false) }
    var multiSelectedDates by remember { mutableStateOf(emptySet<LocalDate>()) }

    val todayText = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val selectedDateText = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val hasCheckedToday = checkInDates.contains(todayText)
    val selectedDateChecked = checkInDates.contains(selectedDateText)
    val visibleDates = remember(checkInDates, recordStartDate, today) {
        filterDatesSince(checkInDates, recordStartDate, today)
    }
    val visibleDayCount = ChronoUnit.DAYS.between(recordStartDate, today).toInt() + 1

    fun saveNewDates(newDates: Set<String>) {
        checkInDates = newDates
        saveDates(newDates)
    }

    fun applyStartDate() {
        val parsed = runCatching { LocalDate.parse(startDateInput.trim()) }.getOrNull()
        when {
            parsed == null -> startDateError = "请输入 yyyy-MM-dd，例如 2025-06-01"
            parsed.isAfter(today) -> startDateError = "开始日期不能晚于今天"
            else -> {
                recordStartDate = parsed
                saveStartDate(parsed)
                selectedDate = selectedDate.coerceAtLeast(parsed)
                startDateError = null
            }
        }
    }

    fun importDates() {
        val parsed = parseImportedDates(importInput, today.year)
        if (parsed.isEmpty()) {
            importError = "没有识别到日期。可输入：2025年6月1,3,5,6,7日"
            return
        }

        val importedKeys = parsed.map { it.format(DateTimeFormatter.ISO_LOCAL_DATE) }.toSet()
        saveNewDates(checkInDates + importedKeys)
        val earliest = parsed.minOrNull()
        if (earliest != null && earliest.isBefore(recordStartDate)) {
            recordStartDate = earliest
            startDateInput = earliest.format(DateTimeFormatter.ISO_LOCAL_DATE)
            saveStartDate(earliest)
        }
        selectedDate = parsed.maxOrNull() ?: selectedDate
        importError = null
        showImportDialog = false
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF7F8F7)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "简单打卡",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "自 ${recordStartDate} 起已打卡 ${visibleDates.size} 次",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (!hasCheckedToday) {
                            saveNewDates(checkInDates + todayText)
                            selectedDate = today
                        }
                    },
                    enabled = !hasCheckedToday,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF33B56F),
                        disabledContainerColor = Color(0xFFB9DCC7)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    Text(text = if (hasCheckedToday) "今日已打卡" else "今日打卡")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "记录热力图",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "绿色=已打卡，灰色=未打卡",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF888888)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        StartDateEditor(
                            value = startDateInput,
                            error = startDateError,
                            visibleDayCount = visibleDayCount,
                            onValueChange = {
                                startDateInput = it
                                startDateError = null
                            },
                            onApply = { applyStartDate() }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        YearHeatMap(
                            checkInDates = checkInDates,
                            startDate = recordStartDate,
                            today = today,
                            selectedDate = selectedDate,
                            multiSelectMode = multiSelectMode,
                            multiSelectedDates = multiSelectedDates,
                            onDateSelected = { date ->
                                if (multiSelectMode) {
                                    multiSelectedDates = if (multiSelectedDates.contains(date)) {
                                        multiSelectedDates - date
                                    } else {
                                        multiSelectedDates + date
                                    }
                                } else {
                                    selectedDate = date
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        BulkActionsPanel(
                            multiSelectMode = multiSelectMode,
                            selectedCount = multiSelectedDates.size,
                            onImportClick = { showImportDialog = true },
                            onToggleMultiSelect = {
                                multiSelectMode = !multiSelectMode
                                multiSelectedDates = emptySet()
                            },
                            onSaveSelected = {
                                val keys = multiSelectedDates.map {
                                    it.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                }.toSet()
                                saveNewDates(checkInDates + keys)
                                selectedDate = multiSelectedDates.maxOrNull() ?: selectedDate
                                multiSelectedDates = emptySet()
                                multiSelectMode = false
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SelectedDatePanel(
                            selectedDate = selectedDate,
                            checked = selectedDateChecked,
                            onAdd = { saveNewDates(checkInDates + selectedDateText) },
                            onDelete = { saveNewDates(checkInDates - selectedDateText) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CheckInStats(checkInDates = visibleDates, today = today)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { exportCheckInData(context, checkInDates) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(text = "导出数据")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showImportDialog) {
        ImportDialog(
            value = importInput,
            error = importError,
            onValueChange = {
                importInput = it
                importError = null
            },
            onDismiss = { showImportDialog = false },
            onImport = { importDates() }
        )
    }
}

@Composable
fun YearHeatMap(
    checkInDates: Set<String>,
    startDate: LocalDate,
    today: LocalDate,
    selectedDate: LocalDate,
    multiSelectMode: Boolean,
    multiSelectedDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val totalDays = ChronoUnit.DAYS.between(startDate, today).toInt() + 1
    val dates = (0 until totalDays).map { startDate.plusDays(it.toLong()) }
    val weeks = dates.chunked(7)
    val heatmapScrollState = rememberScrollState()
    val maxScroll = heatmapScrollState.maxValue
    val monthLabels = weeks.mapIndexed { index, week ->
        val labelDate = when {
            index == 0 -> week.first()
            else -> week.firstOrNull { it.dayOfMonth == 1 }
        }
        labelDate?.let { "${it.monthValue}月" } ?: ""
    }

    LaunchedEffect(totalDays, maxScroll) {
        if (maxScroll > 0) {
            heatmapScrollState.scrollTo(maxScroll)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(heatmapScrollState)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            monthLabels.forEach { label ->
                Box(modifier = Modifier.size(width = 20.dp, height = 18.dp)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF999999)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            weeks.forEach { week ->
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    week.forEach { date ->
                        val dateText = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        CheckInDayBox(
                            checked = checkInDates.contains(dateText),
                            isToday = date == today,
                            isSelected = date == selectedDate,
                            isMultiSelected = multiSelectedDates.contains(date),
                            onClick = { onDateSelected(date) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CheckInDayBox(
    checked: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    isMultiSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (checked) Color(0xFF8AD0A6) else Color(0xFFEFEFEF)
    val borderModifier = when {
        isMultiSelected -> Modifier.border(2.dp, Color(0xFF1976D2), RoundedCornerShape(4.dp))
        isSelected -> Modifier.border(2.dp, Color(0xFF222222), RoundedCornerShape(4.dp))
        isToday -> Modifier.border(1.4.dp, Color(0xFF20A65B), RoundedCornerShape(4.dp))
        else -> Modifier
    }

    Box(
        modifier = Modifier
            .size(20.dp)
            .background(bg, RoundedCornerShape(4.dp))
            .then(borderModifier)
            .clickable(onClick = onClick)
    )
}

@Composable
fun StartDateEditor(
    value: String,
    error: String?,
    visibleDayCount: Int,
    onValueChange: (String) -> Unit,
    onApply: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "记录开始日期",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                label = { Text("yyyy-MM-dd") },
                isError = error != null,
                supportingText = {
                    Text(error ?: "当前显示 $visibleDayCount 天")
                },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onApply,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(52.dp)
            ) {
                Text("应用")
            }
        }
    }
}

@Composable
fun BulkActionsPanel(
    multiSelectMode: Boolean,
    selectedCount: Int,
    onImportClick: () -> Unit,
    onToggleMultiSelect: () -> Unit,
    onSaveSelected: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onImportClick,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
            ) {
                Text("导入数据")
            }
            OutlinedButton(
                onClick = onToggleMultiSelect,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
            ) {
                Text(if (multiSelectMode) "退出多选" else "多选补打卡")
            }
        }

        if (multiSelectMode) {
            Text(
                text = "已选择 $selectedCount 天。蓝色边框表示待补打卡日期。",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF555555)
            )
            Button(
                onClick = onSaveSelected,
                enabled = selectedCount > 0,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text("保存所选日期")
            }
        }
    }
}

@Composable
fun ImportDialog(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onImport: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("导入打卡日期") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "支持输入：2025年6月1,3,5,6,7日，也支持 2025-06-01, 2025-06-03。",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF555555)
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    minLines = 3,
                    isError = error != null,
                    supportingText = { if (error != null) Text(error) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onImport) {
                Text("导入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun SelectedDatePanel(
    selectedDate: LocalDate,
    checked: Boolean,
    onAdd: () -> Unit,
    onDelete: () -> Unit
) {
    val displayFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8F7))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = selectedDate.format(displayFormatter),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF222222)
                    )
                    Text(
                        text = if (checked) "这一天已打卡" else "这一天未打卡",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAdd,
                    enabled = !checked,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF33B56F),
                        disabledContainerColor = Color(0xFFB9DCC7)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text(text = "补打卡")
                }

                OutlinedButton(
                    onClick = onDelete,
                    enabled = checked,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text(text = "删除")
                }
            }
        }
    }
}

@Composable
fun CheckInStats(
    checkInDates: List<LocalDate>,
    today: LocalDate
) {
    val lastCheckIn = checkInDates.lastOrNull()
    val daysSinceLast = lastCheckIn?.let { ChronoUnit.DAYS.between(it, today) }
    val intervals = checkInDates.zipWithNext { a, b -> ChronoUnit.DAYS.between(a, b) }
    val averageInterval = intervals.takeIf { it.isNotEmpty() }?.average()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "最近一次：${lastCheckIn?.toString() ?: "暂无"}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF444444)
        )
        Text(
            text = when (daysSinceLast) {
                null -> "距离上次：暂无数据"
                0L -> "距离上次：今天"
                else -> "距离上次：${daysSinceLast} 天"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF444444)
        )
        Text(
            text = if (averageInterval == null) {
                "平均间隔：暂无数据"
            } else {
                "平均间隔：约 %.1f 天".format(averageInterval)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF444444)
        )
    }
}

private fun filterDatesSince(rawDates: Set<String>, startDate: LocalDate, today: LocalDate): List<LocalDate> {
    return rawDates
        .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }
        .filter { !it.isBefore(startDate) && !it.isAfter(today) }
        .sorted()
}

private fun parseImportedDates(input: String, defaultYear: Int): Set<LocalDate> {
    val normalized = input
        .replace("，", ",")
        .replace("、", ",")
        .replace("；", ";")
    val dates = mutableSetOf<LocalDate>()

    Regex("""\d{4}-\d{1,2}-\d{1,2}""").findAll(normalized).forEach { match ->
        runCatching { LocalDate.parse(match.value, DateTimeFormatter.ofPattern("yyyy-M-d")) }
            .getOrNull()
            ?.let { dates.add(it) }
    }

    Regex("""(?:(\d{4})年)?(\d{1,2})月([\d,\s]+)日?""").findAll(normalized).forEach { match ->
        val year = match.groupValues[1].takeIf { it.isNotBlank() }?.toIntOrNull() ?: defaultYear
        val month = match.groupValues[2].toIntOrNull() ?: return@forEach
        match.groupValues[3]
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { dayText ->
                val day = dayText.toIntOrNull()
                if (day != null) {
                    runCatching { LocalDate.of(year, month, day) }
                        .getOrNull()
                        ?.let { dates.add(it) }
                }
            }
    }

    return dates
}

private fun exportCheckInData(context: Context, rawDates: Set<String>) {
    val dates = rawDates
        .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }
        .sorted()

    val csvText = buildString {
        appendLine("date")
        dates.forEach { appendLine(it.format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    }

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_SUBJECT, "简单打卡数据导出")
        putExtra(Intent.EXTRA_TEXT, csvText)
    }

    val chooser = Intent.createChooser(sendIntent, "导出打卡数据")
    runCatching { context.startActivity(chooser) }
}
