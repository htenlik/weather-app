package com.kampplus.hava.feature.location.domain.repository

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.location.domain.model.UserLocation

interface LocationRepository {
    /**
     * Cihazın güncel konumu. Konum izni çağırandan önce alınmış olmalıdır; izin yoksa ya da
     * konum belirlenemezse [AppResult.Failure] döner, exception fırlatmaz.
     */
    suspend fun getCurrentLocation(): AppResult<UserLocation>
}
