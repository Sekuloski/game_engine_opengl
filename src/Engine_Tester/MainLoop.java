package Engine_Tester;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Entities.Player;
import GUI.GuiRenderer;
import GUI.GuiTexture;
import Models.RawModel;
import Models.TexturedModel;
import Render_Engine.*;
import Terrains.Terrain;
import Textures.ModelTexture;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainLoop
{
    private static final int MAX_LIGHTS = 8;
    // This is the main class that calls all methods needed to display the project to the screen.

    public static void main(String[] args)
    {
        DisplayManager.createDisplay();
        Loader loader = new Loader();

        ModelData treeData = OBJLoader.loadOBJ("tree");
        RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
        TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));

        ModelData fernData = OBJLoader.loadOBJ("fern");
        ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"));
        fernTexture.setNumberOfRows(2);
        RawModel fernModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
        TexturedModel fern = new TexturedModel(fernModel, fernTexture);

        ModelData playerData = OBJLoader.loadOBJ("player");
        RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
        TexturedModel person = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("player")));

        ModelData streetLightData = OBJLoader.loadOBJ("street_light");
        RawModel streetLightModel = loader.loadToVAO(streetLightData.getVertices(), streetLightData.getTextureCoords(), streetLightData.getNormals(), streetLightData.getIndices());
        TexturedModel streetLight = new TexturedModel(streetLightModel, new ModelTexture(loader.loadTexture("street_light")));


        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("soil"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("road"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        tree.getTexture().setHasTransparency(true);
        fern.getTexture().setHasTransparency(true);
        fern.getTexture().setUseFakeLighting(true);
        streetLight.getTexture().setUseFakeLighting(true);

        List<Entity> entities = new ArrayList<>();
        List<Terrain> terrains = new ArrayList<>();
        List<GuiTexture> guis = new ArrayList<>();
        List<Light> lights = new ArrayList<>();
        Random random = new Random();

        Light light = new Light(new Vector3f(-3000, 2000, -2000), new Vector3f(0.1f, 0.1f, 0.1f));
        lights.add(light);
        lights.add(new Light(new Vector3f(185, 10, 293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(370, 17, 300), new Vector3f(0, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(293, 7, 305), new Vector3f(0, 0, 2), new Vector3f(1, 0.01f, 0.002f)));

        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.75f, -0.75f), new Vector2f(0.15f, 0.25f));
        guis.add(gui);

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        int n = 4;

        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                Terrain terrain = new Terrain(i, j, loader, texturePack, blendMap, "heightmap");
                terrains.add(terrain);
            }
        }

        for(int i = 0; i < 500; i++)
        {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            entities.add(new Entity(tree, new Vector3f(x, terrains.get(getTerrainIndex(x, z, n)).getHeightOfTerrain(x, z), z), 0, 0, 0, 3));
            x = random.nextFloat() * 800;
            z = random.nextFloat() * 800;
            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, terrains.get(getTerrainIndex(x, z, n)).getHeightOfTerrain(x, z), z), 0, 0, 0, 0.6f));
        }

        lights.add(new Light(new Vector3f(100, terrains.get(getTerrainIndex(100, 100, n)).getHeightOfTerrain(100, 100) + 12.8f, 100), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
        entities.add(new Entity(streetLight, new Vector3f(100, terrains.get(getTerrainIndex(100, 100, n)).getHeightOfTerrain(100, 100), 100), 0, 0, 0, 1));


        MasterRenderer renderer = new MasterRenderer();

        Player player = new Player(person, new Vector3f(10, 0,10), 0, 235, 0, 1);

        Camera camera = new Camera(player);

        while (!Display.isCloseRequested())
        {
            // Test render
            camera.move();
            player.move(terrains.get(getTerrainIndex(player.getPosition().x, player.getPosition().z, n)));
            renderer.processEntity(player);
            for (Terrain terrain : terrains)
            {
                renderer.processTerrain(terrain);
            }

            for (Entity entity1 : entities)
            {
                renderer.processEntity(entity1);
            }
            renderer.render(lights, camera);
            guiRenderer.render(guis);
            DisplayManager.updateDisplay();
        }

        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }

    public static int getTerrainIndex(float x, float z, int n)
    {
        x = n * (int) Math.floor(x / 800f);
        z = (int) Math.floor(z / 800f);
        return Math.max((int) (x + z), 0);
    }

    public static List<Light> getNearestLights(List<Light> lights)
    {
        List<Light> nearestLights = new ArrayList<>();

//        for(int i = 0; i < MAX_LIGHTS; i++)
//        {
//
//        }

        return  nearestLights;
    }
}
