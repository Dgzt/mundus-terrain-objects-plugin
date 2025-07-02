package com.github.dgzt.mundus.plugin.terrainobjects.runtime.converter;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.AbstractTerrainObjectsComponent;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsManagerComponent;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.exception.ModelAssetNotFoundException;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.exception.TerrainObjectsCustomAssetNotFoundException;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.exception.TerrainObjectsLayerCustomAssetNotFoundException;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.manager.TerrainObjectsLayerManager;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.CustomAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class TerrainObjectsComponentConverter implements CustomComponentConverter {

    private final Json json;

    public TerrainObjectsComponentConverter() {
        json = new Json();
    }

    @Override
    public Component.Type getComponentType() {
        return PluginConstants.TYPE;
    }

    @Override
    public OrderedMap<String, String> convert(final Component component) {
        final OrderedMap<String, String> map = new OrderedMap<>();
        return map;
    }

    @Override
    public Array<String> getAssetIds(final Component component) {
        if (!(component instanceof AbstractTerrainObjectsComponent)) {
            return new Array<>();
        }
        final AbstractTerrainObjectsComponent abstractTerrainObjectsComponent = (AbstractTerrainObjectsComponent) component;

        final Array<String> assetIds = new Array<>();

        // Terrain objects layer and dependencies
        final TerrainObjectsLayerAsset objectsLayerAsset = abstractTerrainObjectsComponent.getTerrainObjectsLayerAsset();
        assetIds.add(objectsLayerAsset.getTerrainObjectsLasetCustomAsset().getID());

        for (int i = 0; i < objectsLayerAsset.getModelAssets().size; ++i) {
            assetIds.add(objectsLayerAsset.getModelAssets().get(i).getID());
        }

        if (abstractTerrainObjectsComponent instanceof TerrainObjectsComponent) {
            final TerrainObjectsComponent terrainObjectsComponent = (TerrainObjectsComponent) abstractTerrainObjectsComponent;
            assetIds.add(terrainObjectsComponent.getTerrainObjectsAsset().getTerrainObjectsCustomAsset().getID());
        }

        return assetIds;
    }

    @Override
    public Component convert(final GameObject gameObject, final OrderedMap<String, String> orderedMap, final ObjectMap<String, Asset> objectMap) {
        final boolean hasTerrainObjectsAsset = hasTerrainObjectsAsset(objectMap);

        final AbstractTerrainObjectsComponent component = (hasTerrainObjectsAsset) ?
                new TerrainObjectsComponent(gameObject) :
                new TerrainObjectsManagerComponent(gameObject);

        component.setTerrainObjectsLayerAsset(getTerrainObjectsLayerAsset(objectMap));

        if (hasTerrainObjectsAsset) {
            final TerrainObjectsComponent terrainObjectsComponent = (TerrainObjectsComponent) component;

            terrainObjectsComponent.setTerrainObjectsAsset(getTerrainObjectsAsset(objectMap));
            terrainObjectsComponent.updateTerrainObjects(true);
        }

        return component;
    }

    private boolean hasTerrainObjectsAsset(final ObjectMap<String, Asset> objectMap) {
        for (final String key : objectMap.keys()) {
            final Asset asset = objectMap.get(key);

            if (asset instanceof CustomAsset) {
                final CustomAsset customAsset = (CustomAsset) asset;
                final ObjectMap<String, String> properties = customAsset.getProperties();

                if (properties.containsKey(PluginConstants.CUSTOM_ASSET_TYPE_KEY)
                        && PluginConstants.CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS.equals(properties.get(PluginConstants.CUSTOM_ASSET_TYPE_KEY))) {
                    return true;
                }
            }
        }

        return false;
    }

    private TerrainObjectsLayerAsset getTerrainObjectsLayerAsset(final ObjectMap<String, Asset> objectMap) {
        for (final String key : objectMap.keys()) {
            final Asset asset = objectMap.get(key);

            if (asset instanceof CustomAsset) {
                final CustomAsset customAsset = (CustomAsset) asset;
                final String customAssetType = customAsset.getProperties().get(PluginConstants.CUSTOM_ASSET_TYPE_KEY);

                if (customAssetType != null && customAssetType.equals(PluginConstants.CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS_LAYER)) {
                    if (TerrainObjectsLayerManager.containsKey(asset.getID())) {
                        return TerrainObjectsLayerManager.getByKey(asset.getID());
                    } else {
                        final TerrainObjectsLayerAsset terrainObjectsLayerAsset = new TerrainObjectsLayerAsset(customAsset);

                        final Array<String> modelIds = json.fromJson(Array.class, String.class, customAsset.getFile().readString());
                        for (int i = 0; i < modelIds.size; ++i) {
                            final String modelId = modelIds.get(i);

                            if (objectMap.containsKey(modelId)) {
                                final ModelAsset modelAsset = (ModelAsset) objectMap.get(modelId);
                                terrainObjectsLayerAsset.getModelAssets().add(modelAsset);
                            } else {
                                throw new ModelAssetNotFoundException(modelId);
                            }
                        }

                        TerrainObjectsLayerManager.register(terrainObjectsLayerAsset);
                        return terrainObjectsLayerAsset;
                    }
                }
            }
        }

        throw new TerrainObjectsLayerCustomAssetNotFoundException();
    }

    private TerrainObjectsAsset getTerrainObjectsAsset(final ObjectMap<String, Asset> objectMap) {
        for (final String key : objectMap.keys()) {
            final Asset asset = objectMap.get(key);

            if (asset instanceof CustomAsset) {
                final CustomAsset customAsset = (CustomAsset) asset;
                final String customAssetType = customAsset.getProperties().get(PluginConstants.CUSTOM_ASSET_TYPE_KEY);

                if (customAssetType != null && customAssetType.equals(PluginConstants.CUSTOM_ASSET_TYPE_TERRAIN_OBJECTS)) {
                    final TerrainObjectsAsset terrainObjectsAsset = new TerrainObjectsAsset(customAsset);

                    final Array<TerrainObject> terrainObjects = json.fromJson(Array.class, TerrainObject.class, customAsset.getFile().readString());
                    terrainObjectsAsset.getTerrainObjects().addAll(terrainObjects);

                    return terrainObjectsAsset;
                }
            }
        }

        throw new TerrainObjectsCustomAssetNotFoundException();
    }
}
