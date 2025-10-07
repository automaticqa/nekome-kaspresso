package com.chesire.nekome.kaspresso

import android.app.Application
import android.content.Context
import com.kaspersky.kaspresso.runner.KaspressoRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom Kaspresso Test Runner
 */
class KaspressoTestRunner : KaspressoRunner() {

    @Throws(
        InstantiationException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class
    )
    override fun newApplication(
        cl: ClassLoader,
        className: String,
        context: Context
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}