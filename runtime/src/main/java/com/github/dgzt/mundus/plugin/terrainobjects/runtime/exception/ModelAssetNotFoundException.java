package com.github.dgzt.mundus.plugin.terrainobjects.runtime.exception;

public class ModelAssetNotFoundException extends RuntimeException {

    public ModelAssetNotFoundException(final String assetId) {
        super("Model asset not found: " + assetId);
    }
}
