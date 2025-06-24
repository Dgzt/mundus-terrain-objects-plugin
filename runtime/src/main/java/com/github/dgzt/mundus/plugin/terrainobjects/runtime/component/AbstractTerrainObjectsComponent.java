package com.github.dgzt.mundus.plugin.terrainobjects.runtime.component;

import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;

public abstract class AbstractTerrainObjectsComponent extends AbstractComponent {

    private TerrainObjectsLayerAsset terrainObjectsLayerAsset;

    public AbstractTerrainObjectsComponent(final GameObject go) {
        super(go);
        setType(PluginConstants.TYPE);
    }

    @Override
    public void update(final float delta) {
        // NOOP
    }

    public TerrainObjectsLayerAsset getTerrainObjectsLayerAsset() {
        return terrainObjectsLayerAsset;
    }

    public void setTerrainObjectsLayerAsset(final TerrainObjectsLayerAsset terrainObjectsLayerAsset) {
        this.terrainObjectsLayerAsset = terrainObjectsLayerAsset;
    }
}
