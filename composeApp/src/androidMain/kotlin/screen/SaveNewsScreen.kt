package screen

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.kmp.core.network.model.NewsItem
import com.example.kmp.ui.SavedNewsViewModel

@Composable
fun SaveNewsDialog(
    newsItem: NewsItem,
    viewModel: SavedNewsViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        iconContentColor = MaterialTheme.colorScheme.secondary,
        titleContentColor = MaterialTheme.colorScheme.primary,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = {
            Text(text = "Save Article")
        },
        text = {
            Text(text = "Would you like to save \"${newsItem.title}\" to your collection?")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.saveNews(newsItem)
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}
