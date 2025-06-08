package com.github.dgzt.mundus.plugin.terrainobjects.runtime.dto;

import com.badlogic.gdx.utils.Array;

public class TerrainObjectsDTO {

    private Array<String> modelAssetIds;

    public Array<String> getModelAssetIds() {
        return modelAssetIds;
    }

    public void setModelAssetIds(final Array<String> modelAssetIds) {
        this.modelAssetIds = modelAssetIds;
    }
}
