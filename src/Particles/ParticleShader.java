package Particles;

import Shaders.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;

public class ParticleShader extends ShaderProgram
{

	private static final String VERTEX_FILE = "particles/particleVShader.glsl";
	private static final String FRAGMENT_FILE = "particles/particleFShader.glsl";

	private int numberOfRows_location;
	private int location_projectionMatrix;

	public ParticleShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations()
	{
		numberOfRows_location = super.getUniformLocation("numberOfRows");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffsets");
		super.bindAttribute(6, "blendFactor");
	}

	protected void loadNumberOfRows(float numberOfRows)
	{
		super.loadFloat(numberOfRows_location, numberOfRows);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix)
	{
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
