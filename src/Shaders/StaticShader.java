package Shaders;

import Entities.Camera;
import ToolBox.Maths;
import org.lwjgl.util.vector.Matrix4f;

public class StaticShader extends ShaderProgram
{

    private static final String VERTEX_FILE = "src/Shaders/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Shaders/fragmentShader.txt";

    private int tM_location;
    private int pM_location;
    private int vM_location;

    public StaticShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes()
    {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    @Override
    protected void getAllUniformLocations()
    {
        tM_location = super.getUniformLocation("transformationMatrix");
        pM_location = super.getUniformLocation("projectionMatrix");
        vM_location = super.getUniformLocation("viewMatrix");
    }

    public void loadTM(Matrix4f matrix)
    {
        super.loadMatrix(tM_location, matrix);
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
