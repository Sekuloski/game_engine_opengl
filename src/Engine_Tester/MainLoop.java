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

        ModelData grassData = OBJLoader.loadOBJ("grassModel");
        RawModel grassModel = loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(), grassData.getNormals(), grassData.getIndices());
        TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));

        ModelData dragonData = OBJLoader.loadOBJ("dragon");
        RawModel dragonModel = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getIndices());
        TexturedModel dragon = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("stallTexture")));

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
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();


        for(int i = 0; i < 500; i++)
        {
            entities.add(new Entity(tree, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 3));
            entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 1));
            entities.add(new Entity(fern, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 0.6f));
        }

        Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");
        Terrain terrain2 = new Terrain(1, 0, loader, texturePack, blendMap, "heightmap");


        MasterRenderer renderer = new MasterRenderer();

        Player player = new Player(dragon, new Vector3f(100, 0,-50), 0, 0, 0, 1);

        Camera camera = new Camera(player);

        while (!Display.isCloseRequested())
        {
            // Test render
            camera.move();
            player.move();
            renderer.processEntity(player);
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            for(Entity entity1 : entities)
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
}
