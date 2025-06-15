package com.github.dgzt.mundus.plugin.terrainobjects.runtime.model;

import com.badlogic.gdx.math.Vector3;

public class TerrainObject {

    private String id;

    private int modelPos;

    private Vector3 position;

    private Vector3 rotation;

    private Vector3 scale;

    public TerrainObject() {

    }

    public TerrainObject(final int modelPos, final float posX, final float posY, final float posZ, final float rotX, final float rotY, final float rotZ, final float scaleX, final float scaleY, final float scaleZ) {
        this(modelPos, new Vector3(posX, posY, posZ), new Vector3(rotX, rotY, rotZ), new Vector3(scaleX, scaleY, scaleZ));
    }

    public TerrainObject(final int modelPos, final Vector3 position, final Vector3 rotation, final Vector3 scale) {
        this.modelPos = modelPos;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public int getModelPos() {
        return modelPos;
    }

    public void setModelPos(final int modelPos) {
        this.modelPos = modelPos;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(final Vector3 position) {
        this.position = position;
    }

    public Vector3 getRotation() {
        return rotation;
    }

    public void setRotation(final Vector3 rotation) {
        this.rotation = rotation;
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setScale(final Vector3 scale) {
        this.scale = scale;
    }
}
