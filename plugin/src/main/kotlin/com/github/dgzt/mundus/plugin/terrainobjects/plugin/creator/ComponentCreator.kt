package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils.GameObjectUtils
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.mbrlabs.mundus.commons.scene3d.GameObject

object ComponentCreator {

    fun create(gameObject: GameObject): TerrainObjectsComponent {
        val component: TerrainObjectsComponent

        if (GameObjectUtils.isTerrainManagerGameObject(gameObject)) {
            component = TerrainObjectsComponent(gameObject)

            for (childGameObject in gameObject.children) {
                if (GameObjectUtils.isTerrainGameObject(childGameObject) && !GameObjectUtils.hasTerrainObjectsComponent(childGameObject)) {
                    childGameObject.addComponent(TerrainObjectsComponent(childGameObject))
                }
            }
        } else if (GameObjectUtils.isTerrainGameObject(gameObject)) {
            component = TerrainObjectsComponent(gameObject)
        } else {
            throw UnsupportedOperationException("Not supported game object type!")
        }

        return component
    }
}
