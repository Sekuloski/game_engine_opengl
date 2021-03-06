package Water;

import Entities.Camera;
import Entities.Light;
import Models.RawModel;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import RenderEngine.MasterRenderer;
import ToolBox.Maths;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class WaterRenderer
{

	private RawModel quad;
	private final WaterShader shader;
	private final WaterFrameBuffers fbos;

	private static final String DUDV_MAP = "waterDUDV";
	private static final String NORMAL_MAP = "matchingNormalMap";
	private static final float WAVE_SPEED = 0.03f;

	private float moveFactor = 0;
	private final int dudv;
	private final int normal;

	public WaterRenderer(Loader loader, WaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers fbos)
	{
		this.shader = shader;
		this.fbos = fbos;
		dudv = loader.loadTexture(DUDV_MAP);
		normal = loader.loadTexture(NORMAL_MAP);
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		setUpVAO(loader);
	}

	public void render(List<WaterTile> water, Camera camera, List<Light> lights)
	{
		prepareRender(camera, lights);
		for (WaterTile tile : water)
		{
			Matrix4f modelMatrix = Maths.createTransformationMatrix(
					new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
					WaterTile.TILE_SIZE);
			shader.loadModelMatrix(modelMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
		}
		unbind();
	}
	
	private void prepareRender(Camera camera, List<Light> lights)
	{
		shader.start();
		shader.loadViewMatrix(camera);
		moveFactor += WAVE_SPEED * DisplayManager.getDelta();
		moveFactor %= 1;
		shader.loadMoveFactor(moveFactor);
		shader.loadLights(lights);
		shader.loadSkyColor(MasterRenderer.RED, MasterRenderer.BLUE, MasterRenderer.GREEN);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudv);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	}
	
	private void unbind()
	{
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void setUpVAO(Loader loader)
	{
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = loader.loadToVAO(vertices, 2);
	}

}
