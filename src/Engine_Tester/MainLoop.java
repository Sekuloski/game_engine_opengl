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
import ToolBox.MousePicker;
import Water.WaterFrameBuffers;
import Water.WaterRenderer;
import Water.WaterShader;
import Water.WaterTile;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class MainLoop
{
    private static final int MAX_LIGHTS = 8;
    public static final int SEA_LEVEL = -5;
    public static float DX;
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

        ModelData playerData = OBJLoader.loadOBJ("player2");
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

        //int n = 4;

        List<Entity> entities = new ArrayList<>();
        Terrain[][] terrains = new Terrain[1][1];
        List<GuiTexture> guis = new ArrayList<>();
        List<Light> lights = new ArrayList<>();
        Random random = new Random();

        Light sun = new Light(new Vector3f(-3000, 2000, -2000), new Vector3f(1f, 1f, 1f));
        lights.add(sun);
        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.75f, -0.75f), new Vector2f(0.15f, 0.25f));
        guis.add(gui);

        GuiRenderer guiRenderer = new GuiRenderer(loader);


//        for(int i = 0; i < n; i++)
//        {
//            for(int j = 0; j < n; j++)
//            {
//                Terrain terrain = new Terrain(i, j, loader, texturePack, blendMap, "heightmap");
//                terrains[i][j] = terrain;
//            }
//        }

        terrains[0][0] = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");

        entities.add(new Entity(streetLight, new Vector3f(100, terrains[(int) Math.floor(100 / Terrain.getSize())][(int) Math.floor(100 / Terrain.getSize())].
                getHeightOfTerrain(100, 100), 100), 0, 0, 0, 1));
        lights.add(new Light(new Vector3f(100, terrains[(int) Math.floor(100 / Terrain.getSize())][(int) Math.floor(100 / Terrain.getSize())]
                .getHeightOfTerrain(100, 100) + 12.8f, 100), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));

//        for(int i = 0; i < 500; i++)
//        {
//            float x = random.nextFloat() * 800;
//            float z = random.nextFloat() * 800;
//            entities.add(new Entity(tree, new Vector3f(x, terrains[(int) Math.floor(x / Terrain.getSize())][(int) Math.floor(z / Terrain.getSize())]
//                    .getHeightOfTerrain(x, z), z), 0, 0, 0, 3));
//            x = random.nextFloat() * 800;
//            z = random.nextFloat() * 800;
//            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, terrains[(int) Math.floor(x / Terrain.getSize())][(int) Math.floor(z / Terrain.getSize())]
//                    .getHeightOfTerrain(x, z), z), 0, 0, 0, 0.6f));
//        }



        MasterRenderer renderer = new MasterRenderer(loader);

        Player player = new Player(person, new Vector3f(10, terrains[0][0].getHeightOfTerrain(10, 10),10), 0, 45, 0, 1);
        entities.add(player);

        Camera camera = new Camera(player);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);

        Mouse.setGrabbed(true);
        boolean current = true;
        boolean pressed = false;

        WaterFrameBuffers fbos = new WaterFrameBuffers();
        WaterShader shader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, shader, renderer.getProjectionMatrix(), fbos);
        List<WaterTile> waterTiles = new ArrayList<>();
        waterTiles.add(new WaterTile(512, 512, SEA_LEVEL));


        while (!Display.isCloseRequested())
        {
            DX = Mouse.getDX() * 0.3f;
            camera.move();
            player.move(getTerrain(player.getPosition().x, player.getPosition().z, terrains));

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - SEA_LEVEL);
            camera.getPosition().y -= distance;
            camera.inverPitch();
            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, -SEA_LEVEL + 1f));
            camera.getPosition().y += distance;
            camera.inverPitch();

            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, SEA_LEVEL + 1f));

            fbos.unbindCurrentFrameBuffer();

            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 0, 0, 0));
            waterRenderer.render(waterTiles, camera, sun);
            guiRenderer.render(guis);

            DisplayManager.updateDisplay();
            if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
            {
                if(!pressed)
                {
                    grabMouse(!current);
                    Player.escPressed = current;
                    current = !current;
                    pressed = true;
                }
            }
            else
            {
                pressed = false;
            }
        }

        fbos.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }

    public static void grabMouse(boolean next)
    {
        Mouse.setGrabbed(next);
    }

    public static Terrain getTerrain(float x, float z, Terrain[][] terrains)
    {
        return terrains[Math.max(0, (int) Math.floor(x / Terrain.getSize()))][Math.max(0, (int) Math.floor(z / Terrain.getSize()))];
    }

}
