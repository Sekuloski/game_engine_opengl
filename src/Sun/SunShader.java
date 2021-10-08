package Sun;

import Shaders.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;

public class SunShader extends ShaderProgram
{

    private static final String VERTEX_FILE = "src/sun/sunVShader.glsl";
    private static final String FRAGMENT_FILE = "src/sun/sunFShader.glsl";

    private int modelViewMatrix_location;
    private int projectionMatrix_location;

    public SunShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations()
    {
        modelViewMatrix_location = super.getUniformLocation("modelViewMatrix");
        projectionMatrix_location = super.getUniformLocation("projectionMatrix");
    }

    @Override
    protected void bindAttributes()
    {
        super.bindAttribute(0, "position");
    }

    protected void loadModelViewMatrix(Matrix4f modelView)
    {
        super.loadMatrix(modelViewMatrix_location, modelView);
    }

    protected void loadProjectionMatrix(Matrix4f projectionMatrix)
    {
        super.loadMatrix(projectionMatrix_location, projectionMatrix);
    }
}
