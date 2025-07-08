package com.example.raceapp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import com.example.raceapp2.ui.screens.dashboard.DashboardScreen
import com.example.raceapp2.ui.screens.dashboard.ProjectItem
import com.example.raceapp2.ui.screens.dashboard.UserRole
import com.example.raceapp2.ui.screens.raci.RaciData
import com.example.raceapp2.ui.screens.raci.RaciMatrixScreen
import com.example.raceapp2.ui.screens.dashboard.ParticipantsScreen
import com.example.raceapp2.ui.screens.settings.Character
import com.example.raceapp2.ui.screens.settings.SettingsScreen
import com.example.raceapp2.ui.screens.LoginScreen
import com.example.raceapp2.ui.theme.RaceApp2Theme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaceApp2Theme {
                var isLoggedIn by remember { mutableStateOf(false) }
                var currentUserRole by remember { mutableStateOf(UserRole.USER) }

                val userProjects = remember {
                    mutableStateListOf(
                        ProjectItem("U1", "Мои проекты - Вид Пользователя", 5)
                    )
                }
                val adminProjects = remember {
                    mutableStateListOf(
                        ProjectItem("A1", "Все проекты - Вид Админа", 10, ownerName = "Разные пользователи"),
                        ProjectItem("A2", "Закупка Материалов", 3, ownerName = "Отдел Снабжения"),
                        ProjectItem("A3", "Подготовка Отчета X", 7, ownerName = "Аналитики")
                    )
                }

                val projectParticipants = remember {
                    mutableStateMapOf(
                        "A1" to listOf("Иванов", "Петров", "Сидоров"),
                        "A2" to listOf("Козлов", "Алексеева"),
                        "A3" to listOf("Морозов", "Никитина", "Ильин")
                    )
                }

                // Мутируемый список персонажей, чтобы изменения обновляли UI
                val characters = remember {
                    mutableStateListOf(
                        Character("1", "admin", "1234567890", "Администратор системы"),
                        Character("2", "user1", null, "Пользователь 1")
                    )
                }

                var showAddProjectDialog by remember { mutableStateOf(false) }
                var projectToEdit by remember { mutableStateOf<ProjectItem?>(null) }
                var projectToDelete by remember { mutableStateOf<ProjectItem?>(null) }
                var showRaciScreen by remember { mutableStateOf(false) }
                var showParticipantsScreen by remember { mutableStateOf(false) }
                var showSettingsScreen by remember { mutableStateOf(false) }
                var selectedProject by remember { mutableStateOf<ProjectItem?>(null) }

                when {
                    !isLoggedIn -> {
                        LoginScreen(
                            onLoginClick = { username, password ->
                                if (username.equals("admin", ignoreCase = true) && password == "admin") {
                                    currentUserRole = UserRole.ADMIN
                                    isLoggedIn = true
                                } else if (username.equals("user", ignoreCase = true) && password == "user") {
                                    currentUserRole = UserRole.USER
                                    isLoggedIn = true
                                }
                            },
                            isLoading = false
                        )
                    }

                    showSettingsScreen -> {
                        SettingsScreen(
                            characters = characters,
                            currentUserRole = currentUserRole,
                            onBack = { showSettingsScreen = false },
                            onUpdateCharacter = { updatedChar ->
                                val index = characters.indexOfFirst { it.id == updatedChar.id }
                                if (index != -1) {
                                    characters[index] = updatedChar
                                }
                            },
                            onAddCharacter = { newChar ->
                                characters.add(newChar)
                            }
                        )
                    }

                    showRaciScreen && selectedProject != null -> {
                        RaciMatrixScreen(
                            project = selectedProject!!,
                            participants = projectParticipants[selectedProject!!.id] ?: emptyList(),
                            onBack = {
                                showRaciScreen = false
                                selectedProject = null
                            },
                            onSave = { matrix: RaciData ->
                                // Тут можно сохранить матрицу
                                println("Сохранена RACI-матрица:")
                                println("Название проекта: ${matrix.projectName}")
                                println("Должности: ${matrix.positions}")
                                println("Этапы: ${matrix.stages}")
                                println("Участники: ${matrix.participants}")
                                showRaciScreen = false
                                selectedProject = null
                            }
                        )
                    }

                    showParticipantsScreen && selectedProject != null -> {
                        ParticipantsScreen(
                            project = selectedProject!!,
                            participants = projectParticipants[selectedProject!!.id] ?: emptyList(),
                            onBack = {
                                showParticipantsScreen = false
                                selectedProject = null
                            }
                        )
                    }

                    else -> {
                        val currentProjectsList = if (currentUserRole == UserRole.ADMIN) adminProjects else userProjects

                        DashboardScreen(
                            projects = currentProjectsList,
                            userRole = currentUserRole,
                            onProjectClick = { project ->
                                if (currentUserRole == UserRole.ADMIN) {
                                    selectedProject = project
                                    showRaciScreen = true
                                }
                            },
                            onParticipantsClick = { project ->
                                if (currentUserRole == UserRole.ADMIN) {
                                    selectedProject = project
                                    showParticipantsScreen = true
                                }
                            },
                            onAddProjectClick = {
                                if (currentUserRole == UserRole.ADMIN) {
                                    showAddProjectDialog = true
                                }
                            },
                            onEditProjectRequest = { projectItem ->
                                if (currentUserRole == UserRole.ADMIN) {
                                    projectToEdit = projectItem
                                }
                            },
                            onDeleteProjectRequest = { projectItem ->
                                if (currentUserRole == UserRole.ADMIN) {
                                    projectToDelete = projectItem
                                }
                            },
                            onLogoutClick = {
                                isLoggedIn = false
                                showAddProjectDialog = false
                                projectToEdit = null
                                projectToDelete = null
                                showRaciScreen = false
                                showParticipantsScreen = false
                                selectedProject = null
                                showSettingsScreen = false
                            },
                            onAdminActionClick = {
                                if (currentUserRole == UserRole.ADMIN) {
                                    showSettingsScreen = true
                                }
                            },
                            isLoading = false,

                            showAddDialog = showAddProjectDialog,
                            onDismissAddDialog = { showAddProjectDialog = false },
                            onConfirmAddProject = { name, taskType, owner ->
                                val newProject = ProjectItem(
                                    id = UUID.randomUUID().toString(),
                                    name = "$name ($taskType)",
                                    taskCount = 0,
                                    ownerName = owner
                                )
                                adminProjects.add(newProject)
                                projectParticipants[newProject.id] = emptyList()
                                selectedProject = newProject
                                showAddProjectDialog = false
                                showRaciScreen = true
                            },

                            projectToEdit = projectToEdit,
                            onDismissEditDialog = { projectToEdit = null },
                            onConfirmEditProject = { updatedProject ->
                                val index = adminProjects.indexOfFirst { it.id == updatedProject.id }
                                if (index != -1) {
                                    adminProjects[index] = updatedProject
                                }
                                projectToEdit = null
                            },

                            projectToDelete = projectToDelete,
                            onDismissDeleteDialog = { projectToDelete = null },
                            onConfirmDeleteProject = { project ->
                                adminProjects.remove(project)
                                projectParticipants.remove(project.id)
                                projectToDelete = null
                            }
                        )
                    }
                }
            }
        }
    }
}
