package Water;

import Entities.Camera;
import Entities.Light;
import Shaders.ShaderProgram;
import ToolBox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class WaterShader extends ShaderProgram
{

	private static final int MAX_LIGHTS = 8;
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
	private int[] lightPosition_location;
	private int[] lightColor_location;
	private int[] attenuation_location;
	private int depth_location;
	private int skyColor_location;


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
		lightColor_location = new int[MAX_LIGHTS];
		lightPosition_location = new int[MAX_LIGHTS];
		attenuation_location = new int[MAX_LIGHTS];

		for(int i = 0; i < MAX_LIGHTS; i++)
		{
			lightColor_location[i] = super.getUniformLocation("lightColor[" + i + "]");
			lightPosition_location[i] = super.getUniformLocation("lightPosition[" + i + "]");
			attenuation_location[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		depth_location = getUniformLocation("depthMap");
		skyColor_location = getUniformLocation("skyColor");
	}

	public void loadLights(List<Light> lights)
	{
		for(int i = 0; i < MAX_LIGHTS; i++)
		{
			if(i < lights.size())
			{
				super.loadVector(lightColor_location[i], lights.get(i).getColor());
				super.loadVector(lightPosition_location[i], lights.get(i).getPosition());
				super.loadVector(attenuation_location[i], lights.get(i).getAttenuation());
			}
			else
			{
				super.loadVector(lightColor_location[i], new Vector3f(0, 0,0));
				super.loadVector(lightPosition_location[i], new Vector3f(0,0,0));
				super.loadVector(attenuation_location[i], new Vector3f(1, 0, 0));
			}
		}
	}

	public void loadSkyColor(float r, float g, float b)
	{
		super.loadVector(skyColor_location, new Vector3f(r, g, b));
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
