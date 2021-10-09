#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;
in vec3 toLightVector[8];
in vec3 fromLightVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudv;
uniform sampler2D normal;
uniform sampler2D depthMap;

uniform vec3 lightColor[8];
uniform vec3 attenuation[8];
uniform vec3 skyColor;

uniform float moveFactor;

const float waveStrength = 0.04;
const float shineDamper = 20.0;
const float reflectivity = 0.5;

void main(void) {

    vec2 ndc = (clipSpace.xy / clipSpace.w) / 2.0 + 0.5;
    vec2 refractTexCoords = vec2(ndc.x, ndc.y);
    vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);

    float near = 0.1;
    float far = 1000.0;
    float depth = texture(depthMap, refractTexCoords).r;
    float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));

    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));

    float waterDepth = floorDistance - waterDistance;

	vec2 distortedTexCoords = texture(dudv, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg*0.1;
	distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+moveFactor);
	vec2 totalDistortion = (texture(dudv, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth / 20.0, 0.0, 1.0);

    refractTexCoords += totalDistortion;
    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    reflectTexCoords += totalDistortion;
    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);

    vec4 reflectColor = texture(reflectionTexture, reflectTexCoords);
    vec4 refractColor = texture(refractionTexture, refractTexCoords);

    vec4 normalMapColor = texture(normal, distortedTexCoords);
    vec3 normalVector = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3.0, normalMapColor.g * 2.0 - 1.0);
    normalVector = normalize(normalVector);

    vec3 viewVector = normalize(toCameraVector);
    float refractiveFactor = dot(viewVector, normalVector);
    refractiveFactor = pow(refractiveFactor, 2.0);

    vec3 totalSpecular = vec3(0.0);

    vec3 reflectedLight = reflect(normalize(toLightVector[0]), normalVector);
    float specular = max(dot(reflectedLight, viewVector), 0.0);
    specular = pow(specular, shineDamper);
    vec3 specularHighlights = lightColor[0] * specular * reflectivity * clamp(waterDepth / 5.0, 0.0, 1.0);

	out_Color = mix(reflectColor, refractColor, refractiveFactor);
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.2) + vec4(specularHighlights, 0.0);
	//out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
    out_Color.a = clamp(waterDepth / 5.0, 0.0, 1.0);
}