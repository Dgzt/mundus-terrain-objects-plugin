package com.github.dgzt.mundus.plugin.terrainobjects.plugin.model

import com.mbrlabs.mundus.commons.assets.ModelAsset

class SelectedModel {
    var pos = -1
    lateinit var modelAsset: ModelAsset

    var posX = 0f
    var posY = 0f
    var posZ = 0f

    var rotationX = 0f
    var rotationY = 0f
    var rotationZ = 0f

    var scaleX = 1f
    var scaleY = 1f
    var scaleZ = 1f
}
