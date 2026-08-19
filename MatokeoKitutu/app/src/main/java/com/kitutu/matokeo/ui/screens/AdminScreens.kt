package com.kitutu.matokeo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kitutu.matokeo.data.ArchiveEntity
import com.kitutu.matokeo.data.SCHOOLS
import com.kitutu.matokeo.ui.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(navController: NavController, viewModel: AppViewModel) {
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Admin Login") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                }
            }
        )
    }) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = false },
                label = { Text("Password ya Admin") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            if (error) Text("Password si sahihi.", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    scope.launch {
                        if (viewModel.verifyAdminPassword(password)) {
                            navController.navigate("admin_panel")
                        } else error = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Ingia") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController, viewModel: AppViewModel) {
    val locked by viewModel.locked.collectAsState()
    val archives by viewModel.archives.collectAsState()
    val students by viewModel.students.collectAsState()
    var newAdminPassword by remember { mutableStateOf("") }
    var archiveLabel by remember { mutableStateOf("") }
    var periodLabel by remember { mutableStateOf("") }
    var confirmClearOpen by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Dashibodi ya Admin") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                }
            }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionCard("Udhibiti wa Kujaza Alama") {
                    Text(if (locked) "🔒 Alama zimefungwa kwa sasa." else "🔓 Walimu wanaruhusiwa kujaza alama.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.setLocked(!locked) }) {
                        Text(if (locked) "Ruhusu Kujaza Alama Upya" else "Funga Uingizaji wa Alama")
                    }
                }
            }
            item {
                SectionCard("Badilisha Password ya Admin") {
                    OutlinedTextField(
                        value = newAdminPassword, onValueChange = { newAdminPassword = it },
                        label = { Text("Password Mpya") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        if (newAdminPassword.isNotBlank()) {
                            viewModel.setAdminPassword(newAdminPassword)
                            newAdminPassword = ""
                        }
                    }) { Text("Hifadhi") }
                }
            }
            item {
                SectionCard("Weka Upya Password za Walimu") {
                    SCHOOLS.forEach { school ->
                        TeacherPasswordResetRow(school, viewModel)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            item {
                SectionCard("Kichwa cha Ripoti — Mwezi wa Sasa") {
                    OutlinedTextField(
                        value = periodLabel, onValueChange = { periodLabel = it },
                        label = { Text("Mfano: Julai 2026") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.setPeriodLabel(periodLabel) }) { Text("Hifadhi") }
                }
            }
            item {
                SectionCard("Hifadhi Matokeo kwa Mwezi") {
                    OutlinedTextField(
                        value = archiveLabel, onValueChange = { archiveLabel = it },
                        label = { Text("Mfano: Januari 2026") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (archiveLabel.isNotBlank()) {
                                viewModel.saveArchive(archiveLabel)
                                archiveLabel = ""
                            }
                        },
                        enabled = students.isNotEmpty()
                    ) { Text("Hifadhi Matokeo ya Sasa") }

                    if (archives.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        archives.forEach { archive ->
                            ArchiveRow(navController, viewModel, archive)
                            Divider()
                        }
                    }
                }
            }
            item {
                SectionCard("Futa Alama Zote za Sasa") {
                    Text("Majina na namba za mtihani zitabaki; alama pekee zitafutwa.")
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { confirmClearOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        enabled = students.isNotEmpty()
                    ) { Text("Futa Alama Zote") }
                }
            }
        }
    }

    if (confirmClearOpen) {
        AlertDialog(
            onDismissRequest = { confirmClearOpen = false },
            title = { Text("Uhakika?") },
            text = { Text("Alama za wanafunzi wote zitafutwa. Majina yatabaki.") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearAllScores(); confirmClearOpen = false }) { Text("Futa") }
            },
            dismissButton = { TextButton(onClick = { confirmClearOpen = false }) { Text("Ghairi") } }
        )
    }
}

@Composable
private fun TeacherPasswordResetRow(school: String, viewModel: AppViewModel) {
    var value by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(school, modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = value, onValueChange = { value = it },
            label = { Text("Password Mpya") }, modifier = Modifier.width(150.dp)
        )
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = {
            if (value.isNotBlank()) {
                viewModel.resetTeacherPassword(school, value)
                value = ""
            }
        }) { Text("Weka Upya") }
    }
}

@Composable
private fun ArchiveRow(navController: NavController, viewModel: AppViewModel, archive: ArchiveEntity) {
    var confirmDelete by remember { mutableStateOf(false) }
    var confirmRestore by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text("📁 ${archive.label}", style = MaterialTheme.typography.bodyMedium)
        Row {
            TextButton(onClick = { navController.navigate("archive_detail/${archive.label}") }) { Text("Tazama") }
            TextButton(onClick = { confirmRestore = true }) { Text("Rejesha") }
            TextButton(onClick = { confirmDelete = true }) { Text("Futa Kabisa") }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Futa Hifadhi Kabisa?") },
            text = { Text("Hii itafuta \"${archive.label}\" kabisa. Huwezi kuirejesha tena baadaye.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteArchive(archive.label); confirmDelete = false }) { Text("Futa Kabisa") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Ghairi") } }
        )
    }

    if (confirmRestore) {
        AlertDialog(
            onDismissRequest = { confirmRestore = false },
            title = { Text("Rejesha Hifadhi?") },
            text = { Text("Matokeo ya sasa (ya moja kwa moja) yatabadilishwa na yale ya \"${archive.label}\".") },
            confirmButton = {
                TextButton(onClick = { viewModel.restoreArchive(archive); confirmRestore = false }) { Text("Rejesha") }
            },
            dismissButton = { TextButton(onClick = { confirmRestore = false }) { Text("Ghairi") } }
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveDetailScreen(navController: NavController, viewModel: AppViewModel, label: String) {
    val archives by viewModel.archives.collectAsState()
    val archive = archives.find { it.label == label }
    val list = remember(archive) { archive?.let { viewModel.studentsInArchive(it) } ?: emptyList() }
    val sorted = list.sortedByDescending { it.average() }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(label) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                }
            }
        )
    }) { padding ->
        if (sorted.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Hakuna data kwenye hifadhi hii.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(sorted, key = { _, s -> s.id }) { i, s ->
                    ResultRow(rank = i + 1, name = s.fullName, subtitle = "${s.school} · ${s.examNumber}", value = s.average())
                }
            }
        }
    }
}
