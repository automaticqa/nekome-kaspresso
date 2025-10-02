package com.chesire.nekome.kaspresso.common

object NetworkModeHolder {
    @Volatile
    var mode: NetworkMode = NetworkMode.ONLINE
}