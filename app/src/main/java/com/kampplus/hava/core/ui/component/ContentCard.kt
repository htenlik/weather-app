package com.kampplus.hava.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.core.ui.theme.HavaTheme

/**
 * Başlık ve açıklama gösteren, tekrar kullanılabilir içerik kartı.
 *
 * Parametreler bileşenin girdilerini (metinleri) tanımlar; [modifier] ise dış yerleşimi
 * (genişlik, kenar boşluğu) belirler ve her zaman çağıran tarafın elindedir.
 * Uzun metinler satır sınırı ve üç nokta ile kırpılır; kart taşmaz.
 */
@Composable
fun ContentCard(title: String, description: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(name = "Kısa metin", showBackground = true)
@Composable
private fun ContentCardShortPreview() {
    HavaTheme {
        ContentCard(
            title = "Ankara",
            description = "21° · Açık",
            modifier = Modifier.padding(16.dp)
        )
    }
}
