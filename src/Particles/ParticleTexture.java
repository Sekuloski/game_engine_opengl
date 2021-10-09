package Particles;

public class ParticleTexture
{

    private final int textureID;
    private final int numberOfRows;
    private final boolean additive;

    public ParticleTexture(int textureID, int numberOfRows, boolean additive)
    {
        this.textureID = textureID;
        this.numberOfRows = numberOfRows;
        this.additive = additive;
    }

    protected boolean usesAdditiveBlending()
    {
        return additive;
    }

    public int getTextureID()
    {
        return textureID;
    }

    public int getNumberOfRows()
    {
        return numberOfRows;
    }
}
