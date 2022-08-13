#version 420

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 100;

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;
layout (location=3) in vec4 jointWeights;
layout (location=4) in ivec4 jointIndices;

out vec2 outTexCoord;
out vec3 vertex;
flat out vec3 normal;

uniform mat4 model_view;
uniform mat4 proj_mat;
uniform mat4 joints_matrix[MAX_JOINTS];
uniform mat4 inverse_bind_pose[MAX_JOINTS];

mat4 scale(float c)
{
    return mat4(
        c, 0, 0, 0,
        0, c, 0, 0,
        0, 0, c, 0,
        0, 0, 0, 1
    );
}


void main()
{

    vec4 initPos = vec4(0, 0, 0, 0);
    vec4 initNormal = vec4(0, 0, 0, 0);
    outTexCoord = texCoord;

    for(int i = 0; i < MAX_WEIGHTS; i++)
    {
        float weight = jointWeights[i];
        if(weight > 0) {
            int jointIndex = jointIndices[i];

            mat4 pose    = scale(0.015) * joints_matrix[jointIndex];
            mat4 inverse = inverse_bind_pose[jointIndex];
            mat4 boneMat = pose * inverse; // joint space

            vec4 localPosition = boneMat * vec4(position, 1);
            initPos = weight * localPosition;

            vec4 tmpNormal = boneMat * vec4(vertexNormal, 0);
            initNormal += weight * tmpNormal;
        }
    }
    normal = normalize(model_view * initNormal).xyz;
    vertex = vec3(model_view * initPos);

    gl_Position = proj_mat * model_view * initPos;
}