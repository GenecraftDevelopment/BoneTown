package com.chaosbuffalo.bonetown.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.Util;

public class GlobalRenderInfo {

    public static final GlobalRenderInfo INFO =  new GlobalRenderInfo();
    public static final Vector3f DIFFUSE_LIGHT_0 = Util.make(new Vector3f(
            0.2F, 1.0F, -0.7F), Vector3f::normalize);
    public static final Vector3f DIFFUSE_LIGHT_1 = Util.make(new Vector3f(
            -0.2F, 1.0F, 0.7F), Vector3f::normalize);

    private PoseStack currentFrameGlobal;

    public GlobalRenderInfo(){

    }

    public void setCurrentFrameGlobal(PoseStack in){
        currentFrameGlobal = in;
    }

    public PoseStack getCurrentFrameGlobal(){
        return currentFrameGlobal;
    }
}
