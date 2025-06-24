package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.PropertyManager
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils.GameObjectUtils
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsAsset
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.manager.TerrainObjectsLayerManager
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject
import com.mbrlabs.mundus.commons.assets.CustomAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject

object ComponentCreator {

    fun create(gameObject: GameObject): TerrainObjectsComponent {
        val component: TerrainObjectsComponent

        val terrainObjectsLayerAsset = TerrainObjectsLayerAsset(createTerrainObjectsLayerAsset(gameObject))
        TerrainObjectsLayerManager.register(terrainObjectsLayerAsset)

        if (GameObjectUtils.isTerrainManagerGameObject(gameObject)) {
            component = TerrainObjectsComponent(gameObject)
            component.terrainObjectsLayerAsset = terrainObjectsLayerAsset

            for (childGameObject in gameObject.children) {
                if (GameObjectUtils.isTerrainGameObject(childGameObject) && !GameObjectUtils.hasTerrainObjectsComponent(childGameObject)) {
                    val childGameObjectComponent = TerrainObjectsComponent(childGameObject)
                    childGameObjectComponent.terrainObjectsLayerAsset = terrainObjectsLayerAsset
                    childGameObjectComponent.terrainObjectsAsset = TerrainObjectsAsset(createTerrainObjectsAsset(childGameObject))

                    childGameObject.addComponent(childGameObjectComponent)
                }
            }
        } else if (GameObjectUtils.isTerrainGameObject(gameObject)) {
            component = TerrainObjectsComponent(gameObject)
            component.terrainObjectsLayerAsset = terrainObjectsLayerAsset
            component.terrainObjectsAsset = TerrainObjectsAsset(createTerrainObjectsAsset(gameObject))
        } else {
            throw UnsupportedOperationException("Not supported game object type!")
        }

        return component
    }

    private fun createTerrainObjectsLayerAsset(gameObject: GameObject): CustomAsset {
        val tmpDir = System.getProperty("java.io.tmpdir")
        val tmpFile = FileHandle("$tmpDir/${gameObject.name}.${PluginConstants.TERRAIN_OBJECTS_LAYER_EXTENSION}")

        tmpFile.writeString(PropertyManager.json.toJson(Array<String>()), false)

        val customAsset = PropertyManager.assetManager.createNewAsset(tmpFile)
        tmpFile.delete()

        customAsset.properties.put(PluginConstants.CUSTOM_ASSET_TYPE_KEY, PluginConstants.CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS_LAYER)
        PropertyManager.assetManager.markAsModifiedAsset(customAsset)

        return customAsset
    }

    private fun createTerrainObjectsAsset(gameObject: GameObject): CustomAsset {
        val tmpDir = System.getProperty("java.io.tmpdir")
        val tmpFile = FileHandle("$tmpDir/${gameObject.name}.${PluginConstants.TERRAIN_OBJECTS_EXTENSION}")
        tmpFile.writeString(PropertyManager.json.toJson(Array<TerrainObject>()), false)

        val customAsset = PropertyManager.assetManager.createNewAsset(tmpFile)
        tmpFile.delete()

        customAsset.properties.put(PluginConstants.CUSTOM_ASSET_TYPE_KEY, PluginConstants.CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS)
        PropertyManager.assetManager.markAsModifiedAsset(customAsset)

        return customAsset
    }
}
