package Entities;

import Models.RawModel;
import Models.TexturedModel;
import RenderEngine.Loader;
import RenderEngine.ModelData;
import RenderEngine.OBJLoader;
import Terrains.Terrain;
import Textures.ModelTexture;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class Lamp
{

    public Lamp(List<Entity> entities, List<Light> lights, Loader loader, Terrain[][] terrains, float x, float z)
    {
        TexturedModel streetLight = getModel(loader);
        streetLight.getTexture().setUseFakeLighting(true);

        entities.add(new Entity(streetLight, new Vector3f(x, terrains[(int) Math.floor(x / Terrain.getSize())][(int) Math.floor(z / Terrain.getSize())].
                getHeightOfTerrain(x, z), z), 0, 0, 0, 1, 5));
        lights.add(new Light(new Vector3f(x, terrains[(int) Math.floor(x / Terrain.getSize())][(int) Math.floor(z / Terrain.getSize())]
                .getHeightOfTerrain(x, z) + 12.8f, z), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
    }

    public TexturedModel getModel(Loader loader)
    {
        ModelData streetLightData = OBJLoader.loadOBJ("street_light");
        RawModel streetLightModel = loader.loadToVAO(streetLightData.getVertices(), streetLightData.getTextureCoords(), streetLightData.getNormals(), streetLightData.getIndices());

        return new TexturedModel(streetLightModel, new ModelTexture(loader.loadTexture("street_light")));
    }
}
