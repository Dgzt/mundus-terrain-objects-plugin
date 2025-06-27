package com.github.dgzt.mundus.plugin.terrainobjects.plugin.listener

import com.github.dgzt.mundus.plugin.terrainobjects.plugin.PropertyManager
import com.mbrlabs.mundus.editorcommons.events.ProjectChangedEvent

class ProjectChangedListenerImpl : ProjectChangedEvent.ProjectChangedListener {
    override fun onProjectChanged(event: ProjectChangedEvent) {
        PropertyManager.sceneGraph = event.sceneGraph
    }
}
