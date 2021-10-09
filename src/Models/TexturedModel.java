package Models;

import Textures.ModelTexture;

public class TexturedModel
{

    private final RawModel model;
    private final ModelTexture texture;
    private final String name;

    public TexturedModel(RawModel model, ModelTexture texture, String name)
    {
        this.model = model;
        this.texture = texture;
        this.name = name;
    }

    public RawModel getModel()
    {
        return model;
    }

    public ModelTexture getTexture()
    {
        return texture;
    }

    public String getName()
    {
        return name;
    }
}
