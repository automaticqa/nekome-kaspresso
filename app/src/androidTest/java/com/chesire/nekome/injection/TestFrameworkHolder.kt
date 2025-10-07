package com.chesire.nekome.injection

enum class TestFramework {
    BARISTA,
    KASPRESSO
}

object TestFrameworkHolder {
    @Volatile
    var framework: TestFramework = TestFramework.KASPRESSO
}