package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils.AssetUtils
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.utils.GameObjectUtils
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsAsset
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.AbstractTerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsManagerComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.manager.TerrainObjectsLayerManager
import com.mbrlabs.mundus.commons.scene3d.GameObject

object ComponentCreator {

    fun create(gameObject: GameObject): AbstractTerrainObjectsComponent {
        val component: AbstractTerrainObjectsComponent

        val terrainObjectsLayerAsset = TerrainObjectsLayerAsset(AssetUtils.createTerrainObjectsLayerAsset(gameObject.name))
        TerrainObjectsLayerManager.register(terrainObjectsLayerAsset)

        if (GameObjectUtils.isTerrainManagerGameObject(gameObject)) {
            component = TerrainObjectsManagerComponent(gameObject)
            component.terrainObjectsLayerAsset = terrainObjectsLayerAsset

            for (childGameObject in gameObject.children) {
                if (GameObjectUtils.isTerrainGameObject(childGameObject) && !GameObjectUtils.hasTerrainObjectsComponent(childGameObject)) {
                    val childGameObjectComponent = TerrainObjectsComponent(childGameObject)
                    childGameObjectComponent.terrainObjectsLayerAsset = terrainObjectsLayerAsset
                    childGameObjectComponent.terrainObjectsAsset = TerrainObjectsAsset(AssetUtils.createTerrainObjectsAsset(childGameObject.name))

                    childGameObject.addComponent(childGameObjectComponent)
                }
            }
        } else if (GameObjectUtils.isTerrainGameObject(gameObject)) {
            component = TerrainObjectsComponent(gameObject)
            component.terrainObjectsLayerAsset = terrainObjectsLayerAsset
            component.terrainObjectsAsset = TerrainObjectsAsset(AssetUtils.createTerrainObjectsAsset(gameObject.name))
        } else {
            throw UnsupportedOperationException("Not supported game object type!")
        }

        return component
    }
}
