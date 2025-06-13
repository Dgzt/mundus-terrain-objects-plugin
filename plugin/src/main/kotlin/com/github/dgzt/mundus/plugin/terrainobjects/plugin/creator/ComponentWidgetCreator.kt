package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.PropertyManager
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.model.SelectedModel
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.transformer.TerrainObjectsTransformer
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editorcommons.assets.EditorModelAsset
import com.mbrlabs.mundus.pluginapi.listener.ToolListener
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.TextureGrid
import com.mbrlabs.mundus.pluginapi.ui.TextureGridListener
import com.mbrlabs.mundus.pluginapi.ui.WidgetAlign

object ComponentWidgetCreator {

    fun setup(component: TerrainObjectsComponent, rootWidget: RootWidget) {
        val selectedModel = SelectedModel()

        rootWidget.addLabel("Objects:").setAlign(WidgetAlign.LEFT)
        rootWidget.addRow()
        var textureGrid: TextureGrid

        val textureGridListener = TextureGridListenerImpl(component, selectedModel)
        textureGrid = rootWidget.addTextureGrid(true, true, textureGridListener).widget
        setupTextureGridWidget(component, textureGrid)
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
        rootWidget.addRow()
        textureGridListener.objectButtonPanel = rootWidget.addEmptyWidget().rootWidget
    }

    private fun setupTextureGridWidget(component: TerrainObjectsComponent, textureGrid: TextureGrid) {
        for (i in 0 until component.modelAssets.size) {
            val model = component.modelAssets.get(i) as EditorModelAsset
            textureGrid.addTexture(model)
        }
    }

    private fun saveComponent(component: TerrainObjectsComponent, file: FileHandle) {
        val assetJson = PropertyManager.json.toJson(TerrainObjectsTransformer.convertToDTO(component))
        file.writeString(assetJson, false)
    }

    private fun setupObjectsButtonPanel(gameObject: GameObject, objectButtonPanel: RootWidget, selectedModel: SelectedModel) {
        val buttonsPanel = objectButtonPanel.addEmptyWidget().rootWidget
        objectButtonPanel.addRow()
        val optionsPanel = objectButtonPanel.addEmptyWidget().rootWidget

        buttonsPanel.addTextButton("Add") {
            optionsPanel.clearWidgets()
            setupAddOptionPanel(selectedModel, optionsPanel)

            PropertyManager.toolManager.activateCustomTool(ToolListenerImpl(gameObject, selectedModel))
        }.setPad(0f, 20f, 0f, 0f)

        buttonsPanel.addTextButton("Remove") {
            optionsPanel.clearWidgets()
            // TODO
        }
    }

    private fun setupAddOptionPanel(selectedModel: SelectedModel, rootWidget: RootWidget) {
        // rotation
        rootWidget.addLabel("Rotation")
        rootWidget.addRow()
        rootWidget.addLabel("X").setPad(0f, 5f, 0f, 5f)
        rootWidget.addSliderWithSpinnerWidget(0f, 359.9f, 0.1f, 1) {
            selectedModel.rotationX = it
            applyTransformations(selectedModel)
        }
        rootWidget.addRow()
        rootWidget.addLabel("Y").setPad(0f, 5f, 0f, 5f)
        rootWidget.addSliderWithSpinnerWidget(0f, 359.9f, 0.1f, 1) {
            selectedModel.rotationY = it
            applyTransformations(selectedModel)
        }
        rootWidget.addRow()
        rootWidget.addLabel("Z").setPad(0f, 5f, 0f, 5f)
        rootWidget.addSliderWithSpinnerWidget(0f, 359.9f, 0.1f, 1) {
            selectedModel.rotationZ = it
            applyTransformations(selectedModel)
        }
        rootWidget.addRow()

        // scale
        rootWidget.addLabel("Scale")
        rootWidget.addRow()
        rootWidget.addSpinner("X", 0.1f, Float.MAX_VALUE, 1.0f, 0.1f) {
            selectedModel.scaleX = it
            applyTransformations(selectedModel)
        }.setPad(0f, 5f, 0f, 0f)
        rootWidget.addRow()
        rootWidget.addSpinner("Y", 0.1f, Float.MAX_VALUE, 1.0f, 0.1f) {
            selectedModel.scaleY = it
            applyTransformations(selectedModel)
        }.setPad(0f, 5f, 0f, 0f)
        rootWidget.addRow()
        rootWidget.addSpinner("Z", 0.1f, Float.MAX_VALUE, 1.0f, 0.1f) {
            selectedModel.scaleZ = it
            applyTransformations(selectedModel)
        }.setPad(0f, 5f, 0f, 0f)

    }

    private fun applyTransformations(selectedModel: SelectedModel) {
        // Position
        PropertyManager.selectedModelInstance?.transform?.idt()
        PropertyManager.selectedModelInstance?.transform?.translate(selectedModel.posX, selectedModel.posY, selectedModel.posZ)

        // Rotation
        val quaternion = Quaternion()
        quaternion.setEulerAngles(selectedModel.rotationY, selectedModel.rotationX, selectedModel.rotationZ)
        PropertyManager.selectedModelInstance?.transform?.rotate(quaternion)

        // Scaling
        PropertyManager.selectedModelInstance?.transform?.scale(selectedModel.scaleX, selectedModel.scaleY, selectedModel.scaleZ)
    }

    private class TextureGridListenerImpl(
        val component: TerrainObjectsComponent,
        val selectedModel: SelectedModel
    ) : TextureGridListener {
        lateinit var objectButtonPanel: RootWidget

        override fun onSelect(pos: Int) {
            Gdx.app.log(PluginConstants.LOG_TAG, "Select: $pos")
            selectedModel.pos = pos
            selectedModel.modelAsset = component.modelAssets.get(pos)

            objectButtonPanel.clearWidgets()
            setupObjectsButtonPanel(component.gameObject, objectButtonPanel, selectedModel)
        }

        override fun onChange(pos: Int) {
            Gdx.app.log(PluginConstants.LOG_TAG, "Change: $pos")
        }

        override fun onRemove(pos: Int) {
            Gdx.app.log(PluginConstants.LOG_TAG, "Remove: $pos")
        }
    }

    private class ToolListenerImpl(gameObject: GameObject, val selectedModel: SelectedModel) : ToolListener {

        private val terrainComponent = gameObject.findComponentByType<TerrainComponent>(Component.Type.TERRAIN)
        private var tmpVector3 = Vector3()

        init {
            PropertyManager.selectedModelInstance = ModelInstance(selectedModel.modelAsset.model)
        }

        override fun touchDown(screenX: Int, screenY: Int, buttonId: Int) {
            Gdx.app.log(PluginConstants.LOG_TAG, "touchDown: $screenX x $screenY - $buttonId")
        }

        override fun mouseMoved(screenX: Int, screenY: Int) {
            val ray = PropertyManager.viewportManager.getPickRay(screenX.toFloat(), screenY.toFloat())
            tmpVector3 = terrainComponent.getRayIntersection(tmpVector3, ray)

            selectedModel.posX = tmpVector3.x
            selectedModel.posY = tmpVector3.y
            selectedModel.posZ = tmpVector3.z

            applyTransformations(selectedModel)
        }

        override fun onDisabled() {
            PropertyManager.selectedModelInstance = null
        }

    }
}
