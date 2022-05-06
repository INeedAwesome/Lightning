#version 330 core

in vec3 position;
in vec2 textureCoords;

out vec2 o_fragTextureCoords;

uniform mat4 transformationMatrix;

void main() { 
    gl_Position = transformationMatrix * vec4(position, 1);
    o_fragTextureCoords = textureCoords;
}