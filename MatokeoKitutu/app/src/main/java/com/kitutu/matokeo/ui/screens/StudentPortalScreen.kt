package com.kitutu.matokeo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kitutu.matokeo.data.SUBJECTS
import com.kitutu.matokeo.data.StudentEntity
import com.kitutu.matokeo.data.gradeLetter
import com.kitutu.matokeo.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentPortalScreen(navController: NavController, viewModel: AppViewModel) {
    val students by viewModel.students.collectAsState()
    var tab by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    val tabs = listOf("Yote") + SUBJECTS

    val filtered = students.filter {
        query.isBlank() || it.fullName.contains(query, ignoreCase = true) ||
            it.examNumber.contains(query, ignoreCase = true)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Matokeo ya Wanafunzi") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                }
            }
        )
    }) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Tafuta kwa jina au namba") },
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            )
            ScrollableTabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { i, t ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(t) })
                }
            }
            if (tab == 0) OverallList(filtered) else SubjectList(filtered, SUBJECTS[tab - 1])
        }
    }
}

@Composable
private fun OverallList(students: List<StudentEntity>) {
    val sorted = students.sortedByDescending { it.average() }
    if (sorted.isEmpty()) {
        EmptyState("Hakuna wanafunzi bado.")
        return
    }
    LazyColumn(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(sorted, key = { _, s -> s.id }) { i, s ->
            ResultRow(rank = i + 1, name = s.fullName, subtitle = s.school, value = s.average())
        }
    }
}

@Composable
private fun SubjectList(students: List<StudentEntity>, subject: String) {
    val sorted = students.filter { it.hasScore(subject) }
        .sortedByDescending { it.scores[subject] ?: 0 }
    if (sorted.isEmpty()) {
        EmptyState("Hakuna alama bado kwa $subject.")
        return
    }
    LazyColumn(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(sorted, key = { _, s -> s.id }) { i, s ->
            ResultRow(rank = i + 1, name = s.fullName, subtitle = s.school, value = (s.scores[subject] ?: 0).toDouble())
        }
    }
}

@Composable
internal fun ResultRow(rank: Int, name: String, subtitle: String, value: Double) {
    ElevatedCard {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.secondaryContainer) {
                    Text("$rank", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(name, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(gradeLetter(value), fontWeight = FontWeight.Bold)
                Text(String.format("%.1f", value), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
internal fun EmptyState(text: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
