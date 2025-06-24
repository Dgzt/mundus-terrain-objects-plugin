package com.github.dgzt.mundus.plugin.terrainobjects.runtime.component;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer.TerrainObjectsRenderer;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer.TerrainObjectsRendererModelCacheImpl;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableComponent;

public class TerrainObjectsComponent extends AbstractComponent implements RenderableComponent {

    private int nextTerrainObjectId;
    private TerrainObjectsLayerAsset terrainObjectsLayerAsset;
    private TerrainObjectsAsset terrainObjectsAsset;
    private final TerrainObjectsRenderer renderer;

    public TerrainObjectsComponent(final GameObject go) {
        super(go);
        setType(PluginConstants.TYPE);
        renderer = new TerrainObjectsRendererModelCacheImpl();
        nextTerrainObjectId = 0;
    }

    @Override
    public void update(final float delta) {
        // NOOP
    }

    @Override
    public Component clone(final GameObject go) {
        return new TerrainObjectsComponent(go);
    }

    @Override
    public RenderableProvider getRenderableProvider() {
        return renderer;
    }

    public void addTerrainObject(final TerrainObject terrainObject) {
        terrainObject.setId(gameObject.id + "_" + nextTerrainObjectId++);
        terrainObjectsAsset.getTerrainObjects().add(terrainObject);

        updateTerrainObjects();
    }

    public void updateTerrainObjects() {
        renderer.update(terrainObjectsLayerAsset, terrainObjectsAsset, gameObject.getTransform());
    }

    public TerrainObjectsLayerAsset getTerrainObjectsLayerAsset() {
        return terrainObjectsLayerAsset;
    }

    public void setTerrainObjectsLayerAsset(final TerrainObjectsLayerAsset terrainObjectsLayerAsset) {
        this.terrainObjectsLayerAsset = terrainObjectsLayerAsset;
    }

    public TerrainObjectsAsset getTerrainObjectsAsset() {
        return terrainObjectsAsset;
    }

    public void setTerrainObjectsAsset(final TerrainObjectsAsset terrainObjectsAsset) {
        this.terrainObjectsAsset = terrainObjectsAsset;
    }
}
