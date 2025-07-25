package com.example.conan.presentation.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conan.R
import com.example.conan.data.models.Quote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeUiEvent.LoadData)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1117),
                        Color(0xFF161B22),
                        Color(0xFF0D1117)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar with settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4FD1C7),
                    fontSize = 28.sp
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color(0xFF2D3748),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF4A90E2).copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = Color.White
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4FD1C7)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Daily Quote Card
                    item {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            DailyQuoteCard(
                                quote = uiState.dailyQuote,
                                onRefresh = { viewModel.onEvent(HomeUiEvent.RefreshQuote) }
                            )
                        }
                    }
                    // Streak Counter Card
                    item {
                        StreakCard(
                            currentStreak = uiState.currentStreak?.dayCount ?: 0,
                            longestStreak = uiState.longestStreak,
                            onResetStreak = { viewModel.onEvent(HomeUiEvent.ResetStreak) }
                        )
                    }

                    // Educational Links
                    item {
                        EducationalLinksCard()
                    }

                }
            }

            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2D1B1B)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFE53E3E).copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFE53E3E)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyQuoteCard(
    quote: Quote?,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF4A5568).copy(alpha = 0.3f),
                spotColor = Color(0xFF2D3748).copy(alpha = 0.5f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A202C)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFF2D3748)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFF4FD1C7),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.daily_inspiration),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color(0xFF2D3748),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh),
                        tint = Color(0xFF4FD1C7),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            quote?.let {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "\"${it.text}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Start
                    )

                    it.source?.let { source ->
                        Text(
                            text = "— $source",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4FD1C7),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } ?: run {
                Text(
                    text = "Loading inspiration...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFA0AEC0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
@Composable
private fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    onResetStreak: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A202C)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFF2D3748)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF4FD1C7),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.clean_streak),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(
                    onClick = onResetStreak,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        tint = Color(0xFFE53E3E),
                        contentDescription = "Reset Streak",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StreakItem(
                    title = stringResource(R.string.current_days),
                    value = currentStreak.toString(),
                    color = Color(0xFF4FD1C7)
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(Color(0xFF2D3748))
                )

                StreakItem(
                    title = stringResource(R.string.best_streak),
                    value = longestStreak.toString(),
                    color = Color(0xFFED8936)
                )
            }
        }
    }
}

@Composable
private fun StreakItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 32.sp
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFA0AEC0),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PanicButtonCard(
    onPanicPress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D1B1B)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFE53E3E).copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onPanicPress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53E3E)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.panic_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EducationalLinksCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A202C)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFF2D3748)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.AccountBox,
                    contentDescription = null,
                    tint = Color(0xFF4FD1C7),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.educational_resources),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            val context = LocalContext.current
            val educationalLinks = listOf(
                "Understanding Addiction" to "https://www.addictioncenter.com/addiction/",
                "Recovery Support" to "https://www.samhsa.gov/find-help/national-helpline",
                "Mindfulness Techniques" to "https://www.mindful.org/meditation/mindfulness-getting-started/",
                "Islamic Guidance" to "https://islamqa.info/",
            )

            educationalLinks.forEach { (title, url) ->
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF4FD1C7)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF4FD1C7).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}


