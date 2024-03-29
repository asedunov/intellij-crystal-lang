package org.crystal.intellij.lang.config

enum class CrystalLevel(val shortName: String) {
    CRYSTAL_1_0("1.0"),
    CRYSTAL_1_1("1.1"),
    CRYSTAL_1_2("1.2"),
    CRYSTAL_1_3("1.3"),
    CRYSTAL_1_4("1.4"),
    CRYSTAL_1_5("1.5"),
    CRYSTAL_1_6("1.6"),
    CRYSTAL_1_7("1.7"),
    CRYSTAL_1_8("1.8"),
    CRYSTAL_1_9("1.9"),
    CRYSTAL_1_10("1.10"),
    CRYSTAL_PREVIEW("Preview");

    companion object {
        @JvmField
        val LATEST_STABLE = CRYSTAL_1_10
    }
}