package com.example.levelapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ServiceUiState(
    val dispositivo: String = "",
    val descripcion: String = "",
    val direccion: String = "",
    val mensaje: String? = null,
    val solicitudEnviada: Boolean = false,
    val cargandoUbicacion: Boolean = false,
    val enviandoSolicitud: Boolean = false
)

class ServiceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    fun onDispositivoChange(v: String) = _uiState.update { it.copy(dispositivo = v) }
    fun onDescripcionChange(v: String) = _uiState.update { it.copy(descripcion = v) }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionReal(context: Context) {
        _uiState.update { it.copy(cargandoUbicacion = true, direccion = "Detectando satélites...") }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (!addresses.isNullOrEmpty()) {
                            val direccionTexto = addresses[0].getAddressLine(0)
                            _uiState.update { it.copy(direccion = direccionTexto, cargandoUbicacion = false) }
                        } else {
                            _uiState.update { it.copy(direccion = "Lat: ${location.latitude}, Lon: ${location.longitude}", cargandoUbicacion = false) }
                        }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(direccion = "Ubicación detectada (Sin dirección postal)", cargandoUbicacion = false) }
                    }
                }
            } else {
                _uiState.update { it.copy(direccion = "No se pudo detectar. Activa el GPS.", cargandoUbicacion = false) }
            }
        }.addOnFailureListener { e ->
            _uiState.update { it.copy(direccion = "Error GPS: ${e.message}", cargandoUbicacion = false) }
        }
    }

    fun enviarSolicitud() {
        val s = _uiState.value
        if (s.dispositivo.isBlank() || s.descripcion.isBlank() || s.direccion.isBlank()) {
            _uiState.update { it.copy(mensaje = "Faltan datos por completar") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(enviandoSolicitud = true, mensaje = null) }

            delay(2000)

            val hoy = LocalDate.now()
            val fechaInicio = hoy.plusDays(1)
            val fechaFin = hoy.plusDays(7)
            val formatter = DateTimeFormatter.ofPattern("dd/MM")

            val mensajeFechas = "Retiro programado entre el ${fechaInicio.format(formatter)} y el ${fechaFin.format(formatter)}"

            _uiState.update {
                it.copy(
                    enviandoSolicitud = false,
                    solicitudEnviada = true,
                    mensaje = "¡Solicitud Exitosa!\n$mensajeFechas"
                )
            }
        }
    }

    fun limpiarMensaje() { _uiState.update { it.copy(mensaje = null, solicitudEnviada = false, dispositivo = "", descripcion = "", direccion = "") } }
}