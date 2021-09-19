package Engine_Tester;

import Render_Engine.DisplayManager;
import Render_Engine.Loader;
import Render_Engine.RawModel;
import Render_Engine.Renderer;
import org.lwjgl.opengl.Display;

public class MainLoop
{

    // This is the main class that calls all methods needed to display the project to the screen.

    public static void main(String[] args)
    {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        Renderer renderer = new Renderer();

        float[] vertices =
        {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f
        };

        int[] indices =
        {
            0, 1, 3,
            3, 1, 2
        };

        RawModel model = loader.loadToVAO(vertices, indices);

        while (!Display.isCloseRequested())
        {
            // Test render
            renderer.prepare();
            renderer.render(model);
            DisplayManager.updateDisplay();
        }

        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
