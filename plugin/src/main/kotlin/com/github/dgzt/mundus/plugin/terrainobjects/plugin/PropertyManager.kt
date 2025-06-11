package com.github.dgzt.mundus.plugin.terrainobjects.plugin

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.mbrlabs.mundus.pluginapi.manager.AssetManager
import com.mbrlabs.mundus.pluginapi.manager.ToolManager

object PropertyManager {
    val json = Json(JsonWriter.OutputType.json)
    lateinit var assetManager: AssetManager
    lateinit var toolManager: ToolManager
}
