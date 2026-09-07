package com.thecurumo.qaf.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thecurumo.qaf.data.DatabaseProvider
import com.thecurumo.qaf.ui.components.HomeHeader
import com.thecurumo.qaf.ui.components.PoetCard
import com.thecurumo.qaf.ui.components.SectionHeader
import com.thecurumo.qaf.ui.components.WorkCard

@Composable
fun HomeScreen(
    onPoetClick: (Int) -> Unit,
    onWorkClick: (Int) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = remember(context) { DatabaseProvider.getDatabase(context) }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(database))
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HomeHeader(
            onSearchClick = onSearchClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "خطا: ${uiState.errorMessage}")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(26.dp)
                ) {
                    item {
                        Column {
                            SectionHeader(title = "ادامه مطالعه")
                            Text(
                                text = "هنوز شعری نخوانده‌اید",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    item {
                        Column {
                            SectionHeader(title = "سخنوران منتخب")
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(
                                        (((uiState.featuredPoets.size + 1) / 2) * 172).dp
                                    ),
                                userScrollEnabled = false
                            ) {
                                items(uiState.featuredPoets) { poet ->
                                    PoetCard(
                                        poetId = poet.id,
                                        poetName = poet.name ?: "نامشخص",
                                        dateRange = formatDateRange(poet.birth_year, poet.death_year),
                                        origin = poet.birthplace ?: "",
                                        onClick = { onPoetClick(poet.id) }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column {
                            SectionHeader(title = "آثار و دیوان‌های منتخب")
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                uiState.featuredWorks.forEach { work ->
                                    WorkCard(
                                        workTitle = work.title,
                                        poetName = work.poetName,
                                        onClick = { onWorkClick(work.catId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDateRange(birth: Int?, death: Int?): String {
    if (birth == null || death == null) return ""
    return "$birth — $death هـ.ق"
}