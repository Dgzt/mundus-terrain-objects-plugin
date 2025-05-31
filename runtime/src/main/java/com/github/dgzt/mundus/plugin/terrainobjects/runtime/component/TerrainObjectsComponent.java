package com.github.dgzt.mundus.plugin.terrainobjects.runtime.component;

import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class TerrainObjectsComponent extends AbstractComponent {

    public TerrainObjectsComponent(final GameObject go) {
        super(go);
    }

    @Override
    public void update(final float delta) {
        // NOOP
    }

    @Override
    public Component clone(final GameObject go) {
        final TerrainObjectsComponent cloned = new TerrainObjectsComponent(go);
        // TODO type
        return cloned;
    }
}
