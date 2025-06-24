package com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset;

import com.badlogic.gdx.utils.Array;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject;
import com.mbrlabs.mundus.commons.assets.CustomAsset;

public class TerrainObjectsAsset {

    private final CustomAsset terrainObjectsCustomAsset;

    private final Array<TerrainObject> terrainObjects;

    public TerrainObjectsAsset(final CustomAsset terrainObjectsCustomAsset) {
        this.terrainObjectsCustomAsset = terrainObjectsCustomAsset;
        terrainObjects = new Array<>(8);
    }

    public CustomAsset getTerrainObjectsCustomAsset() {
        return terrainObjectsCustomAsset;
    }

    public Array<TerrainObject> getTerrainObjects() {
        return terrainObjects;
    }
}
