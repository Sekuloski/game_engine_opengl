package Particles;

import Shaders.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class ParticleShader extends ShaderProgram
{

	private static final String VERTEX_FILE = "src/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "src/particles/particleFShader.txt";

	private int location_modelViewMatrix;
	private int location_projectionMatrix;
	private int texOffset1_location;
	private int texOffset2_location;
	private int texCoordInfo_location;

	public ParticleShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations()
	{
		location_modelViewMatrix = super.getUniformLocation("modelViewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		texOffset1_location = super.getUniformLocation("texOffset1");
		texOffset2_location = super.getUniformLocation("texOffset2");
		texCoordInfo_location = super.getUniformLocation("texCoordInfo");
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");
	}

	protected void loadTextureCoordInfo(Vector2f offset1, Vector2f offset2, float numRows, float blend)
	{
		super.load2DVector(texOffset1_location, offset1);
		super.load2DVector(texOffset2_location, offset2);
		super.load2DVector(texCoordInfo_location, new Vector2f(numRows, blend));
	}

	protected void loadModelViewMatrix(Matrix4f modelView)
	{
		super.loadMatrix(location_modelViewMatrix, modelView);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix)
	{
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
