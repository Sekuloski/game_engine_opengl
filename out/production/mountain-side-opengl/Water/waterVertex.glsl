#version 400 core

in vec2 position;

out vec4 clipSpace;
out vec2 textureCoords;
out vec3 toCameraVector;
out vec3 fromLightVector;
out vec3 toLightVector[8];
out float visibility;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;
uniform vec3 lightPosition;

const float tiling = 20.0;

const float density = 0.0035;
const float gradient = 5.0;

void main(void)
{
    vec4 worldPosition = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);
    clipSpace = projectionMatrix * viewMatrix * worldPosition;
    vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = clipSpace;
    textureCoords = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5) * tiling;
    toCameraVector = cameraPosition - worldPosition.xyz;
    fromLightVector = worldPosition.xyz - lightPosition;

    for(int i = 0; i < 8; i++)
    {
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }

    float distance = length(positionRelativeToCamera.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}