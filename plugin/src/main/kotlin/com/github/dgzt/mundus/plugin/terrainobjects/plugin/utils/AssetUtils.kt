package com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.PropertyManager
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject
import com.mbrlabs.mundus.commons.assets.CustomAsset

object AssetUtils {

    fun createTerrainObjectsLayerAsset(name: String): CustomAsset {
        val tmpDir = System.getProperty("java.io.tmpdir")
        val tmpFile = FileHandle("$tmpDir/$name.${PluginConstants.TERRAIN_OBJECTS_LAYER_EXTENSION}")

        tmpFile.writeString(PropertyManager.json.toJson(Array<String>()), false)

        // TODO exception
        val customAsset = PropertyManager.assetManager.createNewAsset(tmpFile)
        tmpFile.delete()

        customAsset.properties.put(PluginConstants.CUSTOM_ASSET_TYPE_KEY, PluginConstants.CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS_LAYER)
        PropertyManager.assetManager.markAsModifiedAsset(customAsset)

        return customAsset
    }

    fun createTerrainObjectsAsset(name: String): CustomAsset {
        val tmpDir = System.getProperty("java.io.tmpdir")
        val tmpFile = FileHandle("$tmpDir/$name.${PluginConstants.TERRAIN_OBJECTS_EXTENSION}")
        tmpFile.writeString(PropertyManager.json.toJson(Array<TerrainObject>()), false)

        // TODO exception
        val customAsset = PropertyManager.assetManager.createNewAsset(tmpFile)
        tmpFile.delete()

        customAsset.properties.put(PluginConstants.CUSTOM_ASSET_TYPE_KEY, PluginConstants.CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS)
        customAsset.properties.put(PluginConstants.CUSTOM_ASSET_NEXT_TERRAIN_OBJECT_ID_KEY, 0.toString())
        PropertyManager.assetManager.markAsModifiedAsset(customAsset)

        return customAsset
    }
}
