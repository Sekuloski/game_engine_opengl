package Entities;

import Engine_Tester.MainLoop;
import RenderEngine.DisplayManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera
{
    private static float RUN_SPEED = 100;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float currentFlySpeed = 0;

    private final Vector3f position = new Vector3f(100, 35, 50);
    private float pitch = 10;
    private float yaw = 0;
    private final float distanceFromPlayer = 0;

    private float lastPitch = pitch;
    private float lastYaw = yaw;

    //private float angleAroundPlayer = 0;

    public static boolean detached = false;
    private boolean flag = false;
    private boolean flag2 = true;
    private boolean flying = false;
    private boolean falling = false;

    private final Player player;

    public Camera(Player player)
    {
        this.player = player;
    }

    public void move()
    {
       // calculateAngle();
        checkInputs();
        //if(!Player.escPressed)
        {
            if(detached)
            {
                if(flag2)
                {
                    lastPitch = pitch;
                    lastYaw = yaw;
                    flag2 = false;
                }
                changeRotation(MainLoop.DX,-Mouse.getDY() * 0.1f);

                float distance = currentSpeed * DisplayManager.getDelta();
                float dx = (float) (distance * Math.sin(Math.toRadians(player.getRy())));
                float dz = (float) (distance * Math.cos(Math.toRadians(player.getRy())));
                changePosition(dx, 0, dz);

                distance = currentFlySpeed * DisplayManager.getDelta();
                changePosition(0, distance, 0);

                distance = currentTurnSpeed * DisplayManager.getDelta();
                dx = (float) (distance * Math.sin(Math.toRadians(player.getRy() + 90)));
                dz = (float) (distance * Math.cos(Math.toRadians(player.getRy() + 90)));
                changePosition(dx, 0, dz);
            }
            else
            {
                if(!flag2)
                {
                    pitch = lastPitch;
                    yaw = lastYaw;
                    flag2 = true;
                }

                calculatePitch();
                float horizontal = calculateHorDistance();
                float vertical = calculateVerDistance();
                calculatePosition(horizontal, vertical);
            }

        }

    }

    public void changePosition(float dx, float dy, float dz)
    {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void changeRotation(float dy, float dz)
    {
        this.yaw += dy;
        this.pitch += dz;
    }

    private void checkInputs()
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_W))
        {
            this.currentSpeed = RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S))
        {
            this.currentSpeed = -RUN_SPEED;
        } else
        {
            this.currentSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D))
        {
            this.currentTurnSpeed = -RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_A))
        {
            this.currentTurnSpeed = RUN_SPEED;
        } else
        {
            this.currentTurnSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
        {
            RUN_SPEED = 400;
        }
        else
        {
            RUN_SPEED = 100;
        }

        if (!falling && Keyboard.isKeyDown(Keyboard.KEY_SPACE))
        {
            flying = true;
            this.currentFlySpeed = RUN_SPEED;
        }
        else if(!falling)
        {
            flying = false;
            this.currentFlySpeed = 0;
        }

        if (!flying && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
        {
            falling = true;
            this.currentFlySpeed = -RUN_SPEED;
        }
        else if(!flying)
        {
            falling = false;
            this.currentFlySpeed = 0;
        }

        if(MainLoop.dev)
        {
            if(Keyboard.isKeyDown(Keyboard.KEY_O))
            {
                if(flag)
                {
                    detached = !detached;
                }
                flag = false;
            }
            else
            {
                flag = true;
            }
        }

    }

    private void calculatePosition(float horizontal, float vertical)
    {
        float theta = player.getRy();
        float offsetX = horizontal * (float) Math.sin(Math.toRadians(theta));
        float offsetZ = horizontal * (float) Math.cos(Math.toRadians(theta));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + vertical + 10;
        this.yaw = 180 - (player.getRy());
    }

    private float calculateHorDistance()
    {
        return distanceFromPlayer * (float) Math.cos(Math.toRadians(pitch));
    }

    private float calculateVerDistance()
    {
        return distanceFromPlayer * (float) Math.sin(Math.toRadians(pitch));
    }

    private void calculatePitch()
    {
        if(!Player.escPressed)
        {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
            if(pitch < -90)
            {
                pitch = -90;
            }
            if(pitch > 90)
            {
                pitch = 90;
            }
        }
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }

    public void inverPitch()
    {
        this.pitch = -this.pitch;
    }

}
