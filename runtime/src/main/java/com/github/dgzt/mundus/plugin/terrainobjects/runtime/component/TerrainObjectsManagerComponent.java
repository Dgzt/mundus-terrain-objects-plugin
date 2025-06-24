package com.github.dgzt.mundus.plugin.terrainobjects.runtime.component;

import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class TerrainObjectsManagerComponent extends AbstractTerrainObjectsComponent {

    public TerrainObjectsManagerComponent(final GameObject go) {
        super(go);
    }

    @Override
    public Component clone(final GameObject go) {
        final TerrainObjectsManagerComponent cloned = new TerrainObjectsManagerComponent(go);
        cloned.setTerrainObjectsLayerAsset(getTerrainObjectsLayerAsset());

        return cloned;
    }
}
