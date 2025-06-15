package com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject;
import com.mbrlabs.mundus.commons.assets.ModelAsset;

public class TerrainObjectsRendererModelCacheImpl implements TerrainObjectsRenderer {

    private final Array<ModelInstance> modelInstances;
    private final ModelCache modelCache;

    public TerrainObjectsRendererModelCacheImpl() {
        modelInstances = new Array<>();
        modelCache = new ModelCache();
    }

    @Override
    public void update(final Array<ModelAsset> modelAssets, final Array<TerrainObject> terrainObjects) {
        addModelInstances(modelAssets, terrainObjects);

        // TODD position, rotation and scale

        modelCache.begin();
        modelCache.add(modelInstances);
        modelCache.end();
    }

    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        modelCache.getRenderables(renderables, pool);
    }

    private void addModelInstances(final Array<ModelAsset> modelAssets, final Array<TerrainObject> terrainObjects) {
        for (int i = 0; i < terrainObjects.size; ++i) {
            final TerrainObject terrainObject = terrainObjects.get(i);
            final String terrainObjectId = terrainObject.getId();

            if (!containsModelInstance(terrainObjectId)) {
                final int modelPos = terrainObject.getModelPos();
                final ModelAsset modelAsset = modelAssets.get(modelPos);
                final Model model = modelAsset.getModel();

                final ModelInstance modelInstance = new ModelInstance(model);
                modelInstance.userData = terrainObjectId;
                modelInstances.add(modelInstance);
            }
        }
    }

    private boolean containsModelInstance(final String id) {
        for (int i = 0; i < modelInstances.size; ++i) {
            if (id.equals(modelInstances.get(i).userData)) {
                return true;
            }
        }

        return false;
    }
}
