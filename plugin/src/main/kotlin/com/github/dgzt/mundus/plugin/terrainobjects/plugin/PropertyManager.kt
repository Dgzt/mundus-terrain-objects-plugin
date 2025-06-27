package com.github.dgzt.mundus.plugin.terrainobjects.plugin

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.pluginapi.manager.AssetManager
import com.mbrlabs.mundus.pluginapi.manager.ToolManager
import com.mbrlabs.mundus.pluginapi.manager.ViewportManager

object PropertyManager {
    val json = Json(JsonWriter.OutputType.json)

    lateinit var assetManager: AssetManager
    lateinit var toolManager: ToolManager
    lateinit var viewportManager: ViewportManager

    lateinit var sceneGraph: SceneGraph

    var selectedModelInstance: ModelInstance? = null
}
