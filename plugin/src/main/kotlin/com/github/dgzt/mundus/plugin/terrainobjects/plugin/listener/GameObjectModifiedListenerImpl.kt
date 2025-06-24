package com.github.dgzt.mundus.plugin.terrainobjects.plugin.listener

import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.AbstractTerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.mbrlabs.mundus.editorcommons.events.GameObjectModifiedEvent

class GameObjectModifiedListenerImpl : GameObjectModifiedEvent.GameObjectModifiedListener {
    override fun onGameObjectModified(event: GameObjectModifiedEvent) {
        val go = event.gameObject
        val component = go.findComponentByType<AbstractTerrainObjectsComponent?>(PluginConstants.TYPE)

        if (component != null && component is TerrainObjectsComponent) {
            component.updateTerrainObjects()
        }
    }
}
