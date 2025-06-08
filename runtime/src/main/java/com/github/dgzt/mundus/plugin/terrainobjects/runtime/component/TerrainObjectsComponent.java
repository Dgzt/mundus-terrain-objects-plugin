package com.github.dgzt.mundus.plugin.terrainobjects.runtime.component;

import com.badlogic.gdx.utils.Array;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants;
import com.mbrlabs.mundus.commons.assets.CustomAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class TerrainObjectsComponent extends AbstractComponent {

    private CustomAsset customAsset;
    private final Array<ModelAsset> modelAssets;

    public TerrainObjectsComponent(final GameObject go) {
        super(go);
        setType(PluginConstants.TYPE);
        modelAssets = new Array<>(1);
    }

    @Override
    public void update(final float delta) {
        // NOOP
    }

    @Override
    public Component clone(final GameObject go) {
        return new TerrainObjectsComponent(go);
    }

    public CustomAsset getCustomAsset() {
        return customAsset;
    }

    public void setCustomAsset(final CustomAsset customAsset) {
        this.customAsset = customAsset;
    }

    public Array<ModelAsset> getModelAssets() {
        return modelAssets;
    }
}
