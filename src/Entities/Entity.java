package Entities;

import Models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;

public class Entity
{

    private TexturedModel model;
    private Vector3f position;
    private float rx, ry, rz;
    private float scale;

    private int textureIndex = 0;

    public Entity(TexturedModel model, Vector3f position, float rx, float ry, float rz, float scale)
    {
        this.model = model;
        this.position = position;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.scale = scale;
    }

    public Entity(TexturedModel model, int index, Vector3f position, float rx, float ry, float rz, float scale)
    {
        this.textureIndex = index;
        this.model = model;
        this.position = position;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.scale = scale;
    }

    public float getXOffset()
    {
        int column = textureIndex % model.getTexture().getNumberOfRows();
        return (float) column / (float) model.getTexture().getNumberOfRows();
    }

    public float getYOffset()
    {
        int row = textureIndex / model.getTexture().getNumberOfRows();
        return (float) row / (float) model.getTexture().getNumberOfRows();
    }

    public void changePosition(float dx, float dy, float dz)
    {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void changeRotation(float dx, float dy, float dz)
    {
        this.rx += dx;
        this.ry += dy;
        this.rz += dz;
    }

    public TexturedModel getModel()
    {
        return model;
    }

    public void setModel(TexturedModel model)
    {
        this.model = model;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public float getRx()
    {
        return rx;
    }

    public void setRx(float rx)
    {
        this.rx = rx;
    }

    public float getRy()
    {
        return ry;
    }

    public void setRy(float ry)
    {
        this.ry = ry;
    }

    public float getRz()
    {
        return rz;
    }

    public void setRz(float rz)
    {
        this.rz = rz;
    }

    public float getScale()
    {
        return scale;
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }
}
