package com.thecurumo.qaf.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecurumo.qaf.data.AppDatabase
import com.thecurumo.qaf.data.Cat
import com.thecurumo.qaf.data.Poet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FeaturedWork(
    val catId: Int,
    val title: String,
    val poetName: String
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val featuredPoets: List<Poet> = emptyList(),
    val featuredWorks: List<FeaturedWork> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(private val database: AppDatabase) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // شناسه‌های چند شاعر مشهور برای بخش "منتخب" — بر اساس گزارش دیتابیس
    private val featuredPoetIds = listOf(2, 3, 4, 5, 7, 9)

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                val (poets, works) = withContext(Dispatchers.IO) {
                    val poetsList = featuredPoetIds.mapNotNull { id ->
                        database.poetDao().getPoetById(id)
                    }

                    val worksList = poetsList.mapNotNull { poet ->
                        val rootCat = database.catDao().getRootCategory(poet.id)
                        if (rootCat != null) {
                            FeaturedWork(
                                catId = rootCat.id,
                                title = rootCat.text ?: "دیوان اشعار",
                                poetName = poet.name ?: ""
                            )
                        } else null
                    }

                    Pair(poetsList, worksList)
                }

                _uiState.value = HomeUiState(
                    isLoading = false,
                    featuredPoets = poets,
                    featuredWorks = works
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "خطا در بارگذاری اطلاعات"
                )
            }
        }
    }
}