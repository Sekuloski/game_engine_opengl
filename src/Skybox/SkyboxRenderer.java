package Skybox;

import Entities.Camera;
import Entities.Light;
import Models.RawModel;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import RenderEngine.MasterRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class SkyboxRenderer
{

    private static final float SIZE = 500f;

    private static final float RED = 0.5444f;
    private static final float GREEN = 0.62f;
    private static final float BLUE = 0.69f;

    private static final float[] VERTICES =
    {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private static final String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
    private static final String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};

    private final RawModel cube;
    private final int texture;
    private final int nightTexture;
    private final SkyboxShader shader;
    private float time = 0;
    private Light light;

    public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix, Light light)
    {
        cube = loader.loadToVAO(VERTICES, 3);
        texture = loader.loadCubeMap(TEXTURE_FILES);
        nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
        shader = new SkyboxShader();
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
        this.light = light;
    }

    public void render(Camera camera, float r, float g, float b)
    {
        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadFog(r, g, b);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void bindTextures()
    {
        time += DisplayManager.getDelta() * 1000;
        time %= 24000;
        int texture1;
        int texture2;
        float blendFactor;
        if(time >= 0 && time < 5000)
        {
            texture1 = nightTexture;
            texture2 = nightTexture;
            blendFactor = (time - 0)/(5000);
            MasterRenderer.RED = 0;
            MasterRenderer.GREEN = 0;
            MasterRenderer.BLUE = 0;
            light.setColor(new Vector3f(0, 0, 0));

        }
        else if(time >= 5000 && time < 9000)
        {
            texture1 = nightTexture;
            texture2 = texture;
            blendFactor = (time - 5000)/(9000 - 5000);
            MasterRenderer.RED = blendFactor * RED;
            MasterRenderer.GREEN = blendFactor * GREEN;
            MasterRenderer.BLUE = blendFactor * BLUE;
            light.setColor(new Vector3f(blendFactor, blendFactor, blendFactor));
        }
        else if (time >= 9000 && time < 21000){
            texture1 = texture;
            texture2 = texture;
            blendFactor = (time - 9000)/(21000 - 9000);
            shader.loadFog(RED, GREEN, BLUE);
            MasterRenderer.RED = RED;
            MasterRenderer.GREEN = GREEN;
            MasterRenderer.BLUE = BLUE;
            light.setColor(new Vector3f(1, 1, 1));
        }
        else
        {
            texture1 = texture;
            texture2 = nightTexture;
            blendFactor = (time - 21000)/(24000 - 21000);
            shader.loadFog(RED - blendFactor * RED, GREEN - blendFactor * GREEN, BLUE - blendFactor * BLUE);
            MasterRenderer.RED = RED - blendFactor * RED;
            MasterRenderer.GREEN = GREEN - blendFactor * GREEN;
            MasterRenderer.BLUE = BLUE - blendFactor * BLUE;
            light.setColor(new Vector3f(1 - blendFactor, 1 - blendFactor, 1 - blendFactor));
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
        shader.loadBlendFactor(blendFactor);
    }

    public void cleanUp()
    {
        shader.cleanUp();
    }

}
