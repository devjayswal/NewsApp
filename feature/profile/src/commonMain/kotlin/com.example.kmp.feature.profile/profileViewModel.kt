package com.example.kmp.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp.core.network.model.NetworkUser
import com.example.kmp.core.network.repository.AppRepository
import com.example.kmp.core.utils.AppResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: AppRepository
) : ViewModel() {
    private val _profile = MutableStateFlow<AppResult<NetworkUser>>(AppResult.Loading)
    val profile: StateFlow<AppResult<NetworkUser>> = _profile.asStateFlow()

    private var _isEditing = MutableStateFlow(false)
    var isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _editingUser = MutableStateFlow<NetworkUser?>(null)
    val editingUser: StateFlow<NetworkUser?> = _editingUser.asStateFlow()

    private val _errors = MutableStateFlow<Map<String, String>>(emptyMap())
    val errors: StateFlow<Map<String, String>> = _errors.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _profile.value = AppResult.Loading
            val result = repository.getUsers()
            if (result is AppResult.Success) {
                // For now, just taking the first user as "my profile"
                val user = result.data.firstOrNull()
                if (user != null) {
                    _profile.value = AppResult.Success(user)
                } else {
                    _profile.value = AppResult.Error("User not found")
                }
            } else if (result is AppResult.Error) {
                _profile.value = AppResult.Error(result.message)
            }
        }
    }

    fun startEditing() {
        val currentProfile = (_profile.value as? AppResult.Success)?.data
        if (currentProfile != null) {
            _editingUser.value = currentProfile
            _isEditing.value = true
            _errors.value = emptyMap()
        }
    }

    fun cancelEditing() {
        _isEditing.value = false
        _editingUser.value = null
        _errors.value = emptyMap()
    }

    fun updateEditingUser(update: (NetworkUser) -> NetworkUser) {
        _editingUser.update { it?.let(update) }
        // Clear errors when updating
        _errors.value = emptyMap()
    }

    fun onImageSelected(uri: String) {
        _selectedImageUri.value = uri
    }

    private fun validate(): Boolean {
        val user = _editingUser.value ?: return false
        val newErrors = mutableMapOf<String, String>()

        if (user.firstName.isBlank()) {
            newErrors["firstName"] = "First name cannot be empty"
        }
        if (user.lastName.isBlank()) {
            newErrors["lastName"] = "Last name cannot be empty"
        }
        if (user.email.isBlank()) {
            newErrors["email"] = "Email cannot be empty"
        } else if (!isValidEmail(user.email)) {
            newErrors["email"] = "Invalid email format"
        }
        if (user.city.isBlank()) {
            newErrors["city"] = "City cannot be empty"
        }

        _errors.value = newErrors
        return newErrors.isEmpty()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        return email.matches(emailRegex)
    }

    fun saveProfile() {
        if (validate()) {
            val updatedUser = _editingUser.value
            if (updatedUser != null) {
                // In a real app, we would call repository.updateUser(updatedUser)
                _profile.value = AppResult.Success(updatedUser)
                _isEditing.value = false
                _editingUser.value = null
                _errors.value = emptyMap()
            }
        }
    }
}
