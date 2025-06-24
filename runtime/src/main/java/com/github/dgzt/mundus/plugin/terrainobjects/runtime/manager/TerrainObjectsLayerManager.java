package com.github.dgzt.mundus.plugin.terrainobjects.runtime.manager;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset;

public class TerrainObjectsLayerManager {

    private static final ObjectMap<String, TerrainObjectsLayerAsset> assetMap = new ObjectMap<>(2);

    public static void register(final TerrainObjectsLayerAsset asset) {
        final String id = asset.getTerrainObjectsLasetCustomAsset().getID();

        assetMap.put(id, asset);
    }

    public static boolean containsKey(final String key) {
        return assetMap.containsKey(key);
    }

    public static TerrainObjectsLayerAsset getByKey(final String key) {
        return assetMap.get(key);
    }
}
