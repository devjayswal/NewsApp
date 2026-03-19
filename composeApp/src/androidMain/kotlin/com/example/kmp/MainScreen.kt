package com.example.kmp

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kmp.core.network.AndroidAssetDataSource
import com.example.kmp.core.network.NetworkModule
import com.example.kmp.core.network.model.NewsItem
import com.example.kmp.core.network.repository.AppRepository
import com.example.kmp.common.local.data.AppDatabase
import com.example.kmp.feature.home.HomeViewModel
import com.example.kmp.feature.home.NewsDetailViewModel
import com.example.kmp.feature.profile.ProfileViewModel
import com.example.kmp.feature.search.SearchViewModel
import com.example.kmp.ui.SavedNewsViewModel
import com.example.kmp.common.local.data.NewsDao
import kotlinx.coroutines.launch
import screen.GridHomeScreen
import screen.HomeScreen
import screen.NewsDetailScreen
import screen.ProfileScreen
import screen.SaveNewsDialog
import screen.SavedPageScreen
import screen.SearchScreen
import screen.SettingsScreen
import screen.SummeryPage

private object AppRoute {
    const val Home = "home"
    const val Search = "search"
    const val Trending = "trending"
    const val Settings = "settings"
    const val Profile = "profile"
    const val Saved = "saved"
    const val NewsIdArg = "newsId"
    const val SummaryArg = "summaryText"
    const val NewsDetail = "newsDetail/{$NewsIdArg}"
    const val Summary = "summary/{$SummaryArg}"

    fun newsDetail(newsId: Int): String = "newsDetail/$newsId"
    fun summary(summaryText: String): String = "summary/${Uri.encode(summaryText)}"
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private fun navigateToTopLevel(navController: androidx.navigation.NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(initalName: String, onNameChange: (String) -> Unit) {
    val bottomItems = remember {
        listOf(
            BottomNavItem(AppRoute.Home, "Home", Icons.Default.Home),
            BottomNavItem(AppRoute.Search, "Search", Icons.Default.Search),
            BottomNavItem(AppRoute.Trending, "Trending", Icons.Default.LocationOn),
            BottomNavItem(AppRoute.Settings, "Settings", Icons.Default.Settings)
        )
    }
    val topLevelRoutes = remember { bottomItems.map { it.route }.toSet() }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentBaseRoute = currentRoute?.substringBefore("/")
    val isTopLevelDestination = currentBaseRoute in topLevelRoutes

    var newsToSave by remember { mutableStateOf<NewsItem?>(null) }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    val context = LocalContext.current
    val repository = remember {
        AppRepository(
            NetworkModule.apiService,
            AndroidAssetDataSource(context)
        )
    }

    val database = remember { AppDatabase.getDatabase(context) }
    val savedNewsViewModel: SavedNewsViewModel = viewModel {
        SavedNewsViewModel(database.newsDao())
    }

    val homeViewModel: HomeViewModel = viewModel {
        HomeViewModel(repository)
    }

    val searchViewModel: SearchViewModel = viewModel {
        SearchViewModel(repository, createSavedStateHandle())
    }

    val profileViewModel: ProfileViewModel = viewModel {
        ProfileViewModel(repository)
    }
    val isProfileEditing by profileViewModel.isEditing.collectAsState()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.background
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Text(
                    "Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = currentBaseRoute == AppRoute.Profile,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppRoute.Profile) { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Saved") },
                    selected = currentBaseRoute == AppRoute.Saved,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppRoute.Saved) { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = currentBaseRoute == AppRoute.Settings,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigateToTopLevel(navController, AppRoute.Settings)
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.tertiary,
                        selectedTextColor = MaterialTheme.colorScheme.tertiary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                if (isTopLevelDestination) {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.primary,
                            actionIconContentColor = MaterialTheme.colorScheme.secondary
                        ),
                        title = { Text("NewsApp") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(AppRoute.Profile) { launchSingleTop = true } }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (isTopLevelDestination) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                        tonalElevation = 8.dp
                    ) {
                        bottomItems.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = currentBaseRoute == item.route,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                onClick = { navigateToTopLevel(navController, item.route) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundBrush)
            ) {
                newsToSave?.let { news ->
                    SaveNewsDialog(
                        newsItem = news,
                        viewModel = savedNewsViewModel,
                        onDismiss = { newsToSave = null }
                    )
                }

                NavHost(
                    navController = navController,
                    startDestination = AppRoute.Home,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(260)
                        ) + fadeIn(animationSpec = tween(260))
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(220)
                        ) + fadeOut(animationSpec = tween(220))
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(260)
                        ) + fadeIn(animationSpec = tween(260))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(220)
                        ) + fadeOut(animationSpec = tween(220))
                    }
                ) {
                    composable(AppRoute.Home) {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onNewsClick = { newsItem ->
                                navController.navigate(AppRoute.newsDetail(newsItem.id))
                            },
                            contentPadding = PaddingValues(0.dp)
                        )
                    }

                    composable(AppRoute.Search) {
                        SearchScreen(
                            viewModel = searchViewModel,
                            onNewsClick = { newsItem ->
                                navController.navigate(AppRoute.newsDetail(newsItem.id))
                            },
                            contentPadding = PaddingValues(0.dp)
                        )
                    }

                    composable(AppRoute.Trending) {
                        GridHomeScreen(
                            viewModel = homeViewModel,
                            onNewsClick = { newsItem ->
                                navController.navigate(AppRoute.newsDetail(newsItem.id))
                            },
                            onBackClick = { navigateToTopLevel(navController, AppRoute.Home) },
                            contentPadding = PaddingValues(0.dp)
                        )
                    }

                    composable(AppRoute.Settings) {
                        SettingsScreen()
                    }

                    composable(AppRoute.Profile) {
                        BackHandler(enabled = isProfileEditing) {
                            profileViewModel.cancelEditing()
                        }
                        ProfileScreen(
                            viewmodel = profileViewModel,
                            onBackClick = {
                                if (isProfileEditing) {
                                    profileViewModel.cancelEditing()
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                    composable(AppRoute.Saved) {
                        SavedPageScreen(
                            viewModel = savedNewsViewModel,
                            onBack = { navController.popBackStack() },
                            onNewsClick = { newsItem ->
                                navController.navigate(AppRoute.newsDetail(newsItem.id))
                            }
                        )
                    }

                    composable(
                        route = AppRoute.NewsDetail,
                        arguments = listOf(navArgument(AppRoute.NewsIdArg) { type = NavType.IntType })
                    ) { backStackEntry ->
                        val newsId = backStackEntry.arguments?.getInt(AppRoute.NewsIdArg) ?: return@composable
                        val newsDetailViewModel: NewsDetailViewModel = viewModel {
                            NewsDetailViewModel(repository)
                        }
                        NewsDetailScreen(
                            newsId = newsId,
                            viewModel = newsDetailViewModel,
                            onBackClick = { navController.popBackStack() },
                            onSaveClick = { item -> newsToSave = item },
                            onSummeryClick = { summary ->
                                navController.navigate(AppRoute.summary(summary))
                            }
                        )
                    }

                    composable(
                        route = AppRoute.Summary,
                        arguments = listOf(navArgument(AppRoute.SummaryArg) { type = NavType.StringType })
                    ) { backStackEntry ->
                        val summaryText = backStackEntry.arguments
                            ?.getString(AppRoute.SummaryArg)
                            ?.let(Uri::decode)
                            .orEmpty()
                        SummeryPage(
                            summery = summaryText,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

