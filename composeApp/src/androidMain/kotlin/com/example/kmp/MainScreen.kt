package com.example.kmp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import screen.HomeScreen
import screen.NewsDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableIntStateOf(0) }

    // State for News Detail Navigation - Now holds the full object for instant loading
    var selectedNews by remember { mutableStateOf<NewsItem?>(null) }

    // Handle back button when a news item is selected
    if (selectedNews != null) {
        BackHandler {
            selectedNews = null
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
    val homeViewModel: HomeViewModel = viewModel {
        HomeViewModel(repository)
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                NavigationDrawerItem(
                    label = { Text("Profile") }, 
                    selected = false, 
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") }, 
                    selected = false, 
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (selectedNews == null) {
                    CenterAlignedTopAppBar(
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
                if (selectedNews == null) {
                    NavigationBar {
                        navItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = { Icon(navIcons[index], contentDescription = item) },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(if (selectedNews == null) padding else WindowInsets.systemBars.asPaddingValues())) {
                
                val currentNews = selectedNews
                if (currentNews != null) {
                    val newsDetailViewModel: NewsDetailViewModel = viewModel {
                        NewsDetailViewModel(repository)
                    }
                    NewsDetailScreen(
                        newsItem = currentNews,
                        viewModel = newsDetailViewModel,
                        onBackClick = { selectedNews = null }
                    )
                } else {
                    when (selectedItem) {
                        0 -> {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNewsClick = { newsItem -> selectedNews = newsItem }
                            )
                        }
                        1 -> CenteredText("Search Screen")
                        2 -> CenteredText("Trending Screen")
                        3 -> CenteredText("Settings Screen")
                    }
                }
            }
        }
    }
}

@Composable
fun CenteredText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}
