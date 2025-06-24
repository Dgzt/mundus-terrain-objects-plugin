package com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.utils.Pools;

public class TerrainObjectsRendererModelCacheImpl implements TerrainObjectsRenderer {

    private final Array<ModelInstance> modelInstances;
    private final ModelCache modelCache;

    public TerrainObjectsRendererModelCacheImpl() {
        modelInstances = new Array<>();
        modelCache = new ModelCache();
    }

    @Override
    public void update(final TerrainObjectsLayerAsset terrainObjectsLayerAsset, final TerrainObjectsAsset terrainObjectsAsset, final Matrix4 parentTransform) {
        addModelInstances(terrainObjectsLayerAsset, terrainObjectsAsset);
        updatePositions(terrainObjectsAsset, parentTransform);

        modelCache.begin();
        modelCache.add(modelInstances);
        modelCache.end();
    }

    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        modelCache.getRenderables(renderables, pool);
    }

    private void addModelInstances(final TerrainObjectsLayerAsset terrainObjectsLayerAsset, final TerrainObjectsAsset terrainObjectsAsset) {
        final Array<TerrainObject> terrainObjects = terrainObjectsAsset.getTerrainObjects();
        final Array<ModelAsset> modelAssets = terrainObjectsLayerAsset.getModelAssets();

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

    private void updatePositions(final TerrainObjectsAsset terrainObjectsAsset, final Matrix4 parentTransform) {
        final Array<TerrainObject> terrainObjects = terrainObjectsAsset.getTerrainObjects();

        for (int i = 0; i < terrainObjects.size; ++i) {
            final TerrainObject terrainObject = terrainObjects.get(i);
            final ModelInstance modelInstance = findById(terrainObject.getId());

            modelInstance.transform.idt();
            setupPositionScaleAndRotation(modelInstance, terrainObject, parentTransform);
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

    private void setupPositionScaleAndRotation(final ModelInstance modelInstance, final TerrainObject terrainObject, final Matrix4 parentTransform) {
        final Vector3 localPosition = terrainObject.getPosition();
        final Vector3 rotate = terrainObject.getRotation();
        final Vector3 scale = terrainObject.getScale();

        modelInstance.transform.translate(localPosition);

        if (!rotate.isZero()) {
            final Quaternion rot = modelInstance.transform.getRotation(Pools.quaternionPool.obtain());
            rot.setEulerAngles(rotate.y, rotate.x, rotate.z);
            modelInstance.transform.rotate(rot);

            Pools.quaternionPool.free(rot);
        }

        if (!scale.isUnit()) {
            modelInstance.transform.scale(scale.x, scale.y, scale.z);
        }

        // Applies parent's transforms
        modelInstance.transform.mulLeft(parentTransform);
    }

    private ModelInstance findById(final String id) {
        for (int i = 0; i < modelInstances.size; ++i) {
            final ModelInstance modelInstance = modelInstances.get(i);
            if (modelInstance.userData.equals(id)) {
                return modelInstance;
            }
        }

        return null;
    }
}
