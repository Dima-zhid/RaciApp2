package com.example.raceapp2.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.raceapp2.ui.theme.RaceApp2Theme
import androidx.compose.foundation.lazy.items

// ---------- Models ----------
data class ProjectItem(
    val id: String,
    val name: String,
    val taskCount: Int,
    val ownerName: String? = null,
    val participants: List<String> = emptyList()
)



// ---------- Dialogs ----------
@Composable
fun AddProjectDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (name: String, taskType: String, owner: String) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var selectedTaskType by remember { mutableStateOf("") }
    var projectOwner by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Добавить новый проект") },
        text = {
            Column {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Краткое название проекта") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = selectedTaskType,
                    onValueChange = { selectedTaskType = it },
                    label = { Text("Тип задачи") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = projectOwner,
                    onValueChange = { projectOwner = it },
                    label = { Text("Ответственный/Владелец") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(projectName, selectedTaskType, projectOwner)
            }) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Отмена") }
        }
    )
}

@Composable
fun EditParticipantsDialog(
    project: ProjectItem,
    onDismissRequest: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    var participants by remember { mutableStateOf(project.participants.toMutableList()) }
    var newParticipant by remember { mutableStateOf("") }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Участники проекта") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Фиксированный заголовок
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(8.dp)
                        .horizontalScroll(horizontalScrollState),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Имя участника",
                        modifier = Modifier.width(200.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // место для иконок
                }

                // Основной список с прокруткой по вертикали и горизонтали
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState)
                ) {
                    Column {
                        participants.forEachIndexed { index, participant ->
                            var isEditing by remember { mutableStateOf(false) }
                            var editValue by remember { mutableStateOf(participant) }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = editValue,
                                            onValueChange = { editValue = it },
                                            modifier = Modifier.width(200.dp),
                                            singleLine = true
                                        )
                                        IconButton(onClick = {
                                            if (editValue.trim().isNotEmpty()) {
                                                participants[index] = editValue.trim()
                                                isEditing = false
                                            }
                                        }) {
                                            Icon(Icons.Filled.Check, contentDescription = "Сохранить")
                                        }
                                    } else {
                                        Text(
                                            text = participant,
                                            modifier = Modifier.width(200.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        IconButton(onClick = { isEditing = true }) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Редактировать участника")
                                        }
                                    }
                                    IconButton(onClick = { participants.removeAt(index) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Удалить участника")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Добавление нового участника
                Text("Добавить нового участника:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = newParticipant,
                    onValueChange = { newParticipant = it },
                    label = { Text("Имя участника") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val trimmed = newParticipant.trim()
                            if (trimmed.isNotEmpty() && !participants.contains(trimmed)) {
                                participants.add(trimmed)
                                newParticipant = ""
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Добавить участника")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(participants) }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun EditProjectDialog(
    project: ProjectItem,
    onDismissRequest: () -> Unit,
    onConfirm: (ProjectItem) -> Unit
) {
    val (baseName, typeFromName) = remember(project.name) {
        val match = "\\(([^)]+)\\)$".toRegex().find(project.name)
        if (match != null) {
            val type = match.groupValues[1]
            val name = project.name.substringBeforeLast(" ($type)").trim()
            name to type
        } else {
            project.name to ""
        }
    }
    var projectName by remember { mutableStateOf(baseName) }
    var selectedTaskType by remember { mutableStateOf(typeFromName) }
    var projectOwner by remember { mutableStateOf(project.ownerName ?: "") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Редактировать проект") },
        text = {
            Column {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Название проекта") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = selectedTaskType,
                    onValueChange = { selectedTaskType = it },
                    label = { Text("Тип задачи") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = projectOwner,
                    onValueChange = { projectOwner = it },
                    label = { Text("Ответственный/Владелец") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (projectName.isNotBlank() && projectOwner.isNotBlank()) {
                    onConfirm(
                        project.copy(
                            name = if (selectedTaskType.isNotEmpty()) "$projectName ($selectedTaskType)" else projectName,
                            ownerName = projectOwner
                        )
                    )
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Отмена") }
        }
    )
}

@Composable
fun DeleteConfirmDialog(
    project: ProjectItem,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Подтверждение удаления") },
        text = { Text("Вы уверены, что хотите удалить проект '${project.name}'?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Удалить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Отмена") }
        }
    )
}

// ---------- DashboardScreen ----------
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DashboardScreen(
    projects: List<ProjectItem>,
    userRole: UserRole,
    onProjectClick: (ProjectItem) -> Unit,
    onParticipantsClick: (ProjectItem) -> Unit,
    onAddProjectClick: () -> Unit,
    onEditProjectRequest: (ProjectItem) -> Unit,
    onDeleteProjectRequest: (ProjectItem) -> Unit,
    onLogoutClick: () -> Unit,
    onAdminActionClick: () -> Unit,
    isLoading: Boolean,
    showAddDialog: Boolean,
    onDismissAddDialog: () -> Unit,
    onConfirmAddProject: (name: String, taskType: String, owner: String) -> Unit,
    projectToEdit: ProjectItem?,
    onDismissEditDialog: () -> Unit,
    onConfirmEditProject: (ProjectItem) -> Unit,
    projectToDelete: ProjectItem?,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDeleteProject: (ProjectItem) -> Unit
) {
    var editingParticipantsFor by remember { mutableStateOf<ProjectItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (userRole == UserRole.ADMIN) "Панель администратора" else "Мои проекты") },
                actions = {
                    if (userRole == UserRole.ADMIN) {
                        IconButton(onClick = onAdminActionClick) {
                            Icon(Icons.Filled.Settings, contentDescription = "Админ. настройки")
                        }
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Выйти")
                    }
                }
            )
        },
        floatingActionButton = {
            if (userRole == UserRole.ADMIN) {
                FloatingActionButton(onClick = onAddProjectClick) {
                    Icon(Icons.Filled.Add, contentDescription = "Создать/Добавить проект")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                projects.isEmpty() -> Text("Список проектов пуст", modifier = Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(projects, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            userRole = userRole,
                            onClick = { onProjectClick(project) },
                            onParticipantsClick = { editingParticipantsFor = project },
                            onEditClick = { onEditProjectRequest(project) },
                            onDeleteClick = { onDeleteProjectRequest(project) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog && userRole == UserRole.ADMIN) {
        AddProjectDialog(
            onDismissRequest = onDismissAddDialog,
            onConfirm = onConfirmAddProject
        )
    }

    projectToEdit?.let { project ->
        if (userRole == UserRole.ADMIN) {
            EditProjectDialog(
                project = project,
                onDismissRequest = onDismissEditDialog,
                onConfirm = onConfirmEditProject
            )
        }
    }

    projectToDelete?.let { project ->
        if (userRole == UserRole.ADMIN) {
            DeleteConfirmDialog(
                project = project,
                onDismissRequest = onDismissDeleteDialog,
                onConfirm = { onConfirmDeleteProject(project) }
            )
        }
    }

    editingParticipantsFor?.let { project ->
        if (userRole == UserRole.ADMIN) {
            EditParticipantsDialog(
                project = project,
                onDismissRequest = { editingParticipantsFor = null },
                onConfirm = { updatedList ->
                    val updatedProject = project.copy(participants = updatedList)
                    onConfirmEditProject(updatedProject)
                    editingParticipantsFor = null
                }
            )
        }
    }
}

@Composable
fun ProjectCard(
    project: ProjectItem,
    userRole: UserRole,
    onClick: () -> Unit,
    onParticipantsClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = project.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                if (userRole == UserRole.ADMIN && project.ownerName != null) {
                    Text(text = "Владелец: ${project.ownerName}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (userRole == UserRole.ADMIN && project.participants.isNotEmpty()) {
                    Text(text = "Участники: ${project.participants.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (userRole == UserRole.ADMIN) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Filled.Edit, contentDescription = "Редактировать проект")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = "Удалить проект")
                }
                TextButton(
                    onClick = onParticipantsClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Участники")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    RaceApp2Theme {
        val sampleProjects = listOf(
            ProjectItem("1", "Проект А (Разработка)", 4, "Иванов", listOf("Анна", "Сергей", "Мария", "Алексей", "Елена")),
            ProjectItem("2", "Проект Б (Тестирование)", 7, "Петров", listOf("Мария"))
        )
        DashboardScreen(
            projects = sampleProjects,
            userRole = UserRole.ADMIN,
            onProjectClick = {},
            onParticipantsClick = {},
            onAddProjectClick = {},
            onEditProjectRequest = {},
            onDeleteProjectRequest = {},
            onLogoutClick = {},
            onAdminActionClick = {},
            isLoading = false,
            showAddDialog = false,
            onDismissAddDialog = {},
            onConfirmAddProject = { _, _, _ -> },
            projectToEdit = null,
            onDismissEditDialog = {},
            onConfirmEditProject = {},
            projectToDelete = null,
            onDismissDeleteDialog = {},
            onConfirmDeleteProject = {}
        )
    }
}
