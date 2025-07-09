package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.PropertyManager
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.model.AddActionModel
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.model.DeleteActionModel
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.model.SelectedModel
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils.AssetUtils
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils.GameObjectUtils
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

        val json = PropertyManager.json.toJson(modelAssetIds, Array::class.java, String::class.java)

        terrainObjectsLayerAsset.terrainObjectsLasetCustomAsset.file.writeString(json, false)
    }

    private fun saveTerrainObjectsAsset(terrainObjectsComponent: TerrainObjectsComponent) {
        val terrainObjectsAsset = terrainObjectsComponent.terrainObjectsAsset

        val json = PropertyManager.json.toJson(terrainObjectsAsset.terrainObjects, Array::class.java, TerrainObject::class.java)
        terrainObjectsAsset.terrainObjectsCustomAsset.file.writeString(json, false)
    }

    private fun setupObjectsButtonPanel(terrainObjectsComponent: TerrainObjectsComponent, gameObject: GameObject, objectButtonPanel: RootWidget, selectedModel: SelectedModel) {
        val buttonsPanel = objectButtonPanel.addEmptyWidget().rootWidget
        objectButtonPanel.addRow()
        val optionsPanel = objectButtonPanel.addEmptyWidget().rootWidget

        buttonsPanel.addTextButton("Add") {
            optionsPanel.clearWidgets()
            selectedModel.actionModel = AddActionModel()
            setupAddOptionPanel(selectedModel, optionsPanel)

            PropertyManager.toolManager.activateCustomTool(AddToolListenerImpl(gameObject, terrainObjectsComponent, selectedModel))
        }.setPad(0f, 20f, 0f, 0f)

        buttonsPanel.addTextButton("Remove") {
            optionsPanel.clearWidgets()
            selectedModel.actionModel = DeleteActionModel()
            setupDeleteOptionPanel(selectedModel, optionsPanel)

            PropertyManager.toolManager.activateCustomTool(DeleteToolListenerImpl(terrainObjectsComponent, selectedModel))
        }

        if (selectedModel.isActionModelInitialized()) {
            if (selectedModel.actionModel is AddActionModel) {
                setupAddOptionPanel(selectedModel, optionsPanel)
            } else if (selectedModel.actionModel is DeleteActionModel) {
                setupDeleteOptionPanel(selectedModel, optionsPanel)
            }
        }
    }

    private fun setupAddOptionPanel(selectedModel: SelectedModel, rootWidget: RootWidget) {
        val addActionModel = selectedModel.actionModel as AddActionModel

        // rotation
        rootWidget.addLabel("Rotation")
        rootWidget.addRow()
        rootWidget.addLabel("X").setPad(0f, 5f, 0f, 5f)
        rootWidget.addSliderWithSpinnerWidget(0f, 359.9f, 0.1f, 1) { // TODO set init value
            addActionModel.rotationX = it
            applySelectedModelTransformations(selectedModel)
        }
        rootWidget.addRow()
        rootWidget.addLabel("Y").setPad(0f, 5f, 0f, 5f)
        rootWidget.addSliderWithSpinnerWidget(0f, 359.9f, 0.1f, 1) { // TODO set init value
            addActionModel.rotationY = it
            applySelectedModelTransformations(selectedModel)
        }
        rootWidget.addRow()
        rootWidget.addLabel("Z").setPad(0f, 5f, 0f, 5f)
        rootWidget.addSliderWithSpinnerWidget(0f, 359.9f, 0.1f, 1) { // TODO set init value
            addActionModel.rotationZ = it
            applySelectedModelTransformations(selectedModel)
        }
        rootWidget.addRow()

        // scale
        rootWidget.addLabel("Scale")
        rootWidget.addRow()
        rootWidget.addSpinner("X", 0.1f, Float.MAX_VALUE, addActionModel.scaleX, 0.1f) {
            addActionModel.scaleX = it
            applySelectedModelTransformations(selectedModel)
        }.setPad(0f, 5f, 0f, 0f)
        rootWidget.addRow()
        rootWidget.addSpinner("Y", 0.1f, Float.MAX_VALUE, addActionModel.scaleY, 0.1f) {
            addActionModel.scaleY = it
            applySelectedModelTransformations(selectedModel)
        }.setPad(0f, 5f, 0f, 0f)
        rootWidget.addRow()
        rootWidget.addSpinner("Z", 0.1f, Float.MAX_VALUE, addActionModel.scaleZ, 0.1f) {
            addActionModel.scaleZ = it
            applySelectedModelTransformations(selectedModel)
        }.setPad(0f, 5f, 0f, 0f)
    }

    private fun setupDeleteOptionPanel(selectedModel: SelectedModel, rootWidget: RootWidget) {
        val deleteModel = selectedModel.actionModel as DeleteActionModel

        PropertyManager.terrainPickerManager.setRadius(deleteModel.radius)
        rootWidget.addSpinner("Radius", 0.1f, Float.MAX_VALUE, deleteModel.radius, 1f) {
            deleteModel.radius = it
            PropertyManager.terrainPickerManager.setRadius(deleteModel.radius)
        }
    }

    private fun applySelectedModelTransformations(selectedModel: SelectedModel) {
        val addActionModel = selectedModel.actionModel as AddActionModel

        // Position
        PropertyManager.selectedModelInstance?.transform?.idt()
        PropertyManager.selectedModelInstance?.transform?.translate(addActionModel.position.x, addActionModel.position.y, addActionModel.position.z)

        // Rotation
        val quaternion = Quaternion()
        quaternion.setEulerAngles(addActionModel.rotationY, addActionModel.rotationX, addActionModel.rotationZ)
        PropertyManager.selectedModelInstance?.transform?.rotate(quaternion)

        // Scaling
        PropertyManager.selectedModelInstance?.transform?.scale(addActionModel.scaleX, addActionModel.scaleY, addActionModel.scaleZ)
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
                        saveTerrainObjectsAsset(c)
                    }
                }
            }
        }
    }

    private fun removeTerrainObjects(terrainObjectsComponent: TerrainObjectsComponent, selectedModel: SelectedModel) {
        val deleteActionModel = selectedModel.actionModel as DeleteActionModel
        val terrainObjectsAsset = terrainObjectsComponent.terrainObjectsAsset
        val terrainObjects = terrainObjectsAsset.terrainObjects

        var modified = false

        for (i in terrainObjects.size - 1 downTo 0) {
            val terrainObject = terrainObjects.get(i)
            val distance = terrainObject.position.dst(deleteActionModel.position)

            if (selectedModel.pos == terrainObject.modelPos && distance <= deleteActionModel.radius) {
                terrainObjects.removeIndex(i)
                modified = true
            }
        }

        if (modified) {
            terrainObjectsComponent.updateTerrainObjects()

            PropertyManager.assetManager.markAsModifiedAsset(terrainObjectsAsset.terrainObjectsCustomAsset) {
                Gdx.app.debug(PluginConstants.LOG_TAG, "Save terrain objects asset: ${terrainObjectsAsset.terrainObjectsCustomAsset.name}")
                saveTerrainObjectsAsset(terrainObjectsComponent)
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

            if (PropertyManager.selectedModelInstance != null) {
                PropertyManager.selectedModelInstance = ModelInstance(selectedModel.modelAsset.model)
                applySelectedModelTransformations(selectedModel)
            }
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
        private val addActionModel = selectedModel.actionModel as AddActionModel
        private var tmpVector3 = Vector3()
        private var tmpMatrix4 = Matrix4()

        init {
            PropertyManager.selectedModelInstance = ModelInstance(selectedModel.modelAsset.model)
        }

        override fun touchDown(screenX: Int, screenY: Int, buttonId: Int) {
            val terrainObject = TerrainObject(
                selectedModel.pos,
                addActionModel.position.x, addActionModel.position.y, addActionModel.position.z,
                addActionModel.rotationX, addActionModel.rotationY, addActionModel.rotationZ,
                addActionModel.scaleX, addActionModel.scaleY, addActionModel.scaleZ
            )

            terrainObjectsComponent.addTerrainObject(terrainObject)

            val terrainObjectsAsset = terrainObjectsComponent.terrainObjectsAsset
            PropertyManager.assetManager.markAsModifiedAsset(terrainObjectsAsset.terrainObjectsCustomAsset) {
                Gdx.app.debug(PluginConstants.LOG_TAG, "Save terrain objects asset: ${terrainObjectsAsset.terrainObjectsCustomAsset.name}")
                saveTerrainObjectsAsset(terrainObjectsComponent)
            }
        }

        override fun touchDragged(p0: Int, p1: Int) {
            // NOOP
        }

        override fun mouseMoved(screenX: Int, screenY: Int) {
            val ray = PropertyManager.viewportManager.getPickRay(screenX.toFloat(), screenY.toFloat())
            tmpVector3 = terrainComponent.getRayIntersection(tmpVector3, ray)

            // convert to local coordinate
            tmpVector3.mul(tmpMatrix4.set(terrainComponent.modelInstance.transform).inv())

            addActionModel.position.x = tmpVector3.x
            addActionModel.position.y = tmpVector3.y
            addActionModel.position.z = tmpVector3.z

            applySelectedModelTransformations(selectedModel)
        }

        override fun onDisabled() {
            PropertyManager.selectedModelInstance = null
        }

    }

    private class DeleteToolListenerImpl(val terrainObjectsComponent: TerrainObjectsComponent, val selectedModel: SelectedModel) : ToolListener {

        private val terrainComponent = terrainObjectsComponent.gameObject.findComponentByType<TerrainComponent>(Component.Type.TERRAIN)
        private val deleteActionModel = selectedModel.actionModel as DeleteActionModel
        private val brushPos = Vector3()
        private var tmpMatrix4 = Matrix4()

        init {
            PropertyManager.terrainPickerManager.activate(true)
        }

        override fun touchDown(screenX: Int, screenY: Int, buttonId: Int) {
            // Set brush local position to terrain component
            deleteActionModel.position.set(brushPos).mul(tmpMatrix4.set(terrainComponent.modelInstance.transform).inv())
            removeTerrainObjects(terrainObjectsComponent, selectedModel)
        }

        override fun touchDragged(screenX: Int, screenY: Int) {
            mouseMoved(screenX, screenY)
            touchDown(screenX, screenY, -1)
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
