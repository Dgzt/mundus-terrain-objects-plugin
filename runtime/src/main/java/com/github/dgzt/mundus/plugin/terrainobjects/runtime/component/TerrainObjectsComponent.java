package com.github.dgzt.mundus.plugin.terrainobjects.runtime.component;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer.TerrainObjectsRenderer;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer.TerrainObjectsRendererModelCacheImpl;
import com.mbrlabs.mundus.commons.assets.CustomAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableComponent;

public class TerrainObjectsComponent extends AbstractComponent implements RenderableComponent {

    private int nextTerrainObjectId;
    private CustomAsset customAsset;
    private final Array<ModelAsset> modelAssets;
    private final Array<TerrainObject> terrainObjects;
    private final TerrainObjectsRenderer renderer;

    public TerrainObjectsComponent(final GameObject go) {
        super(go);
        setType(PluginConstants.TYPE);
        modelAssets = new Array<>(1);
        terrainObjects = new Array<>(1);
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

    public void addModel(final ModelAsset modelAsset) {
        modelAssets.add(modelAsset);
    }

    public int countModels() {
        return modelAssets.size;
    }

    public ModelAsset getModel(int pos) {
        return modelAssets.get(pos);
    }

    public void addTerrainObject(final TerrainObject terrainObject) {
        terrainObject.setId(gameObject.id + "_" + nextTerrainObjectId++);
        terrainObjects.add(terrainObject);

        updateTerrainObjects();
    }

    public void updateTerrainObjects() {
        renderer.update(modelAssets, terrainObjects, gameObject.getTransform());
    }

    public CustomAsset getCustomAsset() {
        return customAsset;
    }

    public void setCustomAsset(final CustomAsset customAsset) {
        this.customAsset = customAsset;
    }
}
