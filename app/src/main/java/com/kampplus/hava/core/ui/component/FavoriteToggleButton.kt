package com.kampplus.hava.core.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.kampplus.hava.R

/** Kalp ikonu. [tintOnDark] koyu bir başlık üzerinde boş kalbi beyaz çizer. */
@Composable
fun FavoriteToggleButton(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, tintOnDark: Boolean = false) {
    val tint = when {
        isFavorite -> if (tintOnDark) Color(0xFFFF8A80) else MaterialTheme.colorScheme.error
        tintOnDark -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(if (isFavorite) R.string.action_remove_favorite else R.string.action_add_favorite),
            tint = tint
        )
    }
}
