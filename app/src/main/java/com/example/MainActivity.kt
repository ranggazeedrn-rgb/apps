package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.screen.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ComplaintViewModel

// State-based navigation routes
sealed interface Screen {
    object Login : Screen
    object Register : Screen
    object CitizenDashboard : Screen
    object OfficerDashboard : Screen
    object AdminDashboard : Screen
    object AddComplaint : Screen
    data class ComplaintDetail(val id: Long) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Initialize the single-source-of-truth ComplaintViewModel
                val context = LocalContext.current.applicationContext as Application
                val viewModel: ComplaintViewModel = viewModel {
                    ComplaintViewModel(context)
                }

                val user by viewModel.currentUser.collectAsState()

                // Pristine manual stack-based navigation
                val screenStack = remember { mutableStateListOf<Screen>(Screen.Login) }
                val currentScreen = screenStack.lastOrNull() ?: Screen.Login

                // Navigation actions
                val navigateTo: (Screen) -> Unit = { screen ->
                    screenStack.add(screen)
                }

                val navigateBack: () -> Unit = {
                    if (screenStack.size > 1) {
                        screenStack.removeAt(screenStack.size - 1)
                    }
                }

                // Global session listener (Log out forces Redirect to Login)
                LaunchedEffect(user) {
                    if (user == null && currentScreen != Screen.Login && currentScreen != Screen.Register) {
                        screenStack.clear()
                        screenStack.add(Screen.Login)
                    }
                }

                // Custom Back Press Handler routing
                BackHandler(enabled = screenStack.size > 1) {
                    navigateBack()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    when (val screen = currentScreen) {
                        is Screen.Login -> {
                            LoginScreen(
                                viewModel = viewModel,
                                onNavigateToRegister = { navigateTo(Screen.Register) },
                                onLoginSuccess = {
                                    // Route based on role
                                    val currentUser = viewModel.currentUser.value
                                    screenStack.clear()
                                    when (currentUser?.role) {
                                        UserRole.ADMIN -> screenStack.add(Screen.AdminDashboard)
                                        UserRole.PETUGAS -> screenStack.add(Screen.OfficerDashboard)
                                        else -> screenStack.add(Screen.CitizenDashboard)
                                    }
                                }
                            )
                        }
                        is Screen.Register -> {
                            RegisterScreen(
                                viewModel = viewModel,
                                onNavigateBack = navigateBack,
                                onRegisterSuccess = {
                                    screenStack.clear()
                                    screenStack.add(Screen.CitizenDashboard)
                                }
                            )
                        }
                        is Screen.CitizenDashboard -> {
                            CitizenDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToAddComplaint = { navigateTo(Screen.AddComplaint) },
                                onNavigateToComplaintDetail = { comp -> navigateTo(Screen.ComplaintDetail(comp.id)) },
                                onLogout = {
                                    screenStack.clear()
                                    screenStack.add(Screen.Login)
                                }
                            )
                        }
                        is Screen.OfficerDashboard -> {
                            OfficerDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToComplaintDetail = { comp -> navigateTo(Screen.ComplaintDetail(comp.id)) },
                                onLogout = {
                                    screenStack.clear()
                                    screenStack.add(Screen.Login)
                                }
                            )
                        }
                        is Screen.AdminDashboard -> {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToComplaintDetail = { comp -> navigateTo(Screen.ComplaintDetail(comp.id)) },
                                onLogout = {
                                    screenStack.clear()
                                    screenStack.add(Screen.Login)
                                }
                            )
                        }
                        is Screen.AddComplaint -> {
                            AddComplaintScreen(
                                viewModel = viewModel,
                                onNavigateBack = navigateBack,
                                onSubmitSuccess = {
                                    navigateBack() // Pop back to dashboard
                                }
                            )
                        }
                        is Screen.ComplaintDetail -> {
                            ComplaintDetailScreen(
                                viewModel = viewModel,
                                complaintId = screen.id,
                                onNavigateBack = navigateBack
                            )
                        }
                    }
                }
            }
        }
    }
}
