package com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant;

import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class PluginConstants {
    public static final String LOG_TAG = "TerrainObjectsPlugin";

    public static final Component.Type TYPE = Component.Type.PARTICLE_SYSTEM; // TODO change it

    public static final String CUSTOM_ASSET_TYPE_KEY = "type";
    public static final String CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS_LAYER = "terrain-objects-layer";
    public static final String CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS = "terrain-objects";
    public static final String CUSTOM_ASSET_NEXT_TERRAIN_OBJECT_ID_KEY = "next-terrain-object-id";

    public static final String TERRAIN_OBJECTS_LAYER_EXTENSION = "terrainobjectslayer";
    public static final String TERRAIN_OBJECTS_EXTENSION = "terrainobjects";
}
