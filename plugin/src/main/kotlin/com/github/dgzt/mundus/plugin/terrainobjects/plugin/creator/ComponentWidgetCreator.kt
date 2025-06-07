package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.badlogic.gdx.Gdx
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.TextureGrid
import com.mbrlabs.mundus.pluginapi.ui.TextureGridListener
import com.mbrlabs.mundus.pluginapi.ui.WidgetAlign

object ComponentWidgetCreator {

    fun setup(component: TerrainObjectsComponent, rootWidget: RootWidget) {
        rootWidget.addLabel("Objects:").setAlign(WidgetAlign.LEFT)
        rootWidget.addRow()
        var textureGrid: TextureGrid? = null

        val textureGridListener = object : TextureGridListener {
            override fun onChange(pos: Int) {
                Gdx.app.log(PluginConstants.LOG_TAG, "Change: $pos")
            }

            override fun onRemove(pos: Int) {
                Gdx.app.log(PluginConstants.LOG_TAG, "Remove: $pos")
            }
        }

        textureGrid = rootWidget.addTextureGrid(true, true, textureGridListener).widget
        rootWidget.addRow()
        rootWidget.addTextButton("Add Object") {
            rootWidget.showModelAssetSelectionDialog {
                textureGrid.addTexture(it)
            }
        }.setAlign(WidgetAlign.RIGHT)
    }
}
