package com.android.test.starwars.data.networkresource

sealed class NetworkStatus<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) :
        NetworkStatus<T>(data)

    class Error<T>(message: String?, data: T? = null) :
        NetworkStatus<T>(data, message)

    class Loading<T>(data: T? = null) : NetworkStatus<T>(data)
}

data class ApiResponse<T>(var code: Int = 0, var data: T?, var message: String? = "")