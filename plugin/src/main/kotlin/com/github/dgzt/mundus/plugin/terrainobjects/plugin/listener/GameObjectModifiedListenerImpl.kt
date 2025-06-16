package com.github.dgzt.mundus.plugin.terrainobjects.plugin.listener

import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.mbrlabs.mundus.editorcommons.events.GameObjectModifiedEvent

class GameObjectModifiedListenerImpl : GameObjectModifiedEvent.GameObjectModifiedListener {
    override fun onGameObjectModified(event: GameObjectModifiedEvent) {
        val go = event.gameObject
        val terrainObjectsComponent = go.findComponentByType<TerrainObjectsComponent?>(PluginConstants.TYPE)

        terrainObjectsComponent?.updateTerrainObjects()
    }
}
