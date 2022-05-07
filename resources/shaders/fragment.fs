#version 330 core

in vec2 o_fragTextureCoords;
in vec3 o_normal;
in vec3 o_pos;

out vec4 out_color;

struct Material 
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight 
{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight 
{ 
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;
uniform float specularPower;
uniform DirectionalLight directionalLight;
uniform PointLight pointLight;

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

vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDirection, vec3 normal) 
{ 
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    // diffuse light
    float diffuseFactor = max(dot(normal, toLightDirection), 0.0);
    diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    //specular light / calculating shit
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDirection = -toLightDirection;
    vec3 reflectedLight = normalize(reflect(fromLightDirection, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    // setting the var's
    specularFactor = pow(specularFactor, specularPower);
    specularColor = specularC * lightIntensity * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return (diffuseColor + specularColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) 
{ 
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec4 lightColor = calcLightColor(light.color, light.intensity, position, toLightDirection, normal);

    float distance = length(lightDirection);
    //setting max distance / attenuation
    float attenuationInv = light.constant + light.linear * distance + light.exponent * distance * distance;
    return lightColor / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) 
{ 
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

void main() 
{ 
    setupColors(material, o_fragTextureCoords);
    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, o_pos, o_normal);
    diffuseSpecularComp += calcPointLight(pointLight, o_pos, o_normal);

    out_color = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}