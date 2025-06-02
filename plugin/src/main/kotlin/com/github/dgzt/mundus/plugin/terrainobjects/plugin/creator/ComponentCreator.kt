package com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator

import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component

object ComponentCreator {

    fun create(gameObject: GameObject): Component {
        return TerrainObjectsComponent(gameObject)
    }
}
