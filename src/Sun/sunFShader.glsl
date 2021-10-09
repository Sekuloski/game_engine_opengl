#version 140

out vec4 out_colour;

in vec2 textureCoords;

uniform sampler2D textureSample;

void main(void)
{
    out_colour = texture(textureSample, textureCoords);
}