#version 400 core

in vec2 outTextureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[8];
in vec3 toCameraVector;
in float visibility;

out vec4 outColor;

uniform sampler2D textureSampler;
uniform sampler2D specularMap;
uniform float usesSpecularMap;

uniform vec3 lightColor[8];
uniform float shineDamper;
uniform vec3 attenuation[8];
uniform float reflectivity;
uniform vec3 skyColor;

void main(void)
{
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for(int i = 0; i < 8; i++)
    {
        float distance = length(toLightVector[i]);
        float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * pow(distance, 2));
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDot = dot(unitNormal, unitLightVector);
        float brightness = max(nDot, 0.0);
        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attFactor;
    }

    totalDiffuse = max(totalDiffuse, 0.1);

    vec4 textureColor = texture(textureSampler, outTextureCoords);
    if(textureColor.a < 0.5)
    {
        discard;
    }

    if(usesSpecularMap > 0.5)
    {
        vec4 mapInfo = texture(specularMap, outTextureCoords);
        totalSpecular *= mapInfo.r;
    }

    outColor = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
    //outColor = mix(vec4(skyColor, 1.0), outColor, visibility);
}