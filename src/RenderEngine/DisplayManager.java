package RenderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS = 144;

    private static long lastFrame;
    private static float delta;

    public static void createDisplay()
    {

        ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);

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

        lastFrame = getTime();

    }

    private static long getTime()
    {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }

    public static void updateDisplay()
    {
        Display.sync(FPS);
        Display.update();
        long currentTime = getTime();
        delta = (currentTime - lastFrame) / 1000f;
        lastFrame = currentTime;
    }

    public static float getDelta()
    {
        return delta;
    }


    public static void closeDisplay()
    {
        Display.destroy();
    }

}
