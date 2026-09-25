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
import com.kampplus.hava.feature.weather.presentation.selection.SelectionViewModel

/**
 * Detay ekranı da liste ile aynı [SelectionViewModel] örneğini gözler: listede eklenen favori burada,
 * burada kaldırılan favori listede anında görünür. "Favori mi" bilgisi state'ten türetilir.
 */
@Composable
fun ForecastDetailRoute(
    selectionViewModel: SelectionViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForecastDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selection by selectionViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ForecastDetailScreen(
        uiState = uiState,
        isFavorite = selection.isFavorite(viewModel.cityId),
        onBack = onBack,
        onFavoriteClick = { selectionViewModel.toggleFavorite(viewModel.cityId) },
        onShare = { forecast -> context.shareForecast(forecast) },
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
