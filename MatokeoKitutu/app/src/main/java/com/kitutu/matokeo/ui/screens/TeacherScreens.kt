package com.kitutu.matokeo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kitutu.matokeo.data.SCHOOLS
import com.kitutu.matokeo.data.SEX_OPTIONS
import com.kitutu.matokeo.data.SUBJECTS
import com.kitutu.matokeo.data.StudentEntity
import com.kitutu.matokeo.ui.AppViewModel
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// CHAGUA SHULE + PASSWORD
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherSchoolsScreen(navController: NavController, viewModel: AppViewModel) {
    var passwordDialogSchool by remember { mutableStateOf<String?>(null) }
    var forcePasswordSchool by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Chagua Shule") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                }
            }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(SCHOOLS) { school ->
                ElevatedCard(
                    onClick = { passwordDialogSchool = school },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(school, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }

    passwordDialogSchool?.let { school ->
        TeacherPasswordDialog(
            school = school,
            viewModel = viewModel,
            onDismiss = { passwordDialogSchool = null },
            onSuccess = { firstLogin ->
                passwordDialogSchool = null
                if (firstLogin) forcePasswordSchool = school
                else navController.navigate("teacher_dashboard/$school")
            }
        )
    }

    forcePasswordSchool?.let { school ->
        ForcePasswordChangeDialog(
            school = school,
            viewModel = viewModel,
            onDone = {
                forcePasswordSchool = null
                navController.navigate("teacher_dashboard/$school")
            }
        )
    }
}

@Composable
private fun TeacherPasswordDialog(
    school: String,
    viewModel: AppViewModel,
    onDismiss: () -> Unit,
    onSuccess: (firstLogin: Boolean) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Password ya $school") },
        text = {
            Column {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; error = false },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                if (error) {
                    Text("Password si sahihi.", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    if (viewModel.verifyTeacherPassword(school, password)) {
                        onSuccess(viewModel.isFirstTeacherLogin(school))
                    } else {
                        error = true
                    }
                }
            }) { Text("Ingia") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Ghairi") } }
    )
}

@Composable
private fun ForcePasswordChangeDialog(school: String, viewModel: AppViewModel, onDone: () -> Unit) {
    var p1 by remember { mutableStateOf("") }
    var p2 by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { /* lazima abadili password kabla ya kuendelea */ },
        title = { Text("Weka Password Mpya") },
        text = {
            Column {
                Text("Hii ni mara yako ya kwanza kuingia (au password imewekwa upya na Admin). Weka password mpya kabla ya kuendelea.")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = p1,
                    onValueChange = { p1 = it; error = false },
                    label = { Text("Password Mpya") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = p2,
                    onValueChange = { p2 = it; error = false },
                    label = { Text("Rudia Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                if (error) {
                    Text("Password hazifanani au ni fupi mno (angalau herufi 4).", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (p1.length >= 4 && p1 == p2) {
                    viewModel.setTeacherPassword(school, p1)
                    onDone()
                } else {
                    error = true
                }
            }) { Text("Endelea") }
        }
    )
}

// ---------------------------------------------------------------------------
// DASHIBODI YA MWALIMU (Wanafunzi + Jaza Matokeo)
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(navController: NavController, viewModel: AppViewModel, school: String) {
    val students by viewModel.students.collectAsState()
    val locked by viewModel.locked.collectAsState()
    val roster = students.filter { it.school == school }
        .sortedWith(compareBy { it.examNumber })
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(school) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                    }
                }
            )
        },
        floatingActionButton = {
            if (tab == 0 && !locked) {
                FloatingActionButton(onClick = { navController.navigate("roster_form/$school") }) {
                    Icon(Icons.Default.Edit, contentDescription = "Ongeza Mwanafunzi")
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (locked) {
                Surface(color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "🔒 Alama zimefungwa na Admin. Huwezi kufanya mabadiliko kwa sasa.",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Wanafunzi (${roster.size})") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Jaza Matokeo") })
            }
            when (tab) {
                0 -> RosterList(navController, viewModel, school, roster, locked)
                else -> FillResultsList(viewModel, roster, locked)
            }
        }
    }
}

@Composable
private fun RosterList(
    navController: NavController,
    viewModel: AppViewModel,
    school: String,
    roster: List<StudentEntity>,
    locked: Boolean
) {
    if (roster.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hakuna wanafunzi bado. Bonyeza + kuandikisha.")
        }
        return
    }
    LazyColumn(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(roster, key = { it.id }) { s ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(s.fullName, style = MaterialTheme.typography.titleSmall)
                        Text(
                            "${s.examNumber} · ${s.sex} · ${s.enteredSubjects().size}/${SUBJECTS.size} masomo",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (!locked) {
                        Row {
                            IconButton(onClick = {
                                navController.navigate("roster_form/$school?studentId=${s.id}")
                            }) { Icon(Icons.Default.Edit, contentDescription = "Hariri") }
                            IconButton(onClick = { viewModel.deleteStudent(s.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Futa")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FillResultsList(viewModel: AppViewModel, roster: List<StudentEntity>, locked: Boolean) {
    var subjectIndex by remember { mutableStateOf(0) }
    val subject = SUBJECTS[subjectIndex]

    if (locked) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("🔒 Alama zimefungwa na Admin kwa sasa.")
        }
        return
    }
    if (roster.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Andikisha wanafunzi kwanza kwenye tab ya Wanafunzi.")
        }
        return
    }

    Column {
        ScrollableTabRow(selectedTabIndex = subjectIndex) {
            SUBJECTS.forEachIndexed { i, s ->
                Tab(selected = subjectIndex == i, onClick = { subjectIndex = i }, text = { Text(s) })
            }
        }
        LazyColumn(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(roster, key = { it.id + subject }) { s ->
                var text by remember(s.id, subject) { mutableStateOf(s.scores[subject]?.toString() ?: "") }
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(s.fullName, style = MaterialTheme.typography.bodyMedium)
                            Text(s.examNumber, style = MaterialTheme.typography.bodySmall)
                        }
                        OutlinedTextField(
                            value = text,
                            onValueChange = { new ->
                                text = new
                                val n = new.toIntOrNull()
                                when {
                                    new.isEmpty() -> viewModel.setScore(s.id, subject, null)
                                    n != null && n in 0..100 -> viewModel.setScore(s.id, subject, n)
                                }
                            },
                            modifier = Modifier.width(90.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Divider()
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// FOMU YA KUANDIKISHA / KUHARIRI MWANAFUNZI
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RosterFormScreen(navController: NavController, viewModel: AppViewModel, school: String, studentId: String?) {
    val students by viewModel.students.collectAsState()
    val existing = studentId?.let { id -> students.find { it.id == id } }

    var firstName by remember { mutableStateOf(existing?.firstName ?: "") }
    var middleName by remember { mutableStateOf(existing?.middleName ?: "") }
    var lastName by remember { mutableStateOf(existing?.lastName ?: "") }
    var examNumber by remember { mutableStateOf(existing?.examNumber ?: "") }
    var sex by remember { mutableStateOf(existing?.sex ?: "") }
    var sexExpanded by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (existing != null) "Hariri Mwanafunzi" else "Andikisha Mwanafunzi") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                }
            }
        )
    }) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = firstName, onValueChange = { firstName = it },
                label = { Text("Jina la Kwanza") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = middleName, onValueChange = { middleName = it },
                label = { Text("Jina la Kati") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lastName, onValueChange = { lastName = it },
                label = { Text("Jina la Mwisho") }, modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(expanded = sexExpanded, onExpandedChange = { sexExpanded = it }) {
                OutlinedTextField(
                    value = sex,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Jinsia") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexExpanded) },
                    modifier = Modifier.fillMaxWidth().widthIn(min = 0.dp).menuAnchor()
                )
                androidx.compose.material3.ExposedDropdownMenu(
                    expanded = sexExpanded,
                    onDismissRequest = { sexExpanded = false }
                ) {
                    SEX_OPTIONS.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { sex = option; sexExpanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = examNumber, onValueChange = { examNumber = it },
                label = { Text("Namba ya Mtihani") }, modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (firstName.isNotBlank() && lastName.isNotBlank() && sex.isNotBlank() && examNumber.isNotBlank()) {
                        viewModel.addOrUpdateRoster(existing?.id, firstName, middleName, lastName, sex, examNumber, school)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Hifadhi") }
        }
    }
}
