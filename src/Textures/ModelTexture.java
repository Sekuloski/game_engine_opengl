package Textures;

public class ModelTexture
{

    private final int textureID;
    private int normalMap;
    private int specularMap;
    private float shineDamper = 1;

    private float reflectivity = 0;
    private boolean hasTransparency = false;
    private boolean useFakeLighting = false;

    private boolean hasSpecularMap = false;

    public ModelTexture(int id)
    {
        this.textureID = id;
    }

    public int getNormalMap()
    {
        return normalMap;
    }

    public void setNormalMap(int normalMap)
    {
        this.normalMap = normalMap;
    }
    public void setSpecularMap(int specMap)
    {
        this.specularMap = specMap;
        this.hasSpecularMap = true;
    }

    public boolean hasSpecularMap()
    {
        return hasSpecularMap;
    }

    public int getSpecularMap()
    {
        return specularMap;
    }

    private int numberOfRows = 1;

    public int getNumberOfRows()
    {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows)
    {
        this.numberOfRows = numberOfRows;
    }

    public boolean isUseFakeLighting()
    {
        return useFakeLighting;
    }

    public void setUseFakeLighting(boolean useFakeLighting)
    {
        this.useFakeLighting = useFakeLighting;
    }

    public boolean getHasTransparency()
    {
        return hasTransparency;
    }

    public void setHasTransparency(boolean hasTransparency)
    {
        this.hasTransparency = hasTransparency;
    }

    public float getShineDamper()
    {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper)
    {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity()
    {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity)
    {
        this.reflectivity = reflectivity;
    }

    public int getTextureID()
    {
        return textureID;
    }

}
