package com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject;
import com.mbrlabs.mundus.commons.assets.ModelAsset;

public interface TerrainObjectsRenderer extends RenderableProvider {

    void update(Array<ModelAsset> modelAssets, Array<TerrainObject> terrainObjects);
}
