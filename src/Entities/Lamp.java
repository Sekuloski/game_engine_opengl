package Entities;

import Models.RawModel;
import Models.TexturedModel;
import NormalMappingOBJConverter.NormalMappedObjLoader;
import RenderEngine.Loader;
import Terrains.Terrain;
import Textures.ModelTexture;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class Lamp
{

    public Lamp(List<Entity> entities, List<Light> lights, Loader loader, Terrain terrain, float x, float z)
    {
        TexturedModel streetLight = getModel(loader);
        streetLight.getTexture().setUseFakeLighting(true);

        entities.add(new Entity(streetLight, new Vector3f(x, terrain.getHeightOfTerrain(x, z), z), 0, 180, 0, 10f, 5));

    }

    public TexturedModel getModel(Loader loader)
    {
        RawModel streetLightData = NormalMappedObjLoader.loadOBJ("street_light", loader);
        TexturedModel lamp = new TexturedModel(streetLightData, new ModelTexture(loader.loadTexture("street_light_diffuse")), "lamp");
        lamp.getTexture().setNormalMap(loader.loadTexture("street_light_normal"));
        return lamp;
    }
}
