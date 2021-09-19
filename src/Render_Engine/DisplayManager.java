package Render_Engine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS = 144;

    public static void createDisplay()
    {

        ContextAttribs attribs = new ContextAttribs(3, 2);
        ContextAttribs forwardCompatible = attribs.withForwardCompatible(true);
        ContextAttribs profileCore = attribs.withProfileCore(true);

        try
        {

            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), attribs);
            Display.setTitle("OpenGL PROJECT");

        } catch (LWJGLException e)
        {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, WIDTH, HEIGHT);

    }

    public static void updateDisplay()
    {
        Display.sync(FPS);
        Display.update();
    }

    public static void closeDisplay()
    {
        Display.destroy();
    }

}
