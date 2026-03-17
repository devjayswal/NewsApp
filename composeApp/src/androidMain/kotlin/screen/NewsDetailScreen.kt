package screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.kmp.core.network.model.NewsItem
import com.example.kmp.core.utils.AppResult
import com.example.kmp.feature.home.NewsDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsItem: NewsItem,
    viewModel: NewsDetailViewModel,
    onBackClick: () -> Unit
) {
    // We can still use the state to fetch more details if needed, 
    // but we use the passed newsItem for instant rendering.
    val state by viewModel.newsDetail.collectAsState()

    LaunchedEffect(newsItem.id) {
        viewModel.loadNewsDetail(newsItem.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Instant UI using the passed newsItem
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = newsItem.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = newsItem.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "By ${newsItem.newsSite} • ${newsItem.publishedAt}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Show summary instantly, then maybe update with full content if API returns more
                    val displaySummary = when (val result = state) {
                        is AppResult.Success -> result.data.summary
                        else -> newsItem.summary
                    }
                    
                    Text(
                        text = displaySummary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    if (state is AppResult.Loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { goto(newsItem.url) }) {
                        Text("Read More")
                    )
                }
            }
        }
    }
}
