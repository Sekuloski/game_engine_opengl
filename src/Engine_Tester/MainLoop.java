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
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.*;
import java.util.*;

public class MainLoop
{
    public static final int SEA_LEVEL = -70;
    public static float DX;
    private static boolean current = true;
    private static boolean escPressed = false;
    private static boolean FPressed = false;
    private static boolean IPressed = false;
    private static boolean created = false;
    private static boolean fullscreen = false;
    public static boolean dev = true;
    private static final int x = 1262;
    private static final int z = 455;
    private static final String ENTITY_SOURCE = "txt/entities.txt";
    private static final String MODELS_SOURCE = "txt/models.txt";
    private static final String LIGHTS_SOURCE = "txt/lights.txt";
    private static final String PARTICLE_SOURCE = "txt/particles.txt";
    private static final List<Entity> entities = new ArrayList<>();
    private static final List<Entity> normalEntities = new ArrayList<>();
    private static final List<GuiTexture> guis = new ArrayList<>();
    private static final List<Light> lights = new ArrayList<>();
    private static final Map<String, TexturedModel> models = new HashMap<>();
    private static final Map<String, TexturedModel> normalModels = new HashMap<>();
    private static Vector3f firePosition;
    private static Vector3f smokePosition;
    public static final int sunX = 300000;
    public static final int sunY = 200000;
    public static final int sunZ = 200000;

    static String entityModel = "lamp_01";
    static Entity entity = null;
    static boolean change = false;

    public static void main(String[] args)
    {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        setParticlePositions();

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
        Sun sun = new Sun(loader, new Vector3f(3072, sunY / 65f, sunZ / 65f), 0, 1000, renderer.getProjectionMatrix());
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
        ParticleTexture fire_texture = new ParticleTexture(loader.loadTexture("fire"), 8, true);
        ParticleSystem fire = new ParticleSystem(fire_texture, 160, 3, -0.1f, 1, 20f);
        fire.setScaleError(0.5f);

        ParticleTexture smoke_texture = new ParticleTexture(loader.loadTexture("smoke"), 8, false);
        ParticleSystem smoke = new ParticleSystem(smoke_texture, 160, 3, -0.4f, 1, 15f);
        fire.setScaleError(0.5f);

        try
        {
            loadEntities(terrain);
            loadLights();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        List<Entity> entitiesWithoutPlayer = new ArrayList<>(List.copyOf(entities));
        List<Entity> normalEntitiesWithoutPlayer = new ArrayList<>(List.copyOf(normalEntities));
        entities.add(player);

        Light light = null;

        DisplayManager.changeFullscreen(true);

        while (!Display.isCloseRequested())
        {
            DX = Mouse.getDX() * 0.3f;
            camera.move();
            player.move(terrain);

            if(Mouse.isGrabbed())
            {
                Mouse.setCursorPosition(960, 540);
            }

            picker.update();
            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if(terrainPoint != null)
            {
                if(!created && Mouse.isButtonDown(1))
                {
                    //entity = new Entity(models.get(entityModel), terrainPoint, 0, 90, 0, 7, 1); // CHANGE THIS
                    //entities.add(entity);                                                                               // CHANGE THIS
                    //entitiesWithoutPlayer.add(entity);                                                                  // CHANGE THIS
                    light = new Light(new Vector3f(terrainPoint.x, terrain.getHeightOfTerrain(terrainPoint.x, terrainPoint.z) + 10, terrainPoint.z)
                            ,new Vector3f(1, 1, 1), new Vector3f(1, 0.001f, 0.0002f));
                    lights.add(light);
                    created = true;
                }
                else if(created)
                {
                    if(change)
                    {
                        entities.remove(entity);
                        entitiesWithoutPlayer.remove(entity);                                                                  // CHANGE THIS
                        entity = new Entity(models.get(entityModel), terrainPoint, 0, 90, 0, 1, 1); // CHANGE THIS
                        entities.add(entity);                                                                               // CHANGE THIS
                        entitiesWithoutPlayer.add(entity);
                        change = false;
                    }
                    assert entity != null;
                    assert light != null;
                    //entity.setPosition(terrainPoint);
                    //entity.setRy(player.getRy() + 90);
                    //entity.setScale(entity.getScale() + Mouse.getDWheel() * 0.01f);
                    light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + Mouse.getDWheel() * 0.01f + 10, terrainPoint.z));
                    if(Mouse.isButtonDown(0))
                    {
                        //String entityString = entityModel + "," + entity.getPosition().x + "," + entity.getPosition().y + ","                         // CHANGE THIS
                        //        + entity.getPosition().z + "," + entity.getRy() + "," + entity.getScale() + "," + entity.getCollisionScale() + ",F"; // CHANGE THIS
                        String lightString = light.getPosition().x + "," + light.getPosition().y + ","                         // CHANGE THIS
                                + light.getPosition().z + "," + light.getColor().x + "," + light.getColor().y + "," +
                                light.getColor().z + "," + light.getAttenuation().x + "," + light.getAttenuation().y + ","
                                + light.getAttenuation().z;
                        try
                        {
//                            FileWriter fileWriter = new FileWriter(ENTITY_SOURCE, true);
//                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//                            bufferedWriter.newLine();
//                            bufferedWriter.write(entityString);
//                            bufferedWriter.close();

                            FileWriter fileWriter = new FileWriter(LIGHTS_SOURCE, true);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.newLine();
                            bufferedWriter.write(lightString);
                            bufferedWriter.close();

                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        created = false;
                    }
                }

            }

            ParticleMaster.update(camera);

            fire.generateParticles(new Vector3f(firePosition));
            smoke.generateParticles(new Vector3f(smokePosition));
            renderer.renderShadowMap(entities, normalEntities, sunLight);

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
            waterRenderer.render(waterTiles, camera, lights);

            ParticleMaster.renderParticles(camera);

            sun.render(camera);
            guiRenderer.render(guis);

            DisplayManager.updateDisplay();
            checkInputs();
        }

        fbos.cleanUp();
        guiRenderer.cleanUp();
        sun.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        ParticleMaster.cleanUp();
        DisplayManager.closeDisplay();

    }

    private static void setParticlePositions()
    {
        try (BufferedReader br = new BufferedReader(new FileReader(PARTICLE_SOURCE)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split(",");
                if(parts[0].equals("fire"))
                {
                    firePosition = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                }
                else
                {
                    smokePosition = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                }
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void checkInputs()
    {
        if(dev)
        {
            if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
            {
                if(!escPressed)
                {
                    grabMouse(!current);
                    Player.escPressed = current;
                    current = !current;
                    escPressed = true;
                }
            }
            else
            {
                escPressed = false;
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
                if(!FPressed)
                {
                    if(!fullscreen)
                    {
                        DisplayManager.changeFullscreen(true);
                        fullscreen = true;
                    }
                    else
                    {
                        DisplayManager.changeFullscreen(false);
                        fullscreen = false;
                    }
                    FPressed = true;

                }
            }
            else
            {
                FPressed = false;
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_I))
            {
                if(!IPressed)
                {
                    MasterRenderer.fogOn = !MasterRenderer.fogOn;
                    IPressed = true;
                }
            }
            else
            {
                IPressed = false;
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_1))
            {
                entityModel = "fense_01";
                change = true;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_2))
            {
                entityModel = "fense_02";
                change = true;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_3))
            {
                entityModel = "fense_03";
                change = true;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_4))
            {
                entityModel = "fense_stone_01";
                change = true;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_5))
            {
                entityModel = "fense_stone_02";
                change = true;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_6))
            {
                entityModel = "gate_01";
                change = true;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_7))
            {
                entityModel = "gate_02";
                change = true;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_8))
            {
                entityModel = "wheat1";
                change = true;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_9))
            {
                entityModel = "wheat2";
                change = true;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_0))
            {
                entityModel = "sunflower_broken";
                change = true;
            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_DELETE))
        {
            System.exit(0);
        }

    }

    private static void loadEntities(Terrain terrain) throws FileNotFoundException
    {
        try (BufferedReader br = new BufferedReader(new FileReader(ENTITY_SOURCE))) {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split(",");
                float posX = Float.parseFloat(parts[1]);
                float posZ = Float.parseFloat(parts[3]);
                float rY = Float.parseFloat(parts[4]);
                float scale = Float.parseFloat(parts[5]);
                float collisionScale = Float.parseFloat(parts[6]);
                if(parts[7].equals("N"))
                {
                    if(parts[0].equals("wall") || parts[0].equals("bridge"))
                    {
                        Vector3f position = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                        Entity entity = new Entity(normalModels.get(parts[0]), position, 0, rY, 0, scale, collisionScale);
                        normalEntities.add(entity);
                    }
                    else
                    {
                        Vector3f position = new Vector3f(posX, terrain.getHeightOfTerrain(posX, posZ), posZ);
                        Entity entity = new Entity(normalModels.get(parts[0]), position, 0, rY, 0, scale, collisionScale);
                        normalEntities.add(entity);
                    }
                }
                else
                {
                    Vector3f position = new Vector3f(posX, terrain.getHeightOfTerrain(posX, posZ), posZ);
                    Entity entity = new Entity(models.get(parts[0]), position, 0, rY, 0, scale, collisionScale);
                    entities.add(entity);
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
                    if(parts[5].equals("NULL"))
                    {
                        normalModels.put(parts[1], getNormalModel(parts[2], parts[3], parts[4], loader, parts[6]));
                    }
                    else
                    {
                        normalModels.put(parts[1], getNormalModel(parts[2], parts[3], parts[4], parts[5], loader, parts[6]));
                    }
                }
                else
                {
                    if(parts[5].equals("TRUE"))
                    {
                        models.put(parts[1], getModelWithFake(parts[2], parts[3], loader, parts[4]));
                    }
                    else
                    {
                        models.put(parts[1], getModel(parts[2], parts[3], loader, parts[4]));
                    }
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void loadLights()
    {
        try (BufferedReader br = new BufferedReader(new FileReader(LIGHTS_SOURCE)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split(",");
                Vector3f position = new Vector3f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
                Vector3f color = new Vector3f(Float.parseFloat(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
                Vector3f attenuation = new Vector3f(Float.parseFloat(parts[6]), Float.parseFloat(parts[7]), Float.parseFloat(parts[8]));
                lights.add(new Light(position, color, attenuation));
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

    private static TexturedModel getModelWithFake(String obj, String texture, Loader loader, String name)
    {
        ModelData data = OBJLoader.loadOBJ(obj);
        RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture(texture)), name);
        texturedModel.getTexture().setUseFakeLighting(true);

        return texturedModel;
    }

    private static TexturedModel getNormalModel(String obj, String texture, String normal, String specular, Loader loader, String name)
    {
        RawModel model = NormalMappedObjLoader.loadOBJ(obj, loader);
        TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture(texture)), name);
        texturedModel.getTexture().setSpecularMap(loader.loadTexture(specular));
        texturedModel.getTexture().setNormalMap(loader.loadTexture(normal));

        return texturedModel;
    }

    private static TexturedModel getNormalModel(String obj, String texture, String normal, Loader loader, String name)
    {
        RawModel model = NormalMappedObjLoader.loadOBJ(obj, loader);
        TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture(texture)), name);
        texturedModel.getTexture().setNormalMap(loader.loadTexture(normal));

        return texturedModel;
    }

}
