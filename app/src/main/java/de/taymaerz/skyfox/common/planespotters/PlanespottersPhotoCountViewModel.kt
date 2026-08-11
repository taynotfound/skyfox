package de.taymaerz.skyfox.common.planespotters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.planespotters.api.PlanespottersEndpoint
import de.taymaerz.skyfox.main.core.aircraft.AircraftHex
import de.taymaerz.skyfox.main.core.aircraft.Registration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanespottersPhotoCountViewModel @Inject constructor(
    private val endpoint: PlanespottersEndpoint,
) : ViewModel() {

    private val _photoCount = MutableStateFlow(1)
    val photoCount: StateFlow<Int> = _photoCount

    fun load(hex: AircraftHex, registration: Registration?) {
        viewModelScope.launch {
            try {
                val photos = if (registration != null) {
                    endpoint.getPhotosByRegistration(registration)
                } else {
                    endpoint.getPhotosByHex(hex)
                }
                _photoCount.value = maxOf(1, photos.size)
            } catch (_: Exception) {
                _photoCount.value = 1
            }
        }
    }
}
