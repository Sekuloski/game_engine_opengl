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
    private static final int sunX = -300000;
    private static final int sunY = 200000;
    private static final int sunZ = -200000;

    // This is the main class that calls all methods needed to display the project to the screen.

    public static void main(String[] args)
    {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        List<Entity> entities = new ArrayList<>();
        List<Entity> normalEntities = new ArrayList<>();
        Terrain[][] terrains = new Terrain[1][1];
        List<GuiTexture> guis = new ArrayList<>();
        List<Light> lights = new ArrayList<>();
        Random random = new Random();

        TexturedModel tree = getModel("tree", "tree", loader);
        TexturedModel person = getModel("player", "player", loader);

        ModelData fernData = OBJLoader.loadOBJ("fern");
        ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"));
        fernTexture.setNumberOfRows(2);
        RawModel fernModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
        TexturedModel fern = new TexturedModel(fernModel, fernTexture);

        TexturedModel well_roof = getNormalModel("well_roof", "well_roof_diffuse", "well_roof_normal", "well_roof_specular", loader);
        TexturedModel well_middle = getNormalModel("well_middle", "well_middle_diffuse", "well_middle_normal", "well_middle_specular", loader);
        TexturedModel well_lower = getNormalModel("well_lower", "well_lower_diffuse", "well_lower_normal", "well_lower_specular", loader);

        TexturedModel wall = getNormalModel("wall", "wall_disp", "wall_norm", "wall_spec", loader);
        addWalls(entities, wall);

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

        TexturedModel fire_pit = getModel("fire_pit", "fire_pit_diffuse", loader);


//        int n = 4;
//        for(int i = 0; i < n; i++)
//        {
//            for(int j = 0; j < n; j++)
//            {
//                Terrain terrain = new Terrain(i, j, loader, texturePack, blendMap, "heightmap");
//                terrains[i][j] = terrain;
//            }
//        }
        terrains[0][0] = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap2");

//        for(int i = 0; i < 2048; i++)
//        {
//            float x = random.nextFloat() * 2048;
//            float z = random.nextFloat() * 2048;
//            entities.add(new Entity(tree, new Vector3f(x, getTerrain(x, z, terrains)
//                    .getHeightOfTerrain(x, z), z), 0, 0, 0, 10, 50));
//            x = random.nextFloat() * 2048;
//            z = random.nextFloat() * 2048;
//            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, getTerrain(x, z, terrains)
//                    .getHeightOfTerrain(x, z), z), 0, 0, 0, 1));
//        }

        TexturedModel house = getNormalModel("Medieval_House", "Medieval_House_Diff", "Medieval_House_Nor", "Medieval_House_Spec_Red", loader);

        normalEntities.add(new Entity(house, new Vector3f(1281, getTerrainHeight(1281, 739, terrains), 739), 0, -304, 0, 0.2f, 100));
        normalEntities.add(new Entity(house, new Vector3f(1457, getTerrainHeight(1457, 608, terrains), 608), 0, 0, 0, 0.2f, 100));
        normalEntities.add(new Entity(house, new Vector3f(1287, getTerrainHeight(1287, 577, terrains), 577), 0, -258, 0, 0.2f, 100));
        normalEntities.add(new Entity(house, new Vector3f(1421, getTerrainHeight(1421, 357, terrains), 357), 0, -407, 0, 0.2f, 100));


        Light sunLight = new Light(new Vector3f(sunX, sunY, sunZ), new Vector3f(0, 0, 0));
        lights.add(sunLight);
        int x = 1262;
        int z = 455;
        Player player = new Player(person, new Vector3f(x, terrains[0][0].getHeightOfTerrain(x, z),z), 0, 45, 0, 1);
        x += 20;
        z -= 3;

        new Lamp(entities, lights, loader, terrains, 1426, 450);
        new Well(well_roof, well_middle, well_lower, entities, new Vector3f(1260, terrains[0][0].getHeightOfTerrain(1260, 510),510));
        entities.add(new Entity(fire_pit, new Vector3f(x, getTerrainHeight(x, z, terrains), z), 0, 0, 0, 5, 1));
        entities.add(new Entity(getModel("bridge", "bridge_diffuse", loader), new Vector3f(1377, -64, 1241), 0, 60, 0, 10, 1));
        lights.add(new Light(new Vector3f(x, getTerrainHeight(x, z, terrains) + 20, z), new Vector3f(1, 1, 1), new Vector3f(1, 0.001f, 0.00002f)));

        Camera camera = new Camera(player);
        MasterRenderer renderer = new MasterRenderer(loader, sunLight, camera);
        //  Sun sun = new Sun(loader, new Vector3f(sunX/1000f, sunY/1000f, sunZ/1000f), 0, 20, renderer.getProjectionMatrix());

        new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.75f, -0.75f), new Vector2f(0.15f, 0.25f));
        guis.add(gui);
        GuiRenderer guiRenderer = new GuiRenderer(loader);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);
        Mouse.setGrabbed(true);

        WaterFrameBuffers fbos = new WaterFrameBuffers();
        WaterShader shader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, shader, renderer.getProjectionMatrix(), fbos);
        List<WaterTile> waterTiles = new ArrayList<>();
        waterTiles.add(new WaterTile(1024, 1024, SEA_LEVEL));

        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("fire"), 8, true);
        ParticleSystem system = new ParticleSystem(particleTexture, 160, 3, -0.1f, 1, 5f);

        List<Entity> entitiesWithoutPlayer = List.copyOf(entities);
        entities.add(player);

        while (!Display.isCloseRequested())
        {
            DX = Mouse.getDX() * 0.3f;
            camera.move();
            player.move(getTerrain(player.getPosition().x, player.getPosition().z, terrains));
            //player.checkCollisions(entities);
            //moveSun(sunLight, sun, player);

            ParticleMaster.update(camera);

            renderer.renderShadowMap(entities, normalEntities, sunLight);

            system.generateParticles(new Vector3f(x, getTerrainHeight(x, z, terrains), z));

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

            renderer.renderScene(entitiesWithoutPlayer, normalEntities, terrains, lights, camera, new Vector4f(0, 0, 0, 0)
                                                                                   , new Vector4f(0, 0, 0, 0));
            waterRenderer.render(waterTiles, camera, lights);

            ParticleMaster.renderParticles(camera);

            //sun.render(camera);
            guiRenderer.render(guis);

            DisplayManager.updateDisplay();
            checkInputs();
        }

        fbos.cleanUp();
        //guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        ParticleMaster.cleanUp();
        DisplayManager.closeDisplay();

    }

    private static void checkInputs()
    {
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
        if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    private static void addWalls(List<Entity> normalEntities, TexturedModel wall)
    {
        for(int i = 0; i < 16; i++)
        {
            normalEntities.add(new Entity(wall, new Vector3f(-64, -20, 64 + 128 * i), 0, 0, 0, 64, 1));
            normalEntities.add(new Entity(wall, new Vector3f(64 + 128 * i, -20, -64), 0, 0, 0, 64, 1));
            normalEntities.add(new Entity(wall, new Vector3f(2112, -20, 64 + 128 * i), 0, 0, 0, 64, 1));
            normalEntities.add(new Entity(wall, new Vector3f(64 + 128 * i, -20, 2112), 0, 0, 0, 64, 1));
        }
    }

    private static void grabMouse(boolean next)
    {
        Mouse.setGrabbed(next);
    }

    public static Terrain getTerrain(float x, float z, Terrain[][] terrains)
    {
        return terrains[Math.max(0, (int) Math.floor(x / Terrain.getSize()))][Math.max(0, (int) Math.floor(z / Terrain.getSize()))];
    }

    public static float getTerrainHeight(float x, float z, Terrain[][] terrains)
    {
        return terrains[Math.max(0, (int) Math.floor(x / Terrain.getSize()))][Math.max(0, (int) Math.floor(z / Terrain.getSize()))]
                .getHeightOfTerrain(x, z);
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

    private static TexturedModel getModel(String obj, String texture, Loader loader)
    {
        ModelData data = OBJLoader.loadOBJ(obj);
        RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());

        return new TexturedModel(model, new ModelTexture(loader.loadTexture(texture)));
    }

    private static TexturedModel getNormalModel(String obj, String texture, String normal, String specular, Loader loader)
    {
        RawModel model = NormalMappedObjLoader.loadOBJ(obj, loader);
        TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture(texture)));
        texturedModel.getTexture().setSpecularMap(loader.loadTexture(specular));
        texturedModel.getTexture().setNormalMap(loader.loadTexture(normal));

        return texturedModel;
    }

}
