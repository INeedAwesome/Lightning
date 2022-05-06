#version 330 core

in vec2 o_fragTextureCoords;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() { 
    fragColor = texture(textureSampler, o_fragTextureCoords);
}