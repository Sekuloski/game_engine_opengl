package RenderEngine;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.TexturedModel;
import NormalMappingRenderer.NormalMappingRenderer;
import Shaders.StaticShader;
import Shaders.TerrainShader;
import Shadows.ShadowMapMasterRenderer;
import Skybox.SkyboxRenderer;
import Sun.Sun;
import Terrains.Terrain;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer
{

    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 10000;

    public static float RED = 0.5444f;
    public static float GREEN = 0.62f;
    public static float BLUE = 0.69f;

    private Matrix4f projectionMatrix;

    private final StaticShader shader = new StaticShader();
    private final EntityRenderer renderer;
    private final TerrainRenderer terrainRenderer;
    private final TerrainShader terrainShader = new TerrainShader();
    private final SkyboxRenderer skyboxRenderer;
    private final NormalMappingRenderer normalMappingRenderer;
    private final ShadowMapMasterRenderer shadowMapMasterRenderer;

    private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private final Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<>();

    private final List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer(Loader loader, Light light, Camera camera)
    {
        enableCulling();
        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix, light);
        normalMappingRenderer = new NormalMappingRenderer(projectionMatrix);
        this.shadowMapMasterRenderer = new ShadowMapMasterRenderer(camera);
    }

    public void setSun(Sun sun)
    {
        skyboxRenderer.setSun(sun);
    }

    public static void enableCulling()
    {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public void renderShadowMap(List<Entity> entityList, List<Entity> normalMappedEntities, Light sun){
        for(Entity entity : entityList)
        {
            processEntity(entity);
        }
        for(Entity entity : normalMappedEntities)
        {
            processEntity(entity);
        }
        shadowMapMasterRenderer.render(normalMapEntities, sun);
        normalMapEntities.clear();
        shadowMapMasterRenderer.render(entities, sun);
        entities.clear();
    }

    public int getShadowMapTexture()
    {
        return shadowMapMasterRenderer.getShadowMap();
    }

    public static void disableCulling()
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void renderScene(List<Entity> entities, List<Entity> normalEntities, Terrain terrain, List<Light> lights, Camera camera,
                                                    Vector4f clipPlane1, Vector4f clipPlane2)
    {
        processTerrain(terrain);
        for(Entity entity : entities)
        {
            processEntity(entity);
        }
        for(Entity entity : normalEntities)
        {
            processNormalMapEntity(entity);
        }
        render(lights, camera, clipPlane1, clipPlane2);
    }

    public void render(List<Light> lights, Camera camera, Vector4f clipPlane1, Vector4f clipPlane2)
    {
        prepare();
        shader.start();
        shader.loadClipPlane(clipPlane1, clipPlane2);
        shader.loadSkyColor(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        normalMappingRenderer.render(normalMapEntities, clipPlane1, lights, camera);
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane1, clipPlane2);
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains, shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();
        skyboxRenderer.render(camera, RED, GREEN, BLUE);
        terrains.clear();
        entities.clear();
    }

    public void processTerrain(Terrain terrain)
    {
        terrains.add(terrain);
    }

    public void prepare()
    {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(RED, GREEN, BLUE, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
    }

    public void processEntity(Entity entity)
    {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch != null)
        {
            batch.add(entity);
        }
        else
        {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processNormalMapEntity(Entity entity)
    {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMapEntities.get(entityModel);
        if(batch != null)
        {
            batch.add(entity);
        }
        else
        {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            normalMapEntities.put(entityModel, newBatch);
        }
    }

    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }

    private void createProjectionMatrix()
    {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public void cleanUp()
    {
        shader.cleanUp();
        terrainShader.cleanUp();
        skyboxRenderer.cleanUp();
        normalMappingRenderer.cleanUp();
    }

}
