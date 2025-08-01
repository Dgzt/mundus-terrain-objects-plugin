package com.github.dgzt.mundus.plugin.terrainobjects.runtime.renderer;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.asset.TerrainObjectsLayerAsset;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.model.TerrainObject;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.utils.Pools;

import java.nio.FloatBuffer;

public class TerrainObjectsRendererInstancedImpl implements TerrainObjectsRenderer {

    private static final String INSTANCED_ALIAS = "i_worldTrans";

    private final Array<TerrainObjectModelInstance> modelInstances;

    public TerrainObjectsRendererInstancedImpl() {
        modelInstances = new Array<>(2);
    }

    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        for (int i = 0; i < modelInstances.size; ++i) {
            modelInstances.get(i).modelInstance.getRenderables(renderables, pool);
        }
    }

    @Override
    public void update(final boolean recreateAllObjects, final TerrainObjectsLayerAsset terrainObjectsLayerAsset, final TerrainObjectsAsset terrainObjectsAsset, final Matrix4 parentTransform) {
        final ObjectMap<ModelAsset, Array<TerrainObject>> map = createModelAssetMap(terrainObjectsAsset, terrainObjectsLayerAsset);
        removeModelInstances(recreateAllObjects, map);
        addModelInstances(map);
        updatePositions(map, parentTransform);
    }

    private ObjectMap<ModelAsset, Array<TerrainObject>> createModelAssetMap(final TerrainObjectsAsset terrainObjectsAsset, final TerrainObjectsLayerAsset terrainObjectLayerAsset) {
        final ObjectMap<ModelAsset, Array<TerrainObject>> map = new ObjectMap<>();

        for (int i = 0; i < terrainObjectsAsset.getTerrainObjects().size; ++i) {
            final TerrainObject terrainObject = terrainObjectsAsset.getTerrainObjects().get(i);
            final int modelPos = terrainObject.getModelPos();
            final ModelAsset modelAsset = terrainObjectLayerAsset.getModelAssets().get(modelPos);

            if (!map.containsKey(modelAsset)) {
                map.put(modelAsset, new Array<>());
            }

            map.get(modelAsset).add(terrainObject);
        }

        return map;
    }

    private void updatePositions(final ObjectMap<ModelAsset, Array<TerrainObject>> terrainObjectMap, final Matrix4 parentTransform) {
        final ObjectMap.Entries<ModelAsset, Array<TerrainObject>> entries = terrainObjectMap.entries();
        while (entries.hasNext) {
            final ObjectMap.Entry<ModelAsset, Array<TerrainObject>> next = entries.next();

            final TerrainObjectModelInstance terrainObjectModelInstance = findById(next.key.getID());
            final ModelInstance modelInstance = terrainObjectModelInstance.modelInstance;

            modelInstance.transform.set(parentTransform);
            enableInstancedMeshesIfNecessary(terrainObjectModelInstance, next.value.size);
            setupPositionScaleAndRotation(modelInstance, next.value);
        }
    }

    private void addModelInstances(final ObjectMap<ModelAsset, Array<TerrainObject>> terrainObjectMap) {
        ObjectMap.Keys<ModelAsset> keys = terrainObjectMap.keys();
        while (keys.hasNext) {
            final ModelAsset modelAsset = keys.next();

            if (!containsModelInstance(modelAsset.getID())) {
                modelInstances.add(new TerrainObjectModelInstance(modelAsset));
            }
        }
    }

    private void setupPositionScaleAndRotation(final ModelInstance modelInstance, final Array<TerrainObject> terrainObjects) {
        final FloatBuffer offsets = BufferUtils.newFloatBuffer(terrainObjects.size * 16); // 16 floats for mat4
        final Matrix4 tmpMatrix4 = Pools.matrix4Pool.obtain();
        final Quaternion rot = Pools.quaternionPool.obtain();

        for (int i = 0 ; i < terrainObjects.size; ++i) {
            final TerrainObject terrainObject = terrainObjects.get(i);

            final Vector3 localPosition = terrainObject.getPosition();
            final Vector3 rotate = terrainObject.getRotation();
            final Vector3 scale = terrainObject.getScale();

            tmpMatrix4.idt();

            // Translation
            tmpMatrix4.trn(localPosition);

            // Rotation
            if (!rotate.isZero()) {
                rot.idt();
                rot.setEulerAngles(rotate.y, rotate.x, rotate.z);
                tmpMatrix4.rotate(rot);
            }

            // Scale
            if (!scale.isUnit()) {
                tmpMatrix4.scale(scale.x, scale.y, scale.z);
            }

            offsets.put(tmpMatrix4.tra().getValues());
        }

        offsets.position(0);

        for(int i = 0 ; i < modelInstance.nodes.size; i++) {
            final Node node = modelInstance.nodes.get(i);
            for (int ii = 0; ii < node.parts.size; ++ii) {
                final NodePart nodePart = node.parts.get(ii);
                final Mesh mesh = nodePart.meshPart.mesh;

                mesh.setInstanceData(offsets);
            }
        }

        Pools.matrix4Pool.free(tmpMatrix4);
        Pools.quaternionPool.free(rot);
    }

    private void removeModelInstances(final boolean recreateAllObjects, final ObjectMap<ModelAsset, Array<TerrainObject>> terrainObjectMap) {
        for (int i = modelInstances.size - 1; i >= 0; --i) {
            final TerrainObjectModelInstance terrainObjectModelInstance = modelInstances.get(i);

            if (recreateAllObjects || !terrainObjectMap.containsKey(terrainObjectModelInstance.modelAsset)) {
                modelInstances.removeIndex(i);
                terrainObjectModelInstance.dispose();
            }
        }
    }

    private boolean containsModelInstance(final String id) {
        return findById(id) != null;
    }

    private TerrainObjectModelInstance findById(final String id) {
        for (int i = 0; i < modelInstances.size; ++i) {
            final TerrainObjectModelInstance modelInstance = modelInstances.get(i);

            if (id.equals(modelInstance.modelAsset.getID())) {
                return modelInstance;
            }
        }

        return null;
    }

    private void enableInstancedMeshesIfNecessary(final TerrainObjectModelInstance terrainObjectModelInstance, final int maxInstances) {
        final ModelInstance modelInstance = terrainObjectModelInstance.modelInstance;
        final int previousMaxInstances = terrainObjectModelInstance.instancedCount;

        for(int i = 0 ; i < modelInstance.nodes.size; i++) {
            final Node node = modelInstance.nodes.get(i);
            for (int ii = 0; ii < node.parts.size; ++ii) {
                final NodePart nodePart = node.parts.get(ii);
                final Mesh mesh = nodePart.meshPart.mesh;

                if (previousMaxInstances != TerrainObjectModelInstance.DISABLED_INSTANCES_COUNT && previousMaxInstances != maxInstances) {
                    mesh.disableInstancedRendering();
                }

                if (!mesh.isInstanced()) {
                    mesh.enableInstancedRendering(true, maxInstances,
                            new VertexAttribute(VertexAttributes.Usage.Generic, 4, INSTANCED_ALIAS, 0),
                            new VertexAttribute(VertexAttributes.Usage.Generic, 4, INSTANCED_ALIAS, 1),
                            new VertexAttribute(VertexAttributes.Usage.Generic, 4, INSTANCED_ALIAS, 2),
                            new VertexAttribute(VertexAttributes.Usage.Generic, 4, INSTANCED_ALIAS, 3));
                }
            }
        }

        terrainObjectModelInstance.instancedCount = maxInstances;
    }

    private class TerrainObjectModelInstance implements Disposable {
        private static final int DISABLED_INSTANCES_COUNT = -1;

        private final ModelAsset modelAsset;
        private final ModelInstance modelInstance;
        private int instancedCount = DISABLED_INSTANCES_COUNT;

        public TerrainObjectModelInstance(final ModelAsset modelAsset) {
            this.modelAsset = modelAsset;
            modelInstance = new ModelInstance(modelAsset.getModel());

            for(int i = 0 ; i < modelInstance.nodes.size; i++) {
                final Node node = modelInstance.nodes.get(i);
                for (int ii = 0; ii < node.parts.size; ++ii) {
                    final NodePart nodePart = node.parts.get(ii);

                    final VertexAttributes vertexAttributes = nodePart.meshPart.mesh.getVertexAttributes();
                    final int[] usages = new int[vertexAttributes.size()];
                    for (int iii = 0; iii < vertexAttributes.size(); ++iii) {
                        usages[iii] = vertexAttributes.get(iii).usage;
                    }

                    nodePart.meshPart.mesh = nodePart.meshPart.mesh.copy(true, false, usages);
                }
            }
        }

        @Override
        public void dispose() {
            for(int i = 0 ; i < modelInstance.nodes.size; i++) {
                final Node node = modelInstance.nodes.get(i);
                for (int ii = 0; ii < node.parts.size; ++ii) {
                    final Mesh mesh = node.parts.get(ii).meshPart.mesh;

                    mesh.dispose();
                }
            }
        }
    }
}
