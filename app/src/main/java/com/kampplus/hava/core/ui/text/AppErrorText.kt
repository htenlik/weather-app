package com.kampplus.hava.core.ui.text

import com.kampplus.hava.R
import com.kampplus.hava.core.common.error.AppError

/** Hata sözlüğünün kullanıcıya gösterilecek karşılığı. */
fun AppError.toUiText(): UiText = when (this) {
    AppError.Network -> UiText.Resource(R.string.error_network)
    AppError.NotFound -> UiText.Resource(R.string.error_not_found)
    AppError.Parse -> UiText.Resource(R.string.error_parse)
    AppError.LocationDisabled -> UiText.Resource(R.string.error_location_disabled)
    AppError.LocationUnavailable -> UiText.Resource(R.string.error_location_unavailable)
    is AppError.Server -> UiText.Resource(R.string.error_server, code)
    is AppError.Unknown -> UiText.Resource(R.string.error_generic)
}
