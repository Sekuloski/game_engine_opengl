package Engine_Tester;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Entities.Player;
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
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainLoop
{
    public static final int SEA_LEVEL = -70;
    public static float DX;
    private static boolean current = true;
    private static boolean pressed = false;
    private static boolean created = false;
    private static boolean fullscreen = false;
    private static final int x = 1262;
    private static final int z = 455;
    private static final String ENTITY_SOURCE = "txt/entities.txt";
    private static final String MODELS_SOURCE = "txt/models.txt";
    private static final List<Entity> entities = new ArrayList<>();
    private static final List<Entity> normalEntities = new ArrayList<>();
    private static final List<GuiTexture> guis = new ArrayList<>();
    private static final List<Light> lights = new ArrayList<>();
    private static final Map<String, TexturedModel> models = new HashMap<>();
    private static final Map<String, TexturedModel> normalModels = new HashMap<>();
    public static final int sunX = 300000;
    public static final int sunY = 200000;
    public static final int sunZ = 200000;

    public static void main(String[] args)
    {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        TexturedModel person = getModel("player", "player", loader, "person");

        loadModels(loader);

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("soil"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("road"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap2");
        Player player = new Player(person, new Vector3f(x, terrain.getHeightOfTerrain(x, z),z), 0, 45, 0, 1);
        Camera camera = new Camera(player);
        Light sunLight = new Light(new Vector3f(sunX, sunY, sunZ), new Vector3f(0, 0, 0));
        lights.add(sunLight);
        MasterRenderer renderer = new MasterRenderer(loader, sunLight, camera);
        Sun sun = new Sun(loader, new Vector3f(x, sunY / 97.65625f, sunZ / 97.65625f), 0, 100, renderer.getProjectionMatrix());
        renderer.setSun(sun);

        //new Lamp(entities, lights, loader, terrain, 1426, 450);

        new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.75f, -0.75f), new Vector2f(0.15f, 0.25f));
        guis.add(gui);
        GuiRenderer guiRenderer = new GuiRenderer(loader);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
        Mouse.setGrabbed(true);

        WaterFrameBuffers fbos = new WaterFrameBuffers();
        WaterShader shader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, shader, renderer.getProjectionMatrix(), fbos);
        List<WaterTile> waterTiles = new ArrayList<>();
        waterTiles.add(new WaterTile(1024, 1024, SEA_LEVEL));

        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("fire"), 8, true);
        ParticleSystem system = new ParticleSystem(particleTexture, 160, 3, -0.1f, 1, 5f);

        try
        {
            spawnEntities();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        List<Entity> entitiesWithoutPlayer = new ArrayList<>(List.copyOf(entities));
        List<Entity> normalEntitiesWithoutPlayer = new ArrayList<>(List.copyOf(normalEntities));
        entities.add(player);

        Entity entity = null;

        while (!Display.isCloseRequested())
        {
            DX = Mouse.getDX() * 0.3f;
            camera.move();
            player.move(terrain);

            //moveSun(sunLight, player);

            picker.update();
            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if(terrainPoint != null)
            {
                if(!created && Keyboard.isKeyDown(Keyboard.KEY_1))
                {
                    entity = new Entity(models.get("fire_pit"), terrainPoint, 0, 0, 0, 1, 1); // CHANGE THIS
                    entities.add(entity);                                                                               // CHANGE THIS
                    entitiesWithoutPlayer.add(entity);                                                                  // CHANGE THIS
                    created = true;
                }
                else if(created)
                {
                    assert entity != null;
                    entity.setPosition(terrainPoint);
                    entity.setRy(player.getRy());
                    if(Mouse.isButtonDown(0))
                    {
                        String entityString = "fire_pit" + "," + entity.getPosition().x + "," + entity.getPosition().y + ","                         // CHANGE THIS
                                + entity.getPosition().z + "," + entity.getRy() + "," + entity.getScale() + "," + entity.getCollisionScale() + ",F"; // CHANGE THIS
                        try
                        {
                            FileWriter fileWriter = new FileWriter("txt/entities.txt", true);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.newLine();
                            bufferedWriter.write(entityString);
                            bufferedWriter.close();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        entity = null;
                        created = false;
                    }
                }

            }

            ParticleMaster.update(camera);

            renderer.renderShadowMap(entities, normalEntities, sunLight);

            system.generateParticles(new Vector3f(x, terrain.getHeightOfTerrain(x, z), z));

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - SEA_LEVEL);
            camera.getPosition().y -= distance;
            camera.inverPitch();
            renderer.renderScene(entities, normalEntities, terrain, lights, camera, new Vector4f(0, 1, 0, -SEA_LEVEL + 1f),
                    new Vector4f(0, 0, 0, 0));
            camera.getPosition().y += distance;
            camera.inverPitch();

            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(entities, normalEntities, terrain, lights, camera, new Vector4f(0, -1, 0, SEA_LEVEL + 1f),
                    new Vector4f(0, 0, 0, 0));

            fbos.unbindCurrentFrameBuffer();

            GL11.glEnable(GL30.GL_CLIP_DISTANCE1);

            renderer.renderScene(entitiesWithoutPlayer, normalEntitiesWithoutPlayer, terrain, lights, camera, new Vector4f(0, 0, 0, 0)
                                                                                   , new Vector4f(0, 0, 0, 0));
            sun.render(camera);
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

        if (Keyboard.isKeyDown(Keyboard.KEY_F))
        {
            if(!fullscreen)
            {
                try
                {
                    Display.setFullscreen(true);
                } catch (LWJGLException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    Display.setFullscreen(false);
                } catch (LWJGLException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void spawnEntities() throws FileNotFoundException
    {
        try (BufferedReader br = new BufferedReader(new FileReader(ENTITY_SOURCE))) {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] entity = line.split(",");
                Vector3f position = new Vector3f(Float.parseFloat(entity[1]), Float.parseFloat(entity[2]), Float.parseFloat(entity[3]));
                float rY = Float.parseFloat(entity[4]);
                float scale = Float.parseFloat(entity[5]);
                float collisionScale = Float.parseFloat(entity[6]);
                if(entity[7].equals("N"))
                {
                    Entity newEntity = new Entity(normalModels.get(entity[0]), position, 0, rY, 0, scale, collisionScale);
                    normalEntities.add(newEntity);
                }
                else
                {
                    Entity newEntity = new Entity(models.get(entity[0]), position, 0, rY, 0, scale, collisionScale);
                    entities.add(newEntity);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void loadModels(Loader loader)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(MODELS_SOURCE)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split(",");
                String isNormal = parts[0];
                if(isNormal.equals("N"))
                {
                    normalModels.put(parts[1], getNormalModel(parts[2], parts[3], parts[4], parts[5], loader, parts[6]));
                }
                else
                {
                    models.put(parts[1], getModel(parts[2], parts[3], loader, parts[4]));
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void grabMouse(boolean next)
    {
        Mouse.setGrabbed(next);
    }

    private static TexturedModel getModel(String obj, String texture, Loader loader, String name)
    {
        ModelData data = OBJLoader.loadOBJ(obj);
        RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());

        return new TexturedModel(model, new ModelTexture(loader.loadTexture(texture)), name);
    }

    private static TexturedModel getNormalModel(String obj, String texture, String normal, String specular, Loader loader, String name)
    {
        RawModel model = NormalMappedObjLoader.loadOBJ(obj, loader);
        TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture(texture)), name);
        texturedModel.getTexture().setSpecularMap(loader.loadTexture(specular));
        texturedModel.getTexture().setNormalMap(loader.loadTexture(normal));

        return texturedModel;
    }

}
