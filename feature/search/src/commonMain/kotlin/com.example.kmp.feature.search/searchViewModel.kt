package com.example.kmp.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp.core.network.model.NewsItem
import com.example.kmp.core.network.repository.AppRepository
import com.example.kmp.core.utils.AppResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class SearchViewModel(
    private val repository: AppRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery: StateFlow<String> = savedStateHandle.getStateFlow("search_query", "")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<SearchUiState> = searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flow { emit(SearchUiState.Idle) }
            } else {
                flow {
                    emit(SearchUiState.Loading)
                    val result = repository.getNews()
                    when (result) {
                        is AppResult.Success -> {
                            val filtered = result.data.filter {
                                it.title.contains(query, ignoreCase = true) ||
                                        it.summary.contains(query, ignoreCase = true)
                            }
                            emit(SearchUiState.Success(filtered))
                        }
                        is AppResult.Error -> emit(SearchUiState.Error(result.message))
                        is AppResult.Loading -> emit(SearchUiState.Loading)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchUiState.Idle
        )

    fun onSearchQueryChange(newQuery: String) {
        savedStateHandle["search_query"] = newQuery
    }
}

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val results: List<NewsItem>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}
