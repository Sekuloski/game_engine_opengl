package Entities;

import Models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class Well
{

    public Well(TexturedModel roof, TexturedModel middle, TexturedModel lower, List<Entity> entities, Vector3f position)
    {
        entities.add(new Entity(roof, new Vector3f(position.x, position.y, position.z), 0, 0 ,0 ,1,1));
        entities.add(new Entity(middle, new Vector3f(position.x, position.y, position.z), 0, 0 ,0 ,1,1));
        entities.add(new Entity(lower, new Vector3f(position.x, position.y, position.z), 0, 0 ,0 ,1,1));
    }
}
