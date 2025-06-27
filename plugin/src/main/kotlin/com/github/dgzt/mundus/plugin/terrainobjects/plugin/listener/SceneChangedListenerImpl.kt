package com.github.dgzt.mundus.plugin.terrainobjects.plugin.listener

import com.github.dgzt.mundus.plugin.terrainobjects.plugin.PropertyManager
import com.mbrlabs.mundus.editorcommons.events.SceneChangedEvent

class SceneChangedListenerImpl : SceneChangedEvent.SceneChangedListener {
    override fun onSceneChanged(event: SceneChangedEvent) {
        PropertyManager.sceneGraph = event.sceneGraph
    }
}