package Entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera
{

    private final Vector3f position = new Vector3f(100, 35, 50);
    private float pitch = 10;
    private float yaw = 0;
    private float roll;
    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;


    private Player player;

    public Camera(Player player)
    {
        this.player = player;
    }

    public void move()
    {
       // calculateAngle();
        calculatePitch();
        calculateZoom();
        float horizontal = calculateHorDistance();
        float vertical = calculateVerDistance();
        calculatePosition(horizontal, vertical);

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

    public float getRoll()
    {
        return roll;
    }

    private void calculatePosition(float horizontal, float vertical)
    {
        float theta = player.getRy() + angleAroundPlayer;
        float offsetX = horizontal * (float) Math.sin(Math.toRadians(theta));
        float offsetZ = horizontal * (float) Math.cos(Math.toRadians(theta));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + vertical;
        this.yaw = 180 - (player.getRy() + angleAroundPlayer);
    }

    private float calculateHorDistance()
    {
        return distanceFromPlayer * (float) Math.cos(Math.toRadians(pitch));
    }

    private float calculateVerDistance()
    {
        return distanceFromPlayer * (float) Math.sin(Math.toRadians(pitch));
    }

    private void calculateZoom()
    {
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch()
    {
        if(Mouse.isButtonDown(1))
        {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
    }

//    private void calculateAngle()
//    {
//        if(Mouse.isButtonDown(0))
//        {
//            float angleChange = Mouse.getDX() * 0.3f;
//            angleAroundPlayer -= angleChange;
//        }
//    }

}
