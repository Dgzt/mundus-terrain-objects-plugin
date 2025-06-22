package com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils

import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent
import com.mbrlabs.mundus.commons.scene3d.components.TerrainManagerComponent

object GameObjectUtils {

    fun isTerrainManagerGameObject(gameObject: GameObject): Boolean {
        return gameObject.findComponentByType<TerrainManagerComponent>(Component.Type.TERRAIN_MANAGER) != null
    }

    fun isTerrainGameObject(gameObject: GameObject): Boolean {
        return gameObject.findComponentByType<ModelComponent>(Component.Type.TERRAIN) != null
    }

    fun hasTerrainObjectsComponent(gameObject: GameObject): Boolean {
        return gameObject.findComponentByType<TerrainObjectsComponent>(PluginConstants.TYPE) != null
    }
}
