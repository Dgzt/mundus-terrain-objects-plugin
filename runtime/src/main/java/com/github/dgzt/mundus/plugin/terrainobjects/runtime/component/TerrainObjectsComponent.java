package com.github.dgzt.mundus.plugin.terrainobjects.runtime.component;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer.TerrainObjectsRenderer;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer.TerrainObjectsRendererModelCacheImpl;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableComponent;

public class TerrainObjectsComponent extends AbstractTerrainObjectsComponent implements RenderableComponent {

    private int nextTerrainObjectId;
    private TerrainObjectsAsset terrainObjectsAsset;
    private final TerrainObjectsRenderer renderer;

    public TerrainObjectsComponent(final GameObject go) {
        super(go);
        renderer = new TerrainObjectsRendererModelCacheImpl();
        nextTerrainObjectId = 0;
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

        terrainObjectsAsset.getTerrainObjectsCustomAsset().getProperties().put(PluginConstants.CUSTOM_ASSET_NEXT_TERRAIN_OBJECT_ID_KEY, String.valueOf(nextTerrainObjectId));

        updateTerrainObjects();
    }

    public void updateTerrainObjects() {
        updateTerrainObjects(false);
    }

    public void updateTerrainObjects(final boolean recreateAllObjects) {
        renderer.update(recreateAllObjects, getTerrainObjectsLayerAsset(), terrainObjectsAsset, gameObject.getTransform());
    }

    public int getNextTerrainObjectId() {
        return nextTerrainObjectId;
    }

    public void setNextTerrainObjectId(final int nextTerrainObjectId) {
        if (nextTerrainObjectId < this.nextTerrainObjectId) {
            throw new RuntimeException("The next terrain object ID (" + nextTerrainObjectId + ") can not be smaller then the current next object ID (" + this.nextTerrainObjectId + ")!");
        }
        this.nextTerrainObjectId = nextTerrainObjectId;
    }

    public TerrainObjectsAsset getTerrainObjectsAsset() {
        return terrainObjectsAsset;
    }

    public void setTerrainObjectsAsset(final TerrainObjectsAsset terrainObjectsAsset) {
        this.terrainObjectsAsset = terrainObjectsAsset;
    }
}
