package Sun;

import Entities.Camera;
import Models.RawModel;
import RenderEngine.Loader;
import Textures.ModelTexture;
import ToolBox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Sun
{

    private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
    private static final float[] TEXTURES = {0, 0, 0, 1, 1, 1, 1, 0};

    private final RawModel quad;
    private final SunShader shader;
    private Vector3f position;
    private final float rotation;
    private final float scale;
    private final ModelTexture texture;

    public Sun(Loader loader, Vector3f position, float rotation, float scale, Matrix4f projectionMatrix)
    {
        texture = new ModelTexture(loader.loadTexture("sun"));
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        quad = loader.loadToVAO(VERTICES, 2, TEXTURES);
        shader = new SunShader();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Camera camera)
    {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        prepare();
        updateModelViewMatrix(position, rotation, scale, viewMatrix);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        finishRendering();
    }

    private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix)
    {
        Matrix4f modelMatrix = new Matrix4f();
        Matrix4f.translate(position, modelMatrix, modelMatrix);
        modelMatrix.m00 = viewMatrix.m00;
        modelMatrix.m01 = viewMatrix.m10;
        modelMatrix.m02 = viewMatrix.m20;
        modelMatrix.m10 = viewMatrix.m01;
        modelMatrix.m11 = viewMatrix.m11;
        modelMatrix.m12 = viewMatrix.m21;
        modelMatrix.m20 = viewMatrix.m02;
        modelMatrix.m21 = viewMatrix.m12;
        modelMatrix.m22 = viewMatrix.m22;

        Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 0, 1), modelMatrix, modelMatrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
        Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null);
        shader.loadModelViewMatrix(modelViewMatrix);

    }

    private void prepare()
    {
        shader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void finishRendering()
    {
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp()
    {
        shader.cleanUp();
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public float getRotation()
    {
        return rotation;
    }

    public float getScale()
    {
        return scale;
    }
}