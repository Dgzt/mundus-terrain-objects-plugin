package com.github.dgzt.mundus.plugin.terrainobjects.plugin

import com.mbrlabs.mundus.pluginapi.MenuExtension
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import org.pf4j.Extension
import org.pf4j.Plugin

class TerrainObjectsPlugin : Plugin() {

    @Extension
    class TerrainObjectsMenuExtension : MenuExtension {

        companion object {
            const val PAD = 5f
        }

        override fun getMenuName(): String = "Terrain Objects plugin"

        override fun setupDialogRootWidget(root: RootWidget) {
            root.addLabel("Terrain Objects plugin").setPad(PAD, PAD, PAD, PAD)
        }

    }

}