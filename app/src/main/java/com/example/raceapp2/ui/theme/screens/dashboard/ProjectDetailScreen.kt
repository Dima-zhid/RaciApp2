package com.example.raceapp2.ui.screens.dashboard // или com.example.raceapp2.ui.screens.projectdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    navController: NavController,
    projectId: String?, // ID проекта, который нужно отобразить/редактировать
    projectManager: String? // Опционально: Имя менеджера для отображения
) {
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    // Добавьте другие поля формы, которые вам нужны

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали проекта ${projectId ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Форма для проекта ID: ${projectId ?: "Не указан"}", style = MaterialTheme.typography.headlineSmall)
            if (projectManager != null) {
                Text("Менеджер проекта: $projectManager", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Название задачи") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Описание задачи") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Здесь могут быть другие поля формы: выбор исполнителя, даты, статуса и т.д.

            Button(
                onClick = {
                    // Логика сохранения данных формы
                    // Например, можно передать данные в ViewModel или вызвать lambda-функцию
                    // После сохранения можно вернуться назад: navController.popBackStack()
                    // Пока просто выведем в лог или покажем Toast
                    println("Сохранение данных для проекта $projectId: Задача - $taskName, Описание - $taskDescription")
                    // Для примера, после "сохранения" вернемся назад
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить задачу")
            }
        }
    }
}