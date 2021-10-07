package Shaders;

import Entities.Camera;
import Entities.Light;
import ToolBox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;

public class TerrainShader extends ShaderProgram
{

    private static final String VERTEX_FILE = "src/Shaders/terrainVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Shaders/terrainFragmentShader.txt";

    private static final int MAX_LIGHTS = 8;

    private int tM_location;
    private int pM_location;
    private int vM_location;
    private int[] lightPosition_location;
    private int[] lightColor_location;
    private int[] attenuation_location;
    private int shine_location;
    private int refl_location;
    private int skyColor_location;
    private int background_location;
    private int r_location;
    private int g_location;
    private int b_location;
    private int blendMap_location;
    private int plane1_location;
    private int plane2_location;
    private int shadowMap_location;
    private int toShadowMap_location;


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
        shine_location = super.getUniformLocation("shineDamper");
        refl_location = super.getUniformLocation("reflectivity");
        skyColor_location = super.getUniformLocation("skyColor");
        background_location = super.getUniformLocation("backgroundTexture");
        r_location = super.getUniformLocation("rTexture");
        g_location = super.getUniformLocation("gTexture");
        b_location = super.getUniformLocation("bTexture");
        blendMap_location = super.getUniformLocation("blendMap");
        plane1_location = super.getUniformLocation("plane1");
        plane2_location = super.getUniformLocation("plane2");
        toShadowMap_location = super.getUniformLocation("toShadowMapSpace");
        shadowMap_location = super.getUniformLocation("shadowMap");

        lightColor_location = new int[MAX_LIGHTS];
        lightPosition_location = new int[MAX_LIGHTS];
        attenuation_location = new int[MAX_LIGHTS];

        for(int i = 0; i < MAX_LIGHTS; i++)
        {
            lightColor_location[i] = super.getUniformLocation("lightColor[" + i + "]");
            lightPosition_location[i] = super.getUniformLocation("lightPosition[" + i + "]");
            attenuation_location[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void loadToShadowMapSpaceMatrix(Matrix4f matrix)
    {
        super.loadMatrix(toShadowMap_location, matrix);
    }

    public void loadClipPlane(Vector4f plane1, Vector4f plane2)
    {
        super.loadVector(plane1_location, plane1);
        super.loadVector(plane2_location, plane2);
    }

    public void connectTextureUnits()
    {
        super.loadInt(background_location, 0);
        super.loadInt(r_location, 1);
        super.loadInt(g_location, 2);
        super.loadInt(b_location, 3);
        super.loadInt(blendMap_location, 4);
        super.loadInt(shadowMap_location, 5);

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
