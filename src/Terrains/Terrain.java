package Terrains;

import Models.RawModel;
import RenderEngine.Loader;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import ToolBox.Maths;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Terrain
{

    private static final float SIZE = 2048;
    private static final float MAX_HEIGHT = 80;
    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

    private final float x;
    private final float z;
    private final RawModel model;
    private final TerrainTexturePack texturePack;
    private final TerrainTexture blendMap;

    private float[][] heights;

    public float getX()
    {
        return x;
    }

    public float getZ()
    {
        return z;
    }

    public static float getSize()
    {
        return SIZE;
    }

    public RawModel getModel()
    {
        return model;
    }


    public Terrain(int gridSquareX, int gridSquareZ, Loader loader, TerrainTexturePack terrainTexturePack, TerrainTexture blendMap, String heightMap)
    {
        this.texturePack = terrainTexturePack;
        this.blendMap = blendMap;
        this.x = gridSquareX * SIZE;
        this.z = gridSquareZ * SIZE;
        this.model = generateTerrain(loader, heightMap);
    }

    private RawModel generateTerrain(Loader loader, String heightMap)
    {
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(new File("res/" + heightMap + ".png"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        assert image != null;
        int VERTEX_COUNT = image.getHeight();
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++)
        {
            for(int j=0;j<VERTEX_COUNT;j++)
            {
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j, i, image);
                heights[j][i] = height;
                vertices[vertexPointer*3+1] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, image);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++)
        {
            for(int gx=0;gx<VERTEX_COUNT-1;gx++)
            {
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;

                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormal(int x, int y, BufferedImage image)
    {
        float heightL = getHeight(x - 1, y ,image);
        float heightR = getHeight(x + 1, y, image);
        float heightD = getHeight(x, y - 1, image);
        float heightU = getHeight(x, y + 1, image);

        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();

        return normal;
    }

    public float getHeightOfTerrain(float x, float z)
    {
        float terrainX = Math.abs(x - this.x);
        float terrainZ = Math.abs(z - this.z);
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridSquareX = (int) Math.floor(terrainX / gridSquareSize);
        int gridSquareZ = (int) Math.floor(terrainZ / gridSquareSize);

        if(gridSquareX >= heights.length - 1 || gridSquareZ >= heights.length - 1 || gridSquareX < 0 || gridSquareZ < 0)
        {
            return 0;
        }

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float height;
        if (xCoord <= (1-zCoord)) {
            height = Maths.barryCentric(new Vector3f(0, heights[gridSquareX][gridSquareZ], 0), new Vector3f(1,
                            heights[gridSquareX + 1][gridSquareZ], 0), new Vector3f(0,
                            heights[gridSquareX][gridSquareZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            height = Maths.barryCentric(new Vector3f(1, heights[gridSquareX + 1][gridSquareZ], 0), new Vector3f(1,
                            heights[gridSquareX + 1][gridSquareZ + 1], 1), new Vector3f(0,
                            heights[gridSquareX][gridSquareZ + 1], 1), new Vector2f(xCoord, zCoord));
        }

        return height;
    }

    private float getHeight(int x, int y, BufferedImage image)
    {
        if(x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight())
        {
            return 0;
        }
        float height = image.getRGB(x, y);
        height += MAX_PIXEL_COLOR / 2f;
        height /= MAX_PIXEL_COLOR / 2f;
        height *= MAX_HEIGHT;

        return height;
    }

    public TerrainTexturePack getTexturePack()
    {
        return texturePack;
    }

    public TerrainTexture getBlendMap()
    {
        return blendMap;
    }

}
