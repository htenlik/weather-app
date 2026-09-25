package com.kampplus.hava.core.common.error

import java.io.IOException
import javax.inject.Inject

/** Teknik hatayı ([Throwable]) alan hatasına ([AppError]) çevirir. */
fun interface ErrorMapper {
    fun map(throwable: Throwable): AppError
}

/** Varsayılan eşleyici: G/Ç hatası ağ hatasıdır, bulunamayan kayıt NotFound, gerisi Unknown. */
class DefaultErrorMapper @Inject constructor() : ErrorMapper {
    override fun map(throwable: Throwable): AppError = when (throwable) {
        is IOException -> AppError.Network
        is NoSuchElementException -> AppError.NotFound
        else -> AppError.Unknown(throwable)
    }
}
