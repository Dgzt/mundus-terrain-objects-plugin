package com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset;

public interface TerrainObjectsRenderer extends RenderableProvider {

    void update(TerrainObjectsLayerAsset terrainObjectsLayerAsset, TerrainObjectsAsset terrainObjectsAsset, Matrix4 parentTransform);
}
