package Engine_Tester;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.RawModel;
import Models.TexturedModel;
import Render_Engine.DisplayManager;
import Render_Engine.Loader;
import Render_Engine.MasterRenderer;
import Render_Engine.OBJLoader;
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

        RawModel model = OBJLoader.loadObjModel("dragon", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
        TexturedModel texturedModel = new TexturedModel(model, texture);

        ModelTexture texture1 = texturedModel.getTexture();
        texture1.setShineDamper(10);
        texture1.setReflectivity(1);

        Entity entity = new Entity(texturedModel, new Vector3f(0, -4,-20), 0, 180, 0, 1);
        Light light = new Light(new Vector3f(-10, 10, -10), new Vector3f(1, 1, 1));

        Camera camera = new Camera();

        MasterRenderer renderer = new MasterRenderer();

        while (!Display.isCloseRequested())
        {
            // Test render

            camera.move();
            renderer.processEntity(entity);
            entity.changeRotation(0, 0.2f, 0);
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
