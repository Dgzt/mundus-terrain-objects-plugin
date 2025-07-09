package com.github.dgzt.mundus.plugin.terrainobjects.plugin.model

import com.mbrlabs.mundus.commons.assets.ModelAsset

class SelectedModel {
    var pos = -1 // Position in texture grid
    lateinit var modelAsset: ModelAsset
    lateinit var actionModel: BaseActionModel

    fun isActionModelInitialized() = ::actionModel.isInitialized
}
