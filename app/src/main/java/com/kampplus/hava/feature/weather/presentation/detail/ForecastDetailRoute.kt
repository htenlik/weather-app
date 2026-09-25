package com.kampplus.hava.feature.weather.presentation.detail

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.R
import com.kampplus.hava.feature.weather.presentation.model.ForecastUiModel

@Composable
fun ForecastDetailRoute(onBack: () -> Unit, modifier: Modifier = Modifier, viewModel: ForecastDetailViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ForecastDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onShare = { forecast -> context.shareForecast(forecast) },
        onFavoriteClick = viewModel::onToggleFavorite,
        onRetry = viewModel::loadData,
        modifier = modifier
    )
}

/** Android paylaşım penceresini açar. Platform bağımlılığı Route katmanında kalır. */
private fun Context.shareForecast(forecast: ForecastUiModel) {
    val text = getString(
        R.string.share_text,
        forecast.cityName,
        forecast.temperatureText,
        forecast.conditionEmoji,
        forecast.conditionLabel.asString(resources)
    )
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(sendIntent, getString(R.string.action_share)))
}
