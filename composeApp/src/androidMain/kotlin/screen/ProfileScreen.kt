package screen

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.kmp.feature.profile.ProfileViewModel
import com.example.kmp.core.network.model.NetworkUser
import com.example.kmp.core.utils.AppResult

fun onBackClick(){

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewmodel: ProfileViewModel,
    onBackClick: () -> Unit
) {
    val profileState by viewmodel.profile.collectAsState()
    val isEditing by viewmodel.isEditing.collectAsState()
    val editingUser by viewmodel.editingUser.collectAsState()
    val errors by viewmodel.errors.collectAsState()
    val selectedImageUri by viewmodel.selectedImageUri.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Profile" else "Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEditing && profileState is AppResult.Success) {
                        IconButton(onClick = { viewmodel.startEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (profileState) {
                is AppResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AppResult.Success -> {
                    val user = (profileState as AppResult.Success<NetworkUser>).data
                    if (isEditing && editingUser != null) {
                        EditProfileForm(
                            user = editingUser!!,
                            errors = errors,
                            selectedImageUri = selectedImageUri,
                            onImageSelected = { viewmodel.onImageSelected(it) },
                            onSave = { viewmodel.saveProfile() },
                            onCancel = { viewmodel.cancelEditing() },
                            onUpdate = { updatedUser -> 
                                viewmodel.updateEditingUser { updatedUser }
                            }
                        )
                    } else {
                        ProfileDetails(user = user, selectedImageUri = selectedImageUri)
                    }
                }
                is AppResult.Error -> {
                    Text(
                        text = "Error: ${(profileState as AppResult.Error).message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileDetails(user: NetworkUser, selectedImageUri: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // box for holding profile image
        Box(
            // circular shape
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        ){
            ProfileImage(selectedImageUri = selectedImageUri)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center

        ){
            Text(
                text = "${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.headlineMedium

            )

        }

        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
        InfoRow(label = "Phone", value = "+91 9589883539")
        InfoRow(label = "Email", value = user.email)
        InfoRow(label = "City", value = user.city)
        InfoRow(label = "Country", value = "India")
        InfoRow(label="gender",value = user.gender)
        InfoRow(label = "Language", value = user.language)
        InfoRow(label = "Address", value = user.address)

    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ProfileImage(selectedImageUri: String?, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null) {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val context = LocalContext.current
            val bitmap = remember {
                try {
                    val inputStream = context.assets.open("profile.png")
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    null
                }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Default Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }
        
        if (onClick != null) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun EditProfileForm(
    user: NetworkUser,
    errors: Map<String, String>,
    selectedImageUri: String?,
    onImageSelected: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onUpdate: (NetworkUser) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it.toString()) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImage(
            selectedImageUri = selectedImageUri,
            onClick = { launcher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = user.firstName,
                singleLine = true,
                onValueChange = { onUpdate(user.copy(firstName = it)) },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f),
                isError = errors.containsKey("firstName"),
                supportingText = { errors["firstName"]?.let { Text(it) } }
            )
            OutlinedTextField(
                value = user.lastName,
                singleLine = true,
                onValueChange = { onUpdate(user.copy(lastName = it)) },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f),
                isError = errors.containsKey("lastName"),
                supportingText = { errors["lastName"]?.let { Text(it) } }
            )
        }

        OutlinedTextField(
            value = user.email,
            singleLine = true,
            onValueChange = { onUpdate(user.copy(email = it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.containsKey("email"),
            supportingText = { errors["email"]?.let { Text(it) } }
        )

        OutlinedTextField(
            value = user.city,
            singleLine = true,
            onValueChange = { onUpdate(user.copy(city = it)) },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.containsKey("city"),
            supportingText = { errors["city"]?.let { Text(it) } }
        )

        OutlinedTextField(
            value = user.gender,
            singleLine = true,
            onValueChange = { onUpdate(user.copy(gender = it)) },
            label = { Text("Gender") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = user.language,
            singleLine = true,
            onValueChange = { onUpdate(user.copy(language = it)) },
            label = { Text("Language") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = user.address,
            singleLine = true,
            onValueChange = { onUpdate(user.copy(address = it)) },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}
