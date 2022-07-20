package com.chaosbuffalo.bonetown.client.render.render_data;

import com.chaosbuffalo.bonetown.core.model.BakedMesh;
import com.chaosbuffalo.bonetown.client.render.platform.GlStateManagerExtended;
import com.mojang.blaze3d.platform.MemoryTracker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;


@OnlyIn(Dist.CLIENT)
public class BTMeshRenderData implements IBTRenderData {
    private final BakedMesh mesh;
    private int vaoId;
    private List<Integer> vboIdList;
    private int vertexCount;

    public BTMeshRenderData(BakedMesh mesh){
        this.mesh = mesh;
        this.vertexCount = mesh.indices.length;
        vboIdList = new ArrayList<>();
        this.vaoId = -1;
    }


    public void uploadBuffers() {
        int vboId = genVBO();


        // Position Data
        ByteBuffer posByteBuffer = MemoryTracker.create(mesh.positions.length * 4);
        FloatBuffer posBuffer = posByteBuffer.asFloatBuffer();
        posBuffer.put(mesh.positions).flip();
        GlStateManagerExtended._glBindBuffer(GL_ARRAY_BUFFER, vboId);
        GlStateManagerExtended._glBufferData(GL_ARRAY_BUFFER, posByteBuffer, GL_STATIC_DRAW);
        GlStateManagerExtended.enableVertexAttribArray(0);
        GlStateManagerExtended._vertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // UV Data
        vboId = genVBO();
        ByteBuffer textCoordsByteBuffer = MemoryTracker.create(mesh.texCoords.length * 4);
        FloatBuffer textCoordsBuffer = textCoordsByteBuffer.asFloatBuffer();
        textCoordsBuffer.put(mesh.texCoords).flip();
        GlStateManagerExtended._glBindBuffer(GL_ARRAY_BUFFER, vboId);
        GlStateManagerExtended._glBufferData(GL_ARRAY_BUFFER, textCoordsByteBuffer, GL_STATIC_DRAW);
        GlStateManagerExtended.enableVertexAttribArray(1);
        GlStateManagerExtended._vertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        // Normal Data
        vboId = genVBO();
        ByteBuffer vecNormalsByteBuffer =MemoryTracker.create(mesh.normals.length * 4);
        FloatBuffer vecNormalsBuffer = vecNormalsByteBuffer.asFloatBuffer();
        vecNormalsBuffer.put(mesh.normals).flip();
        GlStateManagerExtended._glBindBuffer(GL_ARRAY_BUFFER, vboId);
        GlStateManagerExtended._glBufferData(GL_ARRAY_BUFFER, vecNormalsByteBuffer, GL_STATIC_DRAW);
        GlStateManagerExtended.enableVertexAttribArray(2);
        GlStateManagerExtended._vertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);


        // Indices Data
        vboId = genVBO();
        ByteBuffer indicesByteBuffer = MemoryTracker.create(mesh.indices.length * 4);
        IntBuffer indicesBuffer = indicesByteBuffer.asIntBuffer();
        indicesBuffer.put(mesh.indices).flip();
        GlStateManagerExtended._glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        GlStateManagerExtended._glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesByteBuffer, GL_STATIC_DRAW);
    }

    protected int genVBO(){
        int vbo = GlStateManagerExtended._glGenBuffers();
        vboIdList.add(vbo);
        return vbo;
    }

    @Override
    public void render() {
        initRender();
        GlStateManagerExtended.drawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        endRender();
    }

    public void initRender() {
        // Draw the mesh
        GlStateManagerExtended.bindVertexArray(vaoId);
    }

    public void endRender(){
        GlStateManagerExtended.bindVertexArray(0);
    }

    @Override
    public void cleanup() {
        GlStateManagerExtended.disableVertexAttribArray(0);

        // Delete the VBOs
        GlStateManagerExtended._glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            GlStateManagerExtended._glDeleteBuffers(vboId);
        }

        // Delete the VAO
        GlStateManagerExtended.bindVertexArray(0);
        GlStateManagerExtended.deleteVertexArrays(vaoId);
    }

    @Override
    public void upload() {
        vaoId = GlStateManagerExtended.genVertexArrays();
        GlStateManagerExtended.bindVertexArray(vaoId);
        uploadBuffers();
        GlStateManagerExtended._glBindBuffer(GL_ARRAY_BUFFER, 0);
        GlStateManagerExtended.bindVertexArray(0);
    }
}
