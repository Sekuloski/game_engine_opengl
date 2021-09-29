package Engine_Tester;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Entities.Player;
import Models.RawModel;
import Models.TexturedModel;
import Render_Engine.*;
import Terrains.Terrain;
import Textures.ModelTexture;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainLoop
{

    // This is the main class that calls all methods needed to display the project to the screen.

    public static void main(String[] args)
    {
        DisplayManager.createDisplay();
        Loader loader = new Loader();

        ModelData treeData = OBJLoader.loadOBJ("tree");
        RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
        TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));

        ModelData fernData = OBJLoader.loadOBJ("fern");
        RawModel fernModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
        TexturedModel fern = new TexturedModel(fernModel, new ModelTexture(loader.loadTexture("fern")));

        ModelData playerData = OBJLoader.loadOBJ("player");
        RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
        TexturedModel person = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("player")));

        Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("soil"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("road"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        tree.getTexture().setHasTransparency(true);
        fern.getTexture().setHasTransparency(true);
        fern.getTexture().setUseFakeLighting(true);

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        List<Terrain> terrains = new ArrayList<>();

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
            entities.add(new Entity(fern, new Vector3f(x, terrains.get(getTerrainIndex(x, z, n)).getHeightOfTerrain(x, z), z), 0, 0, 0, 0.6f));
        }


        MasterRenderer renderer = new MasterRenderer();

        Player player = new Player(person, new Vector3f(0, 0,0), 0, 0, 0, 1);

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
            renderer.render(light, camera);

            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }

    public static int getTerrainIndex(float x, float z, int n)
    {
        x = n * (int) Math.floor(x / 800f);
        z = (int) Math.floor(z / 800f);
        System.out.println((int) (x + z));
        return (int) (x + z);
    }
}
