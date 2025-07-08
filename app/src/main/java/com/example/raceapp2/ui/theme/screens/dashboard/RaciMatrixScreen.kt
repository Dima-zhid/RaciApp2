package com.example.raceapp2.ui.screens.raci

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.raceapp2.ui.screens.dashboard.ProjectItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaciMatrixScreen(
    project: ProjectItem,
    participants: List<String>,
    onBack: () -> Unit,
    onSave: (raciData: RaciData) -> Unit
) {
    var taskName by remember { mutableStateOf(TextFieldValue(project.name)) }
    var positions by remember { mutableStateOf(listOf("Руководитель", "Разработчик")) }
    var stages by remember { mutableStateOf(listOf("Этап 1", "Этап 2")) }

    val raciValues = remember {
        mutableStateMapOf<Pair<Int, Int>, String>().apply {
            stages.forEachIndexed { row, _ ->
                positions.forEachIndexed { col, _ ->
                    put(row to col, "")
                }
            }
        }
    }

    val horizontalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Скролл всей страницы
            .padding(16.dp)
    ) {
        Text("Редактирование RACI-матрицы", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        // Таблица с горизонтальной прокруткой
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.dp)
                .background(Color(0xFFF5F5F5))
                .padding(8.dp)
                .horizontalScroll(horizontalScroll) // Только горизонтальная прокрутка
        ) {
            Row {
                Column {
                    // Заголовок: должности по горизонтали
                    Row {
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Этапы", style = MaterialTheme.typography.titleMedium)
                        }

                        positions.forEach { position ->
                            Box(
                                modifier = Modifier
                                    .width(70.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(position, textAlign = TextAlign.Center)
                            }
                        }
                    }

                    // Строки: по этапам
                    stages.forEachIndexed { rowIndex, stage ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(stage)
                            }

                            positions.forEachIndexed { colIndex, _ ->
                                val key = rowIndex to colIndex
                                var value by remember { mutableStateOf(raciValues[key] ?: "") }

                                OutlinedTextField(
                                    value = value,
                                    onValueChange = {
                                        val upper = it.uppercase()
                                        if (upper in listOf("", "R", "A", "C", "I")) {
                                            value = upper
                                            raciValues[key] = upper
                                        }
                                    },
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(60.dp)
                                        .padding(4.dp),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Название задачи/проекта") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text("Должности", style = MaterialTheme.typography.titleMedium)
        positions.forEachIndexed { index, position ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = position,
                    onValueChange = { newValue ->
                        positions = positions.toMutableList().apply { this[index] = newValue }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Должность ${index + 1}") }
                )
                IconButton(onClick = {
                    positions = positions.toMutableList().apply { removeAt(index) }
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Удалить должность")
                }
            }
        }
        Button(onClick = { positions = positions + "Новая должность" }) {
            Icon(Icons.Filled.Add, contentDescription = "Добавить должность")
            Spacer(Modifier.width(8.dp))
            Text("Добавить должность")
        }

        Spacer(Modifier.height(16.dp))

        Text("Этапы", style = MaterialTheme.typography.titleMedium)
        stages.forEachIndexed { index, stage ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = stage,
                    onValueChange = { newValue ->
                        stages = stages.toMutableList().apply { this[index] = newValue }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Этап ${index + 1}") }
                )
                IconButton(onClick = {
                    stages = stages.toMutableList().apply { removeAt(index) }
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Удалить этап")
                }
            }
        }
        Button(onClick = { stages = stages + "Новый этап" }) {
            Icon(Icons.Filled.Add, contentDescription = "Добавить этап")
            Spacer(Modifier.width(8.dp))
            Text("Добавить этап")
        }

        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = {
                    val raciData = RaciData(
                        projectName = taskName.text,
                        positions = positions,
                        stages = stages,
                        participants = participants,
                        matrix = raciValues.toMap()
                    )
                    onSave(raciData)
                }
            ) {
                Text("Сохранить и вернуться")
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = onBack) {
                Text("Отмена")
            }
        }
    }
}

data class RaciData(
    val projectName: String,
    val positions: List<String>,
    val stages: List<String>,
    val participants: List<String>,
    val matrix: Map<Pair<Int, Int>, String>
)
