#version 330 core

in vec2 o_fragTextureCoords;
in vec3 o_normal;
in vec3 o_pos;

out vec4 fragColor;

struct Material 
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textureCoords) 
{ 
    if(material.hasTexture == 1) 
    { 
        ambientC = texture(textureSampler, textureCoords);
        diffuseC = ambientC;
        specularC = ambientC;
    }
    else 
    { 
       ambientC = material.ambient; 
       diffuseC = material.diffuse;
       specularC = material.specular;
    }
}

void main() 
{ 
    setupColors(material, o_fragTextureCoords);
    fragColor = ambientC * vec4(ambientLight, 1);
}