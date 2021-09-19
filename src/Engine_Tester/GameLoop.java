package Engine_Tester;

import Render_Engine.DisplayManager;
import org.lwjgl.opengl.Display;

public class GameLoop
{
    public static void main(String[] args)
    {

        DisplayManager.createDisplay();
        while (!Display.isCloseRequested())
        {
            DisplayManager.updateDisplay();
        }

        DisplayManager.closeDisplay();

    }
}
