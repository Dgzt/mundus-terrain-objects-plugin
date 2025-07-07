package com.github.dgzt.mundus.plugin.terrainobjects.plugin.model

import com.badlogic.gdx.math.Vector3

data class DeleteModel(
    var radius: Float,
    var brushLocalPos: Vector3 = Vector3(),
)
