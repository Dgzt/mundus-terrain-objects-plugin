package com.github.dgzt.mundus.plugin.terrainobjects.runtime.converter;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class TerrainObjectsComponentConverter implements CustomComponentConverter {

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
    public Component convert(final GameObject gameObject, final OrderedMap<String, String> orderedMap, final ObjectMap<String, Asset> objectMap) {
        final TerrainObjectsComponent component = new TerrainObjectsComponent(gameObject);
        return component;
    }
}
