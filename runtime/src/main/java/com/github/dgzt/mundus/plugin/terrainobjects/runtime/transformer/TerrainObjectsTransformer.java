package com.github.dgzt.mundus.plugin.terrainobjects.runtime.transformer;

import com.badlogic.gdx.utils.Array;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent;
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.dto.TerrainObjectsDTO;

public class TerrainObjectsTransformer {

    public static TerrainObjectsDTO convertToDTO(final TerrainObjectsComponent component) {
        final Array<String> modelAssetIds = new Array<>();

        for (int i = 0; i < component.countModels(); ++i) {
            modelAssetIds.add(component.getModel(i).getID());
        }

        final TerrainObjectsDTO dto = new TerrainObjectsDTO();
        dto.setModelAssetIds(modelAssetIds);
        return dto;
    }
}
