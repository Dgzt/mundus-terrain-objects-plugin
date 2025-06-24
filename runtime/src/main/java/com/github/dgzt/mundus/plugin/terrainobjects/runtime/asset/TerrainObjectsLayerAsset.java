package com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset;

import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.assets.CustomAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;

public class TerrainObjectsLayerAsset {

    private final CustomAsset terrainObjectsLasetCustomAsset;

    private final Array<ModelAsset> modelAssets;

    public TerrainObjectsLayerAsset(final CustomAsset terrainObjectsLasetCustomAsset) {
        this.terrainObjectsLasetCustomAsset = terrainObjectsLasetCustomAsset;
        modelAssets = new Array<>(1);
    }

    public CustomAsset getTerrainObjectsLasetCustomAsset() {
        return terrainObjectsLasetCustomAsset;
    }

    public Array<ModelAsset> getModelAssets() {
        return modelAssets;
    }
}
