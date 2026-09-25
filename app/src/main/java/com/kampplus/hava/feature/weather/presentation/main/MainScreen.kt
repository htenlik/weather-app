package com.kampplus.hava.feature.weather.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.core.ui.component.ContentCard
import com.kampplus.hava.core.ui.theme.HavaTheme

/**
 * Uygulamanın ilk ekranı. Kendisi veri üretmez: başlık, açıklama ve kart içerikleri
 * parametreyle gelir, her kart için tekrar kullanılabilir [ContentCard] çizilir.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(title: String, description: String, cards: List<ContentItem>, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(text = title) }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            items(cards) { card ->
                ContentCard(title = card.title, description = card.description)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    HavaTheme {
        MainScreen(
            title = "Şehir Havası",
            description = "Öne çıkan şehirlerin anlık hava durumunu keşfet.",
            cards = sampleContent
        )
    }
}
