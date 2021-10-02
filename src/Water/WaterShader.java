package Water;

import Entities.Camera;
import Entities.Light;
import Shaders.ShaderProgram;
import ToolBox.Maths;
import org.lwjgl.util.vector.Matrix4f;

public class WaterShader extends ShaderProgram
{

	private final static String VERTEX_FILE = "src/water/waterVertex.txt";
	private final static String FRAGMENT_FILE = "src/water/waterFragment.txt";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int reflection_location;
	private int refraction_location;
	private int dudv_location;
	private int moveFactor_location;
	private int camera_location;
	private int normal_location;
	private int lightColor_location;
	private int lightPosition_location;
	private int depth_location;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		reflection_location = getUniformLocation("reflectionTexture");
		refraction_location = getUniformLocation("refractionTexture");
		dudv_location = getUniformLocation("dudv");
		moveFactor_location = getUniformLocation("moveFactor");
		camera_location = getUniformLocation("cameraPosition");
		normal_location = getUniformLocation("normal");
		lightColor_location = getUniformLocation("lightColor");
		lightPosition_location = getUniformLocation("lightPosition");
		depth_location = getUniformLocation("depthMap");
	}

	public void loadLight(Light sun)
	{
		super.loadVector(lightColor_location, sun.getColor());
		super.loadVector(lightPosition_location, sun.getPosition());
	}

	public void connectTextureUnits()
	{
		super.loadInt(reflection_location, 0);
		super.loadInt(refraction_location, 1);
		super.loadInt(dudv_location, 2);
		super.loadInt(normal_location, 3);
		super.loadInt(depth_location, 4);
	}

	public void loadMoveFactor(float factor)
	{
		super.loadFloat(moveFactor_location, factor);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(camera_location, camera.getPosition());
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
