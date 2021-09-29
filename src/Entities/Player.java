package Entities;

import Models.TexturedModel;
import Render_Engine.DisplayManager;
import Terrains.Terrain;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Player extends Entity
{

    private static final float RUN_SPEED = 50;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP = 30;
    private static final float TERRAIN = 0;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;
    private boolean grounded = true;

    public Player(TexturedModel model, Vector3f position, float rx, float ry, float rz, float scale)
    {
        super(model, position, rx, ry, rz, scale);
    }

    public void move(Terrain terrain)
    {
        checkInputs();
        super.changeRotation(0, Mouse.getDX() * 0.3f, 0);
        float distance = currentSpeed * DisplayManager.getDelta();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRy())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRy())));
        super.changePosition(dx, 0, dz);
        distance = currentTurnSpeed * DisplayManager.getDelta();
        dz = (float) (distance * Math.sin(Math.toRadians(super.getRy())));
        dx = (float) (distance * Math.cos(Math.toRadians(super.getRy())));
        super.changePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * DisplayManager.getDelta();
        super.changePosition(0, upwardsSpeed * DisplayManager.getDelta(), 0);

        float height = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);

        if(super.getPosition().y <= height)
        {
            upwardsSpeed = 0;
            grounded = true;
            super.getPosition().y = height;
        }

    }

    private void jump()
    {
        this.upwardsSpeed = JUMP;
    }

    private void checkInputs()
    {
        if(Keyboard.isKeyDown(Keyboard.KEY_W))
        {
            this.currentSpeed = RUN_SPEED;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_S))
        {
            this.currentSpeed = -RUN_SPEED;
        }
        else
        {
            this.currentSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_D))
        {
            this.currentTurnSpeed = RUN_SPEED;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_A))
        {
            this.currentTurnSpeed = -RUN_SPEED;
        }
        else
        {
            this.currentTurnSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
        {
            if(grounded)
            {
                jump();
                grounded = false;
            }
        }
//        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
//        {
//            //position.y -= 0.2;
//        }
    }

}
