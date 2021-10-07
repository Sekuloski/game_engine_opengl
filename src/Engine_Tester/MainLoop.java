package Engine_Tester;

import Entities.*;
import GUI.GuiRenderer;
import GUI.GuiTexture;
import Models.RawModel;
import Models.TexturedModel;
import NormalMappingOBJConverter.NormalMappedObjLoader;
import Particles.ParticleMaster;
import Particles.ParticleSystem;
import Particles.ParticleTexture;
import RenderEngine.*;
import Sun.Sun;
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
    public static final int SEA_LEVEL = -70;
    public static float DX;
    private static boolean current = true;
    private static boolean pressed = false;
    private static float time = 0;
    private static final int sunX = 300000;
    private static final int sunY = 200000;
    private static final int sunZ = 200000;

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

        RawModel barrelData = NormalMappedObjLoader.loadOBJ("barrel", loader);
        ModelTexture barrelTexture = new ModelTexture(loader.loadTexture("barrel"));
        TexturedModel barrelModel = new TexturedModel(barrelData, barrelTexture);
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));

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
        List<Entity> normalEntities = new ArrayList<>();
        Terrain[][] terrains = new Terrain[1][1];
        List<GuiTexture> guis = new ArrayList<>();
        List<Light> lights = new ArrayList<>();
        Random random = new Random();

//        int n = 4;
//        for(int i = 0; i < n; i++)
//        {
//            for(int j = 0; j < n; j++)
//            {
//                Terrain terrain = new Terrain(i, j, loader, texturePack, blendMap, "heightmap");
//                terrains[i][j] = terrain;
//            }
//        }
        terrains[0][0] = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");

        for(int i = 0; i < 2048; i++)
        {
            float x = random.nextFloat() * 2048;
            float z = random.nextFloat() * 2048;
            entities.add(new Entity(tree, new Vector3f(x, getTerrain(x, z, terrains)
                    .getHeightOfTerrain(x, z), z), 0, 0, 0, 10, 50));
            x = random.nextFloat() * 2048;
            z = random.nextFloat() * 2048;
            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, getTerrain(x, z, terrains)
                    .getHeightOfTerrain(x, z), z), 0, 0, 0, 1));
        }

//        normalEntities.add(new Entity(barrelModel, new Vector3f(100, terrains[(int) Math.floor(100 / Terrain.getSize())][(int) Math.floor(100 / Terrain.getSize())].
//                getHeightOfTerrain(100, 100) + 20, 100), 0, 0, 0, 1f));

//        RawModel houseData = NormalMappedObjLoader.loadOBJ("Medieval_House", loader);
//        ModelTexture houseTexture = new ModelTexture(loader.loadTexture("Medieval_House_Diff"));
//        TexturedModel houseModel = new TexturedModel(barrelData, barrelTexture);
//        barrelModel.getTexture().setShineDamper(10);
//        barrelModel.getTexture().setReflectivity(0.5f);
//        barrelModel.getTexture().setNormalMap(loader.loadTexture("Medieval_House_Nor"));
//        barrelModel.getTexture().setSpecularMap(loader.loadTexture("Medieval_House_Spec_Red"));

//        normalEntities.add(new Entity(houseModel, new Vector3f(1300, getTerrain(1300, 500,
//                terrains).getHeightOfTerrain(1300, 500), 500), 0, 0, 0, 1f, 100));

        Light sunLight = new Light(new Vector3f(sunX, sunY, sunZ), new Vector3f(1f, 1f, 1f));
        lights.add(sunLight);

        Player player = new Player(person, new Vector3f(10, terrains[0][0].getHeightOfTerrain(10, 10),10), 0, 45, 0, 1);
        Camera camera = new Camera(player);
        MasterRenderer renderer = new MasterRenderer(loader, sunLight, camera);
        new Lamp(entities, lights, loader, terrains, 200, 200);
      //  Sun sun = new Sun(loader, new Vector3f(sunX/1000f, sunY/1000f, sunZ/1000f), 0, 20, renderer.getProjectionMatrix());

        new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.75f, -0.75f), new Vector2f(0.15f, 0.25f));
        guis.add(gui);
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        entities.add(player);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);
        Mouse.setGrabbed(true);

        WaterFrameBuffers fbos = new WaterFrameBuffers();
        WaterShader shader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, shader, renderer.getProjectionMatrix(), fbos);
        List<WaterTile> waterTiles = new ArrayList<>();
        waterTiles.add(new WaterTile(1024, 1024, SEA_LEVEL));

        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);
        ParticleSystem system = new ParticleSystem(particleTexture, 80, 10, 0.1f, 1, 1.6f);

        while (!Display.isCloseRequested())
        {
            DX = Mouse.getDX() * 0.3f;
            camera.move();
            player.move(getTerrain(player.getPosition().x, player.getPosition().z, terrains));
            //player.checkCollisions(entities);
           // moveSun(sunLight, sun, player);

            ParticleMaster.update(camera);

            renderer.renderShadowMap(entities, normalEntities, sunLight);

            system.generateParticles(new Vector3f(50, 20, 50));

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - SEA_LEVEL);
            camera.getPosition().y -= distance;
            camera.inverPitch();
            renderer.renderScene(entities, normalEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -SEA_LEVEL + 1f),
                    new Vector4f(0, 0, 0, 0));
            camera.getPosition().y += distance;
            camera.inverPitch();

            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(entities, normalEntities, terrains, lights, camera, new Vector4f(0, -1, 0, SEA_LEVEL + 1f),
                    new Vector4f(0, 0, 0, 0));

            fbos.unbindCurrentFrameBuffer();

            GL11.glEnable(GL30.GL_CLIP_DISTANCE1);

            renderer.renderScene(entities, normalEntities, terrains, lights, camera, new Vector4f(0, 0, 0, 0)
                                                                                   , new Vector4f(0, 0, -1, player.getPosition().z + 500));
            waterRenderer.render(waterTiles, camera, lights);

            ParticleMaster.renderParticles(camera);

            //sun.render(camera);
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
        //guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        ParticleMaster.cleanUp();
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

    private static void moveSun(Light sunLight, Sun sun, Player player)
    {
        time += DisplayManager.getDelta() * 1000;
        time %= 24000;

        sunLight.setPosition(new Vector3f(player.getPosition().x, sunY/1000f * (float) Math.sin(Math.toRadians(time/100)), sunZ/1000f * (float) Math.cos(Math.toRadians(time/100))));
        sun.setPosition(new Vector3f(player.getPosition().x, sunY/1000f * (float) Math.sin(Math.toRadians(time/100)), sunZ/1000f * (float) Math.cos(Math.toRadians(time/100))));

        float blendFactor;
        if(time >= 0 && time < 5000)
        {
            blendFactor = (time)/(5000);
        }
        else if(time >= 5000 && time < 9000)
        {
            blendFactor = (time - 5000)/(9000 - 5000);
        }
        else if (time >= 9000 && time < 21000){
            blendFactor = (time - 9000)/(21000 - 9000);
        }
        else
        {
            blendFactor = (time - 21000)/(24000 - 21000);
        }
    }

}
