package Shaders;

import Entities.Camera;
import Entities.Light;
import ToolBox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class TerrainShader extends ShaderProgram
{

    private static final String VERTEX_FILE = "src/Shaders/terrainVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Shaders/terrainFragmentShader.txt";

    private int tM_location;
    private int pM_location;
    private int vM_location;
    private int lightPosition_location;
    private int lightColor_location;
    private int shine_location;
    private int refl_location;
    private int skyColor_location;
    private int background_location;
    private int r_location;
    private int g_location;
    private int b_location;
    private int blendMap_location;


    public TerrainShader()
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
        skyColor_location = super.getUniformLocation("skyColor");
        background_location = super.getUniformLocation("backgroundTexture");
        r_location = super.getUniformLocation("rTexture");
        g_location = super.getUniformLocation("gTexture");
        b_location = super.getUniformLocation("bTexture");
        blendMap_location = super.getUniformLocation("blendMap");
    }

    public void connectTextureUnits()
    {
        super.loadInt(background_location, 0);
        super.loadInt(r_location, 1);
        super.loadInt(g_location, 2);
        super.loadInt(b_location, 3);
        super.loadInt(blendMap_location, 4);

    }

    public void loadSkyColor(float r, float g, float b)
    {
        super.loadVector(skyColor_location, new Vector3f(r, g, b));
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
