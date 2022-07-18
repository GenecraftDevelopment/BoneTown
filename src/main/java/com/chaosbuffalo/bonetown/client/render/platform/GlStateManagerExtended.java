package com.chaosbuffalo.bonetown.client.render.platform;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.NativeType;

import java.nio.FloatBuffer;

import static org.lwjgl.system.MemoryUtil.memAddress;

/**
 * Created by Jacob on 1/25/2020.
 */
public class GlStateManagerExtended extends GlStateManager {

    public static int genVertexArrays() {
        RenderSystem.assertOnGameThreadOrInit();
        return GL30.glGenVertexArrays();
    }

    public static void bindVertexArray(int array) {
        RenderSystem.assertOnGameThreadOrInit();
        GL30.glBindVertexArray(array);
    }

    public static void enableVertexAttribArray(int array){
        RenderSystem.assertOnGameThreadOrInit();
        GL30.glEnableVertexAttribArray(array);
    }

    public static void deleteVertexArrays(int vaoId){
        RenderSystem.assertOnGameThreadOrInit();
        GL30.glDeleteVertexArrays(vaoId);
    }

    public static void disableVertexAttribArray(int array){
        RenderSystem.assertOnGameThreadOrInit();
        GL30.glDisableVertexAttribArray(array);
    }

    public static void drawElements(int mode, int count, int type, long indices){
        RenderSystem.assertOnGameThreadOrInit();
        GL11.glDrawElements(mode, count, type, indices);
    }

    public static void uniformMatrix4fCount(@NativeType("GLint") int location,
                                            @NativeType("GLboolean") boolean transpose,
                                            int count,
                                            @NativeType("GLfloat const *") FloatBuffer value){
        RenderSystem.assertOnGameThreadOrInit();
        GL20C.nglUniformMatrix4fv(location, (count * 16) >> 4, transpose, memAddress(value));
    }

}
