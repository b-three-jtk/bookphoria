package com.example.bookphoria.ui


import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookphoria.ui.auth.ChangePasswordScreen
import com.example.bookphoria.ui.auth.ForgotpassScreen
import com.example.bookphoria.ui.auth.LoginScreen
import com.example.bookphoria.ui.auth.RegisterScreen
import com.example.bookphoria.ui.auth.ResetpassScreen
import com.example.bookphoria.ui.book.DetailBookNetworkScreen
import com.example.bookphoria.ui.book.DetailBookScreen
import com.example.bookphoria.ui.book.EditBookScreen
import com.example.bookphoria.ui.book.EntryBookScreen
import com.example.bookphoria.ui.book.MyShelfScreen
import com.example.bookphoria.ui.book.ScanCodeScreen
import com.example.bookphoria.ui.book.SearchScreen
import com.example.bookphoria.ui.book.ShelfDetailScreen
import com.example.bookphoria.ui.book.YourBooksScreen
import com.example.bookphoria.ui.home.MainScreen
import com.example.bookphoria.ui.onboarding.OnboardingScreen
import com.example.bookphoria.ui.profile.EditProfileScreen
import com.example.bookphoria.ui.profile.FriendScreen
import com.example.bookphoria.ui.profile.ProfileFriendScreen
import com.example.bookphoria.ui.profile.ProfileScreen
import com.example.bookphoria.ui.viewmodel.AuthViewModel
import com.example.bookphoria.ui.viewmodel.BookViewModel
import com.example.bookphoria.ui.viewmodel.DetailBookViewModel
import com.example.bookphoria.ui.viewmodel.EditBookViewModel
import com.example.bookphoria.ui.viewmodel.EntryBookViewModel
import com.example.bookphoria.ui.viewmodel.FriendViewModel
import com.example.bookphoria.ui.viewmodel.HomeViewModel
import com.example.bookphoria.ui.viewmodel.OnboardingViewModel
import com.example.bookphoria.ui.viewmodel.ProfileFriendViewModel
import com.example.bookphoria.ui.viewmodel.ProfileViewModel
import com.example.bookphoria.ui.viewmodel.ShelfDetailViewModel

@OptIn(ExperimentalGetImage::class)
@Composable
fun AppNavHost(
    startDestination: String,
    onDeepLinkTriggered: (NavController) -> Unit = {},
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val bookViewModel: BookViewModel = hiltViewModel()
    val detailBookViewModel: DetailBookViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val friendViewModel: FriendViewModel = hiltViewModel()
    val profileFriendViewModel: ProfileFriendViewModel = hiltViewModel()
    val profilViewModel: ProfileViewModel = hiltViewModel()

    val isOnboardingCompleteState = onboardingViewModel
        .isOnboardingComplete
        .collectAsState(initial = null)

    val isOnboardingComplete = isOnboardingCompleteState.value

    LaunchedEffect(navController) {
        onDeepLinkTriggered(navController)
    }

    // Memastikan tidak ada yang muncul sebelum onboarding tersedia
    if (isOnboardingComplete == null) return

    val startDestination = when {
        !isOnboardingComplete -> "onboarding"
        startDestination == "home" -> "home"
        startDestination == "login" -> "login"
        else -> "register"
    }

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    onFinished = {
                        onboardingViewModel.completeOnboarding()
                        navController.navigate("register") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("login") {
                LoginScreen(viewModel = authViewModel, navController = navController)
            }
            composable("register") {
                RegisterScreen(viewModel = authViewModel, navController = navController)
            }
            composable("forgot") {
                ForgotpassScreen(viewModel = authViewModel, navController = navController)
            }
            composable("reset") {
                ResetpassScreen(viewModel = authViewModel, navController = navController)
            }
            composable("home") {
                MainScreen(navController = navController, viewModel = homeViewModel)
            }
            composable("search") {
                SearchScreen(navController = navController)
            }
            composable("friend-list") {
                FriendScreen(viewModel = friendViewModel, navController = navController)
            }

            composable(
                route = "edit_book/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: return@composable

                val viewModel: EditBookViewModel = hiltViewModel()

                EditBookScreen(
                    bookId = bookId,
                    viewModel = viewModel,
                    navController = navController
                )
            }

            // edit profile
            composable("edit-profile") {
                EditProfileScreen(profilViewModel, navController)
            }

            composable("profile") {
                ProfileScreen(navController = navController)
            }

            composable(route = "user-profile/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                ProfileFriendScreen(userId = userId, viewModel = profileFriendViewModel, friendViewModel = friendViewModel, navController = navController)
            }

            composable(
                "reset?token={token}&email={email}",
                arguments = listOf(
                    navArgument("token") { type = NavType.StringType; defaultValue = "" },
                    navArgument("email") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                val email = backStackEntry.arguments?.getString("email") ?: ""
                ResetpassScreen(
                    viewModel = authViewModel,
                    navController = navController,
                    token = token,
                    email = email
                )
            }
            composable("add-new-book") {
                val viewModel: EntryBookViewModel = hiltViewModel()
                EntryBookScreen(navController = navController, viewModel = viewModel)
            }
            composable(
                route = "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                DetailBookScreen(navController = navController, bookViewModel = bookViewModel, bookId = id)
            }
            composable(
                route = "detail/search/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                DetailBookNetworkScreen(navController = navController, bookViewModel = detailBookViewModel, bookId = id)
            }
            composable("scan") {
                ScanCodeScreen(
                    onScanResult = { code ->
                        // Navigasi ke entry book dengan hasil scan
                        // Simpan ISBN hasil scan ke savedStateHandle
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("search_query", code)

                        navController.navigate("search")
                    },
                    onCancel = {
                        navController.popBackStack() // kembali ke layar sebelumnya
                    }
                )
            }

            composable("myshelf") {
                MyShelfScreen(navController = navController)
            }
            composable("your_books") {
                YourBooksScreen(navController = navController)
            }
            composable(
                route = "detail_shelf/{userId}/{shelfId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.IntType },
                    navArgument("shelfId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                val shelfId = backStackEntry.arguments?.getInt("shelfId") ?: 0
                val viewModel: ShelfDetailViewModel = hiltViewModel()
                ShelfDetailScreen(
                    userId = userId,
                    shelfId = shelfId,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable("change") {
                ChangePasswordScreen(viewModel = authViewModel, navController = navController)
            }
        }
    }
}

