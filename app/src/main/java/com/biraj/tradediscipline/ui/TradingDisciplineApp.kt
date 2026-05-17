package com.biraj.tradediscipline.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.biraj.tradediscipline.DisciplineUiState
import com.biraj.tradediscipline.DisciplineViewModel
import com.biraj.tradediscipline.data.AppMode
import com.biraj.tradediscipline.data.EmotionalCondition
import com.biraj.tradediscipline.data.EntryDiscipline
import com.biraj.tradediscipline.data.ExitDiscipline
import com.biraj.tradediscipline.data.PermissionAction
import com.biraj.tradediscipline.data.TradeIntentEntity
import com.biraj.tradediscipline.data.TradeReason
import com.biraj.tradediscipline.data.TradeReviewEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradingDisciplineApp(viewModel: DisciplineViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        val message = state.message
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Discipline Incharge", fontWeight = FontWeight.Bold)
                        Text(state.istStamp, style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { TodayCard(state) }
            item {
                when (state.mode) {
                    AppMode.PRE_MARKET -> PreMarketScreen(state, viewModel)
                    AppMode.LIVE_TRADING -> LiveTradingScreen(state, viewModel)
                    AppMode.POST_MARKET -> PostMarketScreen(state, viewModel)
                    AppMode.REST -> RestScreen(state)
                    AppMode.WEEKEND_CLOSED -> WeekendScreen(state)
                }
            }
            item { RecentIntentSection(state.intents) }
            item { RecentReviewSection(state.reviews) }
        }
    }
}

@Composable
private fun TodayCard(state: DisciplineUiState) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(state.modeTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(state.modeSubtitle, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip("Date: ${state.tradingDate}")
                InfoChip("Trades: ${state.intents.size}")
            }
            val plan = state.today
            if (plan == null || !plan.marketPlanDone) {
                Text("Morning plan not completed.", color = MaterialTheme.colorScheme.error)
            } else {
                Text(
                    "Plan: ${plan.allowedInstrument} | Max trades ${plan.maxTrades} | Max loss ₹${plan.maxDailyLoss.toInt()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            PermissionResultCard(state.lastPermission)
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        tonalElevation = 2.dp
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun PermissionResultCard(result: com.biraj.tradediscipline.logic.DisciplineRulesEngine.PermissionResult?) {
    if (result == null) return
    val label = when (result.action) {
        PermissionAction.ALLOWED -> "ALLOWED"
        PermissionAction.CAUTION -> "CAUTION"
        PermissionAction.WAIT -> "WAIT"
        PermissionAction.BLOCKED -> "BLOCKED"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(label, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
            Text(result.message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PreMarketScreen(state: DisciplineUiState, viewModel: DisciplineViewModel) {
    var planDone by rememberSaveable { mutableStateOf(state.today?.marketPlanDone ?: true) }
    var instrument by rememberSaveable { mutableStateOf(state.today?.allowedInstrument ?: "NIFTY") }
    var maxTrades by rememberSaveable { mutableStateOf((state.today?.maxTrades ?: 3).toString()) }
    var maxLoss by rememberSaveable { mutableStateOf((state.today?.maxDailyLoss ?: 0.0).toInt().toString()) }
    var mood by rememberSaveable { mutableStateOf(state.today?.moodLabel ?: "Normal") }
    var notes by rememberSaveable { mutableStateOf(state.today?.notes ?: "") }

    SectionCard(title = "Morning Permission Setup") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Market plan ready", modifier = Modifier.weight(1f))
            Switch(checked = planDone, onCheckedChange = { planDone = it })
        }
        SimpleDropdown(
            label = "Allowed instrument",
            value = instrument,
            options = listOf("NIFTY", "SENSEX", "NIFTY + SENSEX", "NO TRADE")
        ) { instrument = it }
        SimpleDropdown(
            label = "Mood / focus",
            value = mood,
            options = listOf("Calm", "Normal", "Tired", "Distracted", "Emotional", "Not fit to trade")
        ) { mood = it }
        OutlinedTextField(
            value = maxTrades,
            onValueChange = { maxTrades = it.filter(Char::isDigit).take(2) },
            label = { Text("Max trades") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = maxLoss,
            onValueChange = { maxLoss = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Max daily loss ₹") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Short day note") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Button(
            onClick = {
                viewModel.saveMorningPlan(
                    planDone = planDone,
                    allowedInstrument = instrument,
                    maxTrades = maxTrades.toIntOrNull() ?: 3,
                    maxLoss = maxLoss.toDoubleOrNull() ?: 0.0,
                    moodLabel = mood,
                    notes = notes
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Morning Plan")
        }
    }
}

@Composable
private fun LiveTradingScreen(state: DisciplineUiState, viewModel: DisciplineViewModel) {
    var emotion by rememberSaveable { mutableStateOf(EmotionalCondition.CALM) }
    var currentPnl by rememberSaveable { mutableStateOf("") }
    var tradesTaken by rememberSaveable { mutableStateOf(state.intents.size.coerceAtMost(5).toString()) }
    var reason by rememberSaveable { mutableStateOf(TradeReason.PULLBACK_ENTRY) }

    SectionCard(title = "Fast Trade Permission") {
        EnumDropdown("Emotional condition", emotion, EmotionalCondition.entries, { it.label }) { emotion = it }
        OutlinedTextField(
            value = currentPnl,
            onValueChange = { value -> currentPnl = value.filter { it.isDigit() || it == '-' || it == '.' } },
            label = { Text("Current day P&L ₹") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        SimpleDropdown(
            label = "Trades already taken",
            value = tradesTaken,
            options = listOf("0", "1", "2", "3", "4", "5")
        ) { tradesTaken = it }
        EnumDropdown("Why am I taking this trade?", reason, TradeReason.entries, { it.label }) { reason = it }
        Button(
            onClick = {
                viewModel.checkTradePermission(
                    emotionalCondition = emotion,
                    currentPnl = currentPnl.toDoubleOrNull() ?: 0.0,
                    tradesTaken = tradesTaken.toIntOrNull() ?: 0,
                    tradeReason = reason
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check Permission")
        }
        Text(
            "Only 4 live inputs are used: emotion, current P&L, trade count, and reason.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun PostMarketScreen(state: DisciplineUiState, viewModel: DisciplineViewModel) {
    var pnl by rememberSaveable { mutableStateOf("") }
    var followedPlan by rememberSaveable { mutableStateOf(true) }
    var entry by rememberSaveable { mutableStateOf(EntryDiscipline.GOOD) }
    var exit by rememberSaveable { mutableStateOf(ExitDiscipline.GOOD) }
    var mistakeTags by rememberSaveable { mutableStateOf("") }
    var lesson by rememberSaveable { mutableStateOf("") }

    SectionCard(title = "Post Trade / Day Review") {
        OutlinedTextField(
            value = pnl,
            onValueChange = { value -> pnl = value.filter { it.isDigit() || it == '-' || it == '.' } },
            label = { Text("Trade/day P&L ₹") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Followed plan", modifier = Modifier.weight(1f))
            Switch(checked = followedPlan, onCheckedChange = { followedPlan = it })
        }
        EnumDropdown("Entry discipline", entry, EntryDiscipline.entries, { it.label }) { entry = it }
        EnumDropdown("Exit discipline", exit, ExitDiscipline.entries, { it.label }) { exit = it }
        OutlinedTextField(
            value = mistakeTags,
            onValueChange = { mistakeTags = it },
            label = { Text("Mistake tags") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lesson,
            onValueChange = { lesson = it },
            label = { Text("Lesson") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Button(
            onClick = {
                viewModel.savePostTradeReview(
                    pnl = pnl.toDoubleOrNull() ?: 0.0,
                    followedPlan = followedPlan,
                    entryDiscipline = entry,
                    exitDiscipline = exit,
                    mistakeTags = mistakeTags,
                    lesson = lesson
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Review")
        }
    }
}

@Composable
private fun RestScreen(state: DisciplineUiState) {
    SectionCard(title = "Rest Mode") {
        Text("Live trading permission is disabled before 5:00 AM IST. You can review history after more records are added.")
    }
}

@Composable
private fun WeekendScreen(state: DisciplineUiState) {
    SectionCard(title = "Weekend") {
        Text("Saturday/Sunday live trading mode is disabled. This screen is for review and planning only.")
    }
}

@Composable
private fun RecentIntentSection(intents: List<TradeIntentEntity>) {
    SectionCard(title = "Recent Permission Checks") {
        if (intents.isEmpty()) {
            Text("No permission checks saved today.")
        } else {
            intents.take(5).forEach { intent ->
                CompactLogCard(
                    title = intent.permissionAction,
                    subtitle = "${intent.emotionalCondition} | ₹${intent.currentPnl.toInt()} | ${intent.tradesTaken} trades",
                    body = "Reason: ${intent.tradeReason}\n${intent.permissionMessage}"
                )
            }
        }
    }
}

@Composable
private fun RecentReviewSection(reviews: List<TradeReviewEntity>) {
    SectionCard(title = "Recent Reviews") {
        if (reviews.isEmpty()) {
            Text("No reviews saved today.")
        } else {
            reviews.take(5).forEach { review ->
                CompactLogCard(
                    title = if (review.followedPlan) "Plan Followed" else "Plan Broken",
                    subtitle = "P&L ₹${review.pnl.toInt()} | Entry ${review.entryDiscipline} | Exit ${review.exitDiscipline}",
                    body = listOf(review.mistakeTags, review.lesson).filter { it.isNotBlank() }.joinToString("\n")
                )
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable Column.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun CompactLogCard(title: String, subtitle: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
            if (body.isNotBlank()) Text(body, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SimpleDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(value, modifier = Modifier.weight(1f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun <T> EnumDropdown(
    label: String,
    value: T,
    options: List<T>,
    labelProvider: (T) -> String,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(labelProvider(value), modifier = Modifier.weight(1f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(labelProvider(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
