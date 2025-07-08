package com.example.raceapp2.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.raceapp2.ui.screens.dashboard.UserRole

// Добавил поле positions для списка должностей
data class Character(
    val id: String,
    val login: String,
    val phone: String?, // можно оставить, но не используем
    val description: String,
    val positions: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    characters: List<Character>,
    currentUserRole: UserRole,
    onBack: () -> Unit,
    onUpdateCharacter: (Character) -> Unit,
    onAddCharacter: (Character) -> Unit
) {
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (selectedCharacter != null) {
        CharacterDetailScreen(
            character = selectedCharacter!!,
            isEditable = currentUserRole == UserRole.ADMIN,
            onBack = { selectedCharacter = null },
            onSave = {
                onUpdateCharacter(it)
                selectedCharacter = null
            }
        )
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Настройки пользователей") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (currentUserRole == UserRole.ADMIN) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Добавить пользователя")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(characters) { character ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCharacter = character },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Логин: ${character.login}", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = character.description, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Должности: ${character.positions.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CharacterEditDialog(
            initialCharacter = Character(id = "", login = "", phone = "", description = "", positions = listOf()),
            onDismiss = { showAddDialog = false },
            onConfirm = {
                onAddCharacter(it.copy(id = java.util.UUID.randomUUID().toString()))
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    character: Character,
    isEditable: Boolean,
    onBack: () -> Unit,
    onSave: (Character) -> Unit
) {
    var login by remember { mutableStateOf(character.login) }
    var description by remember { mutableStateOf(character.description) }
    var positions by remember { mutableStateOf(character.positions.toMutableList()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Данные сотрудника") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (isEditable) {
                        TextButton(onClick = {
                            val filteredPositions = positions.filter { it.isNotBlank() }
                            onSave(character.copy(login = login, description = description, positions = filteredPositions))
                        }) {
                            Text("Сохранить")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isEditable) {
                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = { Text("Логин") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
                Text("Должности", style = MaterialTheme.typography.titleMedium)
                PositionsEditor(
                    positions = positions,
                    onPositionsChange = { positions = it.toMutableList() }
                )
            } else {
                Text(text = "Логин: $login", style = MaterialTheme.typography.titleMedium)
                Text(text = "Описание: $description", style = MaterialTheme.typography.bodySmall)
                Text(text = "Должности: ${positions.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterEditDialog(
    initialCharacter: Character,
    onDismiss: () -> Unit,
    onConfirm: (Character) -> Unit
) {
    var login by remember { mutableStateOf(initialCharacter.login) }
    var description by remember { mutableStateOf(initialCharacter.description) }
    var positions by remember { mutableStateOf(initialCharacter.positions.toMutableList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить пользователя") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = { Text("Логин") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
                Text("Должности", style = MaterialTheme.typography.titleMedium)
                PositionsEditor(
                    positions = positions,
                    onPositionsChange = { positions = it.toMutableList() }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val filteredPositions = positions.filter { it.isNotBlank() }
                if (login.isNotBlank() && filteredPositions.isNotEmpty()) {
                    onConfirm(
                        initialCharacter.copy(
                            login = login,
                            description = description,
                            positions = filteredPositions
                        )
                    )
                }
            }) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

// Компонент для редактирования списка должностей с возможностью добавлять и удалять
@Composable
fun PositionsEditor(
    positions: List<String>,
    onPositionsChange: (List<String>) -> Unit
) {
    var localPositions by remember { mutableStateOf(positions) }  // иммутабельный список

    Column {
        localPositions.forEachIndexed { index, pos ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = pos,
                    onValueChange = { newValue ->
                        val newList = localPositions.toMutableList()
                        newList[index] = newValue
                        localPositions = newList
                        onPositionsChange(localPositions)
                    },
                    label = { Text("Должность ${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    val newList = localPositions.toMutableList()
                    newList.removeAt(index)
                    localPositions = newList
                    onPositionsChange(localPositions)
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Удалить должность")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val newList = localPositions.toMutableList()
            newList.add("")
            localPositions = newList
            onPositionsChange(localPositions)
        }) {
            Icon(Icons.Filled.Add, contentDescription = "Добавить должность")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Добавить должность")
        }
    }
}
