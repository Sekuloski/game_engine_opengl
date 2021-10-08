package Skybox;

import Entities.Camera;
import Shaders.ShaderProgram;
import ToolBox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class SkyboxShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/Skybox/skyboxVertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/Skybox/skyboxFragmentShader.glsl";

    private static final float ROTATE_SPEED = 1.0f;

    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int fog_location;
    private int cubeMap_location;
    private int cubeMap2_location;
    private int blendFactor_location;

    private float rotaion = 0;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        //rotaion += ROTATE_SPEED * DisplayManager.getDelta();
        Matrix4f.rotate((float) Math.toRadians(rotaion), new Vector3f(0, 1, 0), matrix, matrix);
        super.loadMatrix(location_viewMatrix, matrix);
    }

    public void loadFog(float r, float g, float b)
    {
        super.loadVector(fog_location, new Vector3f(r, g, b));
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        fog_location = super.getUniformLocation("fogColor");
        cubeMap_location = super.getUniformLocation("cubeMap");
        cubeMap2_location = super.getUniformLocation("cubeMap2");
        blendFactor_location = super.getUniformLocation("blendFactor");
    }

    public void connectTextureUnits()
    {
        super.loadInt(cubeMap_location, 0);
        super.loadInt(cubeMap2_location, 1);
    }

    public void loadBlendFactor(float blend)
    {
        super.loadFloat(blendFactor_location, blend);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
