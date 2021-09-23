package Shaders;

import Entities.Camera;
import Entities.Light;
import ToolBox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class StaticShader extends ShaderProgram
{

    private static final String VERTEX_FILE = "src/Shaders/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Shaders/fragmentShader.txt";

    private int tM_location;
    private int pM_location;
    private int vM_location;
    private int lightPosition_location;
    private int lightColor_location;
    private int shine_location;
    private int refl_location;
    private int fakeLighting_location;
    private int skyColor_location;

    public StaticShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes()
    {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations()
    {
        tM_location = super.getUniformLocation("transformationMatrix");
        pM_location = super.getUniformLocation("projectionMatrix");
        vM_location = super.getUniformLocation("viewMatrix");
        lightColor_location = super.getUniformLocation("lightColor");
        lightPosition_location = super.getUniformLocation("lightPosition");
        shine_location = super.getUniformLocation("shineDamper");
        refl_location = super.getUniformLocation("reflectivity");
        fakeLighting_location = super.getUniformLocation("useFakeLighting");
        skyColor_location = super.getUniformLocation("skyColor");

    }

    public void loadSkyColor(float r, float g, float b)
    {
        super.loadVector(skyColor_location, new Vector3f(r, g, b));
    }

    public void loadFakeLighting(boolean useFake)
    {
        super.loadBoolean(fakeLighting_location, useFake);
    }


    public void loadShine(float damper, float reflectivity)
    {
        super.loadFloat(shine_location, damper);
        super.loadFloat(refl_location, reflectivity);
    }

    public void loadTM(Matrix4f matrix)
    {
        super.loadMatrix(tM_location, matrix);
    }

    public void loadLight(Light light)
    {
        super.loadVector(lightPosition_location, light.getPosition());
        super.loadVector(lightColor_location, light.getColor());
    }

    public void loadViewMatrix(Camera camera)
    {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(vM_location, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection)
    {
        super.loadMatrix(pM_location, projection);
    }

}
