package com.example.kmp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kmp.core.network.AndroidAssetDataSource
import com.example.kmp.core.network.NetworkModule
import com.example.kmp.core.network.model.NewsItem
import com.example.kmp.core.network.repository.AppRepository
import com.example.kmp.feature.home.HomeViewModel
import com.example.kmp.feature.home.NewsDetailViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kmp.data.local.AppDatabase
import com.example.kmp.ui.SavedNewsViewModel
import screen.HomeScreen
import screen.NewsDetailScreen
import screen.SaveNewsDialog
import screen.SavedPageScreen
import screen.GridHomeScreen
import screen.SummeryPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableIntStateOf(0) }

    // State for News Detail Navigation
    var selectedNews by remember { mutableStateOf<NewsItem?>(null) }
    
    // State for Saved Page Navigation
    var showSavedPage by remember { mutableStateOf(false) }
    
    // State for Save News Navigation
    var newsToSave by remember { mutableStateOf<NewsItem?>(null) }

    var showSummery by remember { mutableStateOf(false) }
    var summeryText by remember { mutableStateOf("") }


    // Handle back button for sub-screens
    BackHandler(enabled = selectedNews != null || showSavedPage || showSummery) {
        when {
            showSummery -> showSummery = false
            selectedNews != null -> selectedNews = null
            showSavedPage -> showSavedPage = false
        }
    }

    val navItems = listOf("Home", "Search", "Trending", "Settings")
    val navIcons = listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.LocationOn, Icons.Default.Settings)

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
                    selected = false, 
                    onClick = { scope.launch { drawerState.close() } },
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
                    selected = showSavedPage,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showSavedPage = true
                    },
                    icon = {Icon(Icons.Default.Favorite, contentDescription = null)},
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
                    selected = false, 
                    onClick = { scope.launch { drawerState.close() } },
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
        val isSubScreenVisible = selectedNews != null || showSavedPage || showSummery
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                if (!isSubScreenVisible) {
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
                            IconButton(onClick = { /* Profile Click */ }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (!isSubScreenVisible) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                        tonalElevation = 8.dp
                    ) {
                        navItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = { Icon(navIcons[index], contentDescription = item) },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                onClick = { selectedItem = index }
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
                
                // Dialog for saving news
                newsToSave?.let { news ->
                    SaveNewsDialog(
                        newsItem = news,
                        viewModel = savedNewsViewModel,
                        onDismiss = { newsToSave = null }
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    val currentPadding = if (!isSubScreenVisible) padding else PaddingValues(0.dp)
                    
                    when {
                        showSummery -> {
                            SummeryPage(
                                summery = summeryText,
                                onBackClick = { showSummery = false }
                            )
                        }
                        selectedNews != null -> {
                            val newsDetailViewModel: NewsDetailViewModel = viewModel {
                                NewsDetailViewModel(repository)
                            }
                            NewsDetailScreen(
                                newsItem = selectedNews!!,
                                viewModel = newsDetailViewModel,
                                onBackClick = { selectedNews = null },
                                onSaveClick = { item -> newsToSave = item },
                                onSummeryClick = { summary ->
                                    summeryText = summary
                                    showSummery = true
                                }
                            )
                        }
                        showSavedPage -> {
                            SavedPageScreen(
                                viewModel = savedNewsViewModel,
                                onBack = { showSavedPage = false },
                                onNewsClick = { newsItem -> 
                                    selectedNews = newsItem
                                }
                            )
                        }
                        else -> {
                            when (selectedItem) {
                                0 -> {
                                    HomeScreen(
                                        viewModel = homeViewModel,
                                        onNewsClick = { newsItem -> selectedNews = newsItem },
                                        contentPadding = currentPadding
                                    )
                                }
                                1 -> CenteredText("Search Screen")
                                2 -> {
                                    GridHomeScreen(
                                        viewModel = homeViewModel,
                                        onNewsClick = {newsItem -> selectedNews = newsItem },
                                        onBackClick = { selectedItem = 0 },
                                        contentPadding = currentPadding
                                    )
                                }
                                3 -> CenteredText("Settings Screen")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CenteredText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
            )
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
