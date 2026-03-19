package screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					listOf(
						MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
						MaterialTheme.colorScheme.background
					)
				)
			)
			.padding(16.dp)
	) {
		ElevatedCard(
			colors = CardDefaults.elevatedCardColors(
				containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
			)
		) {
			Text(
				text = "Settings",
				modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
				style = MaterialTheme.typography.headlineSmall,
				color = MaterialTheme.colorScheme.tertiary
			)
		}
	}
}

