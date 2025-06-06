package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.WidgetAlign

object ComponentWidgetCreator {

    fun setup(component: TerrainObjectsComponent, rootWidget: RootWidget) {
        rootWidget.addLabel("Objects:").setAlign(WidgetAlign.LEFT)
        rootWidget.addRow()
        val textureGrid = rootWidget.addTextureGrid().widget
        rootWidget.addRow()
        rootWidget.addTextButton("Add Object") {
            rootWidget.showModelAssetSelectionDialog {
                textureGrid.addTexture(it)
            }
        }.setAlign(WidgetAlign.RIGHT)
    }
}
