package com.kampplus.hava.core.common.error

/** Uygulama genelindeki hata sözlüğü. UI metinleri presentation katmanında üretilir. */
sealed interface AppError {
    data object Network : AppError

    data class Server(
        val code: Int
    ) : AppError

    data object NotFound : AppError

    data object Parse : AppError

    /** Cihazda konum servisleri kapalı. */
    data object LocationDisabled : AppError

    /** Konum servisleri açık ama konum alınamadı (zaman aşımı, sağlayıcı yok, izin yok). */
    data object LocationUnavailable : AppError

    data class Unknown(
        val cause: Throwable
    ) : AppError
}
