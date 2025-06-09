package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.PropertyManager
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.transformer.TerrainObjectsTransformer
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

                if (component.customAsset == null) {
                    val tmpDir = System.getProperty("java.io.tmpdir")
                    val tmpFile = FileHandle("$tmpDir/${component.gameObject.name}.terrainobjects")
                    saveComponent(component, tmpFile)
                    component.customAsset = PropertyManager.assetManager.createNewAsset(tmpFile)
                    tmpFile.delete()
                }

                component.modelAssets.add(it)
                PropertyManager.assetManager.markAsModifiedAsset(component.customAsset) {
                    Gdx.app.debug(PluginConstants.LOG_TAG, "Save terrain objects asset for terrain: ${component.gameObject.name}")
                    saveComponent(component, component.customAsset.file)
                }
            }
        }.setAlign(WidgetAlign.RIGHT)
    }

    private fun saveComponent(component: TerrainObjectsComponent, file: FileHandle) {
        val assetJson = PropertyManager.json.toJson(TerrainObjectsTransformer.convertToDTO(component))
        file.writeString(assetJson, false)
    }
}
