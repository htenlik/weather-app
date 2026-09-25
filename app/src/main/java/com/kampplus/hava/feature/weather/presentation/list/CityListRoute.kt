package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.presentation.selection.SelectionViewModel

/**
 * ViewModel'leri ekrana bağlayan katman. Ekranın kendisi ([CityListScreen]) stateless'tır.
 *
 * İki state kaynağı gözlemlenir: bu ekrana ait yükleme durumu ([CityListViewModel]) ve ekranlar arasında
 * paylaşılan seçim durumu ([SelectionViewModel], NavHost'un üstünden gelir). Ekranda gösterilen liste bu
 * ikisinden türetilir; türetilmiş sonuç ayrı bir state olarak saklanmaz.
 */
@Composable
fun CityListRoute(
    selectionViewModel: SelectionViewModel,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityListViewModel = hiltViewModel()
) {
    val loadState by viewModel.uiState.collectAsStateWithLifecycle()
    val selection by selectionViewModel.uiState.collectAsStateWithLifecycle()
    CityListScreen(
        uiState = loadState.applySelection(selection),
        selection = selection,
        onCityClick = { cityId -> viewModel.findCity(cityId)?.let(onCityClick) },
        onFavoriteClick = selectionViewModel::toggleFavorite,
        onShowOnlyFavoritesChange = selectionViewModel::setShowOnlyFavorites,
        modifier = modifier
    )
}
