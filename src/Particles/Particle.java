package Particles;


import Entities.Camera;
import Entities.Player;
import RenderEngine.DisplayManager;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Particle
{

    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float rotation;
    private float scale;
    private float elapsedTime = 0;

    private ParticleTexture texture;
    private float distance;

    private Vector2f texOffset1 = new Vector2f();

    private Vector2f texOffset2 = new Vector2f();
    private float blendFactor;
    public Particle(Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale, ParticleTexture texture)
    {
        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        ParticleMaster.addParticles(this);
    }

    protected boolean update(Camera camera)
    {
        velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.getDelta();
        Vector3f change = new Vector3f(velocity);
        change.scale(DisplayManager.getDelta());
        Vector3f.add(change, position, position);
        distance = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
        updateTextureCoordInfo();
        elapsedTime += DisplayManager.getDelta();

        return elapsedTime < lifeLength;
    }

    private void updateTextureCoordInfo()
    {
        float lifeFactor = elapsedTime / lifeLength;
        int stageCount = (int) Math.pow(texture.getNumberOfRows(), 2);
        float atlasProgression = lifeFactor * stageCount;
        int index1 = (int) Math.floor(atlasProgression);
        int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
        this.blendFactor = atlasProgression % 1;
        setTextureOffstet(texOffset1, index1);
        setTextureOffstet(texOffset2, index2);

    }

    private void setTextureOffstet(Vector2f offset, int index)
    {
        int column = index % texture.getNumberOfRows();
        int row = index / texture.getNumberOfRows();

        offset.x = (float) column / texture.getNumberOfRows();
        offset.y = (float) row / texture.getNumberOfRows();
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public ParticleTexture getTexture()
    {
        return texture;
    }

    public float getRotation()
    {
        return rotation;
    }

    public float getScale()
    {
        return scale;
    }

    public float getDistance()
    {
        return distance;
    }

    public Vector2f getTexOffset1()
    {
        return texOffset1;
    }

    public Vector2f getTexOffset2()
    {
        return texOffset2;
    }

    public float getBlendFactor()
    {
        return blendFactor;
    }
}
