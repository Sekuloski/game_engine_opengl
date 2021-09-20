package Engine_Tester;

import Entities.Camera;
import Entities.Entity;
import Models.TexturedModel;
import Render_Engine.DisplayManager;
import Render_Engine.Loader;
import Render_Engine.OBJLoader;
import Models.RawModel;
import Render_Engine.Renderer;
import Shaders.StaticShader;
import Textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class MainLoop
{

    // This is the main class that calls all methods needed to display the project to the screen.

    public static void main(String[] args)
    {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);

        RawModel model = OBJLoader.loadObjModel("stall", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
        TexturedModel texturedModel = new TexturedModel(model, texture);

        Entity entity = new Entity(texturedModel, new Vector3f(0, 0,-20), 0, 180, 0, 1);

        Camera camera = new Camera();

        while (!Display.isCloseRequested())
        {
            // Test render
            renderer.prepare();
            camera.move();
            shader.start();
            shader.loadViewMatrix(camera);
            renderer.render(entity, shader);
            shader.stop();
            DisplayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
