package org.crystal.intellij.ide.config.project

import com.intellij.openapi.module.ModuleType
import org.crystal.intellij.CrystalIcons
import javax.swing.Icon

object CrystalModuleType : ModuleType<CrystalModuleBuilder>("CRYSTAL_MODULE") {
    override fun getNodeIcon(isOpened: Boolean): Icon = CrystalIcons.LANGUAGE

    override fun createModuleBuilder(): CrystalModuleBuilder = CrystalModuleBuilder()

    override fun getDescription(): String = "Crystal module"

    override fun getName(): String = "Crystal"
}
