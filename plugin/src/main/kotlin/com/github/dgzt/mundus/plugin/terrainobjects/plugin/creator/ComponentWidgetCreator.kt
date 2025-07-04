package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.PropertyManager
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.model.SelectedModel
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils.AssetUtils
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils.GameObjectUtils
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsAsset
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.AbstractTerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsManagerComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.manager.TerrainObjectsLayerManager
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject
import com.mbrlabs.mundus.commons.assets.CustomAsset
import com.mbrlabs.mundus.commons.assets.ModelAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editorcommons.assets.EditorModelAsset
import com.mbrlabs.mundus.pluginapi.listener.ToolListener
import com.mbrlabs.mundus.pluginapi.manager.GameObjectPickerManager
import com.mbrlabs.mundus.pluginapi.ui.CustomAssetFilter
import com.mbrlabs.mundus.pluginapi.ui.CustomAssetSelectionDialogListener
import com.mbrlabs.mundus.pluginapi.ui.Label
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.TextureGrid
import com.mbrlabs.mundus.pluginapi.ui.TextureGridListener
import com.mbrlabs.mundus.pluginapi.ui.WidgetAlign

object ComponentWidgetCreator {

    fun setup(component: AbstractTerrainObjectsComponent, rootWidget: RootWidget) {
        if (component is TerrainObjectsManagerComponent) {
            setupTerrainObjectsManagerWidget(component, rootWidget)
        } else if (component is TerrainObjectsComponent) {
            setupTerrainObjectsWidget(component, rootWidget)
        } else {
            throw UnsupportedOperationException("Unsupported component!")
        }
    }

    private fun setupTerrainObjectsManagerWidget(component: TerrainObjectsManagerComponent, rootWidget: RootWidget) {
        setupTerrainObjectsLayerWidget(component, TerrainManagerTextureGridListenerImpl(component, rootWidget), rootWidget)
    }

    private fun setupTerrainObjectsWidget(component: TerrainObjectsComponent, rootWidget: RootWidget) {
        val selectedModel = SelectedModel()

        val textureGridListener = TextureGridListenerImpl(component, selectedModel, rootWidget)

        setupTerrainObjectsLayerWidget(component, textureGridListener, rootWidget)
        rootWidget.addRow()

        // Object grid

        rootWidget.addRow()
        textureGridListener.objectButtonPanel = rootWidget.addEmptyWidget().rootWidget
    }

    private fun setupTerrainObjectsLayerWidget(component: AbstractTerrainObjectsComponent, textureGridListener: BaseTextureGridListenerImpl, rootWidget: RootWidget) {
        val terrainObjectsLayerAsset = component.terrainObjectsLayerAsset

        rootWidget.addLabel("Object layer:").setAlign(WidgetAlign.LEFT)
        rootWidget.addRow()
        val objectLayerWidgetCell = rootWidget.addEmptyWidget()
        objectLayerWidgetCell.grow()
        objectLayerWidgetCell.setPad(0f, 0f, 0f, 5f)
        val terrainObjectsLayerLabel = objectLayerWidgetCell.rootWidget.addLabel(terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset.name)
        terrainObjectsLayerLabel.grow().setAlign(WidgetAlign.LEFT)
        objectLayerWidgetCell.rootWidget.addTextButton("Duplicate") {
            duplicateTerrainObjectsLayer(component, terrainObjectsLayerAsset, rootWidget, terrainObjectsLayerLabel.label)
        }.setPad(0f, 5f, 0f, 0f).setAlign(WidgetAlign.RIGHT)
        objectLayerWidgetCell.rootWidget.addTextButton("Change") {
            rootWidget.showCustomAssetSelectionDialog(CustomAssetFilterImpl(), CustomAssetSelectionDialogListenerImpl(component.gameObject, terrainObjectsLayerLabel.label))
        }.setAlign(WidgetAlign.RIGHT)
        rootWidget.addRow()

        rootWidget.addLabel("Objects:").setAlign(WidgetAlign.LEFT)
        rootWidget.addRow()
        var textureGrid: TextureGrid


        textureGrid = rootWidget.addTextureGrid(true, true, textureGridListener).widget
        setupTextureGridTextures(textureGrid, terrainObjectsLayerAsset)
        textureGridListener.textureGrid = textureGrid

        rootWidget.addRow()
        rootWidget.addTextButton("Add Object") {
            rootWidget.showModelAssetSelectionDialog {
                textureGrid.addTexture(it)

                terrainObjectsLayerAsset.modelAssets.add(it)
                PropertyManager.assetManager.markAsModifiedAsset(terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset) {
                    Gdx.app.debug(PluginConstants.LOG_TAG, "Save terrain objects layer asset: ${terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset.name}")
                    saveTerrainObjectsLayerAsset(terrainObjectsLayerAsset)
                }
            }
        }.setAlign(WidgetAlign.RIGHT)
    }

    private fun setupTextureGridTextures(textureGrid: TextureGrid, terrainObjectsLayerAsset: TerrainObjectsLayerAsset) {
        for (i in 0 until terrainObjectsLayerAsset.modelAssets.size) {
            val model = terrainObjectsLayerAsset.modelAssets.get(i) as EditorModelAsset
            textureGrid.addTexture(model)
        }
    }

    private fun saveTerrainObjectsLayerAsset(terrainObjectsLayerAsset: TerrainObjectsLayerAsset) {
        val modelAssetIds = Array<String>()
        for (modelAsset in terrainObjectsLayerAsset.modelAssets) {
            modelAssetIds.add(modelAsset.id)
        }

        val json = PropertyManager.json.toJson(modelAssetIds)

        terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset.file.writeString(json, false)
    }

    private fun saveTerrainObjectsAsset(terrainObjectsAsset: TerrainObjectsAsset) {
        val json = PropertyManager.json.toJson(terrainObjectsAsset.terrainObjects, Array::class.java, TerrainObject::class.java)
        terrainObjectsAsset.terrainObjectsCustomAsset.file.writeString(json, false)
    }

    private fun setupObjectsButtonPanel(terrainObjectsComponent: TerrainObjectsComponent, gameObject: GameObject, objectButtonPanel: RootWidget, selectedModel: SelectedModel) {
        val buttonsPanel = objectButtonPanel.addEmptyWidget().rootWidget
        objectButtonPanel.addRow()
        val optionsPanel = objectButtonPanel.addEmptyWidget().rootWidget

        buttonsPanel.addTextButton("Add") {
            optionsPanel.clearWidgets()
            setupAddOptionPanel(selectedModel, optionsPanel)

            PropertyManager.toolManager.activateCustomTool(AddToolListenerImpl(gameObject, terrainObjectsComponent, selectedModel))
        }.setPad(0f, 20f, 0f, 0f)

        buttonsPanel.addTextButton("Remove") {
            optionsPanel.clearWidgets()
            setupDeleteOptionPanel(optionsPanel)
            PropertyManager.toolManager.activateCustomTool(DeleteToolListenerImpl())
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

    private fun setupDeleteOptionPanel(rootWidget: RootWidget) {
        val defaultRadius = 25f

        PropertyManager.terrainPickerManager.setRadius(defaultRadius)
        rootWidget.addSpinner("Radius", 0.1f, Float.MAX_VALUE, defaultRadius, 1f) {
            PropertyManager.terrainPickerManager.setRadius(it)
        }
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

    private fun duplicateTerrainObjectsLayer(
        component: AbstractTerrainObjectsComponent,
        terrainObjectsLayerAsset: TerrainObjectsLayerAsset,
        rootWidget: RootWidget,
        terrainObjectsLayerLabel: Label
    ) {
        rootWidget.showStringInputDialog("Name:") {
            val newCustomAsset = AssetUtils.createTerrainObjectsLayerAsset(it)
            val newTerrainObjectLayerAsset = TerrainObjectsLayerAsset(newCustomAsset)
            newTerrainObjectLayerAsset.modelAssets.addAll(terrainObjectsLayerAsset.modelAssets)

            component.terrainObjectsLayerAsset = newTerrainObjectLayerAsset

            PropertyManager.assetManager.markAsModifiedAsset(newTerrainObjectLayerAsset.terrainObjectsLasetCustomAsset) {
                Gdx.app.debug(PluginConstants.LOG_TAG, "Save terrain objects layer asset: ${terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset.name}")
                saveTerrainObjectsLayerAsset(terrainObjectsLayerAsset)
            }

            terrainObjectsLayerLabel.setText(component.terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset.name)
        }
    }

    private fun changeModelInTerrainObjectsLayerAsset(
        textureGrid: TextureGrid,
        terrainObjectsLayerAsset: TerrainObjectsLayerAsset,
        newModel: ModelAsset,
        pos: Int
    ) {
        terrainObjectsLayerAsset.modelAssets.removeIndex(pos)
        terrainObjectsLayerAsset.modelAssets.insert(pos, newModel)

        PropertyManager.assetManager.markAsModifiedAsset(terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset) {
            saveTerrainObjectsLayerAsset(terrainObjectsLayerAsset)
        }

        textureGrid.removeTextures()
        setupTextureGridTextures(textureGrid, terrainObjectsLayerAsset)

        val components = PropertyManager.sceneGraph.root.findComponentsByType(Array<AbstractTerrainObjectsComponent>(), PluginConstants.TYPE, true)
        for (c in components) {
            if (c is TerrainObjectsComponent && c.terrainObjectsLayerAsset == terrainObjectsLayerAsset) {
                c.updateTerrainObjects(true)
            }
        }
    }

    private fun removeModelInTerrainObjectsLayerAsset(
        textureGrid: TextureGrid,
        terrainObjectsLayerAsset: TerrainObjectsLayerAsset,
        pos: Int
    ) {
        terrainObjectsLayerAsset.modelAssets.removeIndex(pos)

        PropertyManager.assetManager.markAsModifiedAsset(terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset) {
            saveTerrainObjectsLayerAsset(terrainObjectsLayerAsset)
        }

        textureGrid.removeTextures()
        setupTextureGridTextures(textureGrid, terrainObjectsLayerAsset)

        val components = PropertyManager.sceneGraph.root.findComponentsByType(Array<AbstractTerrainObjectsComponent>(), PluginConstants.TYPE, true)

        for (c in components) {
            if (c is TerrainObjectsComponent && c.terrainObjectsLayerAsset == terrainObjectsLayerAsset) {
                val terrainObjectsAsset = c.terrainObjectsAsset
                var modified = false

                for (i in terrainObjectsAsset.terrainObjects.size - 1 downTo 0) {
                    val terrainObject = terrainObjectsAsset.terrainObjects.get(i)

                    if (terrainObject.modelPos == pos) {
                        terrainObjectsAsset.terrainObjects.removeIndex(i)
                        modified = true
                    } else if (pos < terrainObject.modelPos) {
                        terrainObject.modelPos -= 1
                        modified = true
                    }
                }

                if (modified) {
                    c.updateTerrainObjects(true)
                    PropertyManager.assetManager.markAsModifiedAsset(terrainObjectsAsset.terrainObjectsCustomAsset) {
                        Gdx.app.debug(PluginConstants.LOG_TAG, "Save terrain objects asset: ${terrainObjectsAsset.terrainObjectsCustomAsset.name}")
                        saveTerrainObjectsAsset(terrainObjectsAsset)
                    }
                }
            }
        }
    }

    private class CustomAssetFilterImpl : CustomAssetFilter {
        override fun isVisible(customAsset: CustomAsset): Boolean {
            val properties = customAsset.properties
            return properties.containsKey(PluginConstants.CUSTOM_ASSET_TYPE_KEY)
                    && PluginConstants.CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS_LAYER == properties.get(PluginConstants.CUSTOM_ASSET_TYPE_KEY)
        }
    }

    private class CustomAssetSelectionDialogListenerImpl(val gameObject: GameObject, val terrainObjectsLayerLabel: Label) : CustomAssetSelectionDialogListener {
        override fun onSelected(customAsset: CustomAsset) {
            // TODO handle different objects num

            val terrainObjectsLayerAsset = TerrainObjectsLayerManager.getByKey(customAsset.id)

            if (GameObjectUtils.isTerrainManagerGameObject(gameObject)) {
                val components = gameObject.findComponentsByType(Array<AbstractTerrainObjectsComponent>(), PluginConstants.TYPE, true)
                for (component in components) {
                    component.terrainObjectsLayerAsset = terrainObjectsLayerAsset
                    if (component is TerrainObjectsComponent) {
                        component.updateTerrainObjects(true)
                    }
                }
            } else {
                val component = gameObject.findComponentByType<TerrainObjectsComponent>(PluginConstants.TYPE)
                component.terrainObjectsLayerAsset = terrainObjectsLayerAsset
                component.updateTerrainObjects(true)
            }

            terrainObjectsLayerLabel.setText(terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset.name)
        }
    }

    private class TerrainManagerTextureGridListenerImpl(
        val component: TerrainObjectsManagerComponent,
        val rootWidget: RootWidget
    ) : BaseTextureGridListenerImpl() {
        private var selectedPos = -1

        override fun onSelect(pos: Int) {
            Gdx.app.log(PluginConstants.LOG_TAG, "Select: $pos")
            selectedPos = pos
        }

        override fun onChange(pos: Int) {
            rootWidget.showModelAssetSelectionDialog {
                Gdx.app.debug(PluginConstants.LOG_TAG, "Change $pos. model to ${it.name}")
                changeModelInTerrainObjectsLayerAsset(textureGrid, component.terrainObjectsLayerAsset, it, pos)
            }
        }

        override fun onRemove(pos: Int) {
            Gdx.app.debug(PluginConstants.LOG_TAG, "Remove $pos. model")
            removeModelInTerrainObjectsLayerAsset(textureGrid, component.terrainObjectsLayerAsset, pos)
        }

    }

    private class TextureGridListenerImpl(
        val component: TerrainObjectsComponent,
        val selectedModel: SelectedModel,
        val rootWidget: RootWidget
    ) : BaseTextureGridListenerImpl() {
        lateinit var objectButtonPanel: RootWidget

        override fun onSelect(pos: Int) {
            Gdx.app.log(PluginConstants.LOG_TAG, "Select: $pos")
            selectedModel.pos = pos
            selectedModel.modelAsset = component.terrainObjectsLayerAsset.modelAssets.get(pos)

            objectButtonPanel.clearWidgets()
            setupObjectsButtonPanel(component, component.gameObject, objectButtonPanel, selectedModel)
        }

        override fun onChange(pos: Int) {
            rootWidget.showModelAssetSelectionDialog {
                Gdx.app.debug(PluginConstants.LOG_TAG, "Change $pos. model to ${it.name}")
                changeModelInTerrainObjectsLayerAsset(textureGrid, component.terrainObjectsLayerAsset, it, pos)

                objectButtonPanel.clearWidgets()
                PropertyManager.toolManager.deactivateCustomTool()
            }
        }

        override fun onRemove(pos: Int) {
            Gdx.app.debug(PluginConstants.LOG_TAG, "Remove $pos. model")
            removeModelInTerrainObjectsLayerAsset(textureGrid, component.terrainObjectsLayerAsset, pos)

            objectButtonPanel.clearWidgets()
            PropertyManager.toolManager.deactivateCustomTool()
        }
    }

    private abstract class BaseTextureGridListenerImpl : TextureGridListener {
        lateinit var textureGrid: TextureGrid
    }

    private class AddToolListenerImpl(gameObject: GameObject, val terrainObjectsComponent: TerrainObjectsComponent, val selectedModel: SelectedModel) : ToolListener {

        private val terrainComponent = gameObject.findComponentByType<TerrainComponent>(Component.Type.TERRAIN)
        private var tmpVector3 = Vector3()
        private var tmpMatrix4 = Matrix4()

        init {
            PropertyManager.selectedModelInstance = ModelInstance(selectedModel.modelAsset.model)
        }

        override fun touchDown(screenX: Int, screenY: Int, buttonId: Int) {
            val terrainObject = TerrainObject(
                selectedModel.pos,
                selectedModel.posX, selectedModel.posY, selectedModel.posZ,
                selectedModel.rotationX, selectedModel.rotationY, selectedModel.rotationZ,
                selectedModel.scaleX, selectedModel.scaleY, selectedModel.scaleZ
            )

            terrainObjectsComponent.addTerrainObject(terrainObject)

            val terrainObjectsAsset = terrainObjectsComponent.terrainObjectsAsset
            PropertyManager.assetManager.markAsModifiedAsset(terrainObjectsAsset.terrainObjectsCustomAsset) {
                Gdx.app.debug(PluginConstants.LOG_TAG, "Save terrain objects asset: ${terrainObjectsAsset.terrainObjectsCustomAsset.name}")
                saveTerrainObjectsAsset(terrainObjectsAsset)
            }
        }

        override fun mouseMoved(screenX: Int, screenY: Int) {
            val ray = PropertyManager.viewportManager.getPickRay(screenX.toFloat(), screenY.toFloat())
            tmpVector3 = terrainComponent.getRayIntersection(tmpVector3, ray)

            // convert to local coordinate
            tmpVector3.mul(tmpMatrix4.set(terrainComponent.modelInstance.transform).inv())

            selectedModel.posX = tmpVector3.x
            selectedModel.posY = tmpVector3.y
            selectedModel.posZ = tmpVector3.z

            applyTransformations(selectedModel)
        }

        override fun onDisabled() {
            PropertyManager.selectedModelInstance = null
        }

    }

    private class DeleteToolListenerImpl : ToolListener {

        private val brushPos = Vector3()

        init {
            PropertyManager.terrainPickerManager.activate(true)
        }

        override fun touchDown(screenX: Int, screenY: Int, buttonId: Int) {
            // NOOP
        }

        override fun mouseMoved(screenX: Int, screenY: Int) {
            val go = PropertyManager.gameObjectPickerManager.pick(screenX, screenY, GameObjectPickerManager.ComponentType.TERRAIN)
            if (go != null) {
                val terrainComponent = go.findComponentByType<TerrainComponent>(Component.Type.TERRAIN)
                val ray = PropertyManager.viewportManager.getPickRay(screenX.toFloat(), screenY.toFloat())
                terrainComponent.getRayIntersection(brushPos, ray)

                PropertyManager.terrainPickerManager.setPosition(brushPos.x, brushPos.y, brushPos.z)
            }
        }

        override fun onDisabled() {
            PropertyManager.terrainPickerManager.activate(false)
        }

    }
}
