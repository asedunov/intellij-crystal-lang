package org.crystal.intellij.config

enum class LanguageLevel(val shortName: String) {
    CRYSTAL_1_0("1.0"),
    CRYSTAL_1_1("1.1"),
    CRYSTAL_PREVIEW("Preview");

    companion object {
        val LATEST_STABLE = CRYSTAL_1_1
    }
}