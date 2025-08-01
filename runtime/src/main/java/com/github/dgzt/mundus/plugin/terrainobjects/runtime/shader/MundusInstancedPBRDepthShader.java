package com.github.dgzt.mundus.plugin.terrainobjects.runtime.shader;


import com.badlogic.gdx.graphics.g3d.Renderable;
import net.mgsx.gltf.scene3d.shaders.PBRDepthShader;

public class MundusInstancedPBRDepthShader extends PBRDepthShader {

    private final boolean instanced;

    public MundusInstancedPBRDepthShader(final Renderable renderable, final Config config, final String prefix) {
        super(renderable, config, prefix);
        instanced = renderable.meshPart.mesh.isInstanced();
    }

    @Override
    public boolean canRender(final Renderable renderable) {
        return renderable.meshPart.mesh.isInstanced() == instanced && super.canRender(renderable);
    }
}
