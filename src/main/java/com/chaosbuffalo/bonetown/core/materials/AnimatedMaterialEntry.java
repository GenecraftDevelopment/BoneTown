package com.chaosbuffalo.bonetown.core.materials;

import com.mojang.blaze3d.shaders.Program;
import net.minecraft.resources.ResourceLocation;

public class AnimatedMaterialEntry extends BTMaterialEntry {

    public AnimatedMaterialEntry(ResourceLocation name, ResourceLocation vertexShader,
                                 ResourceLocation fragShader) {
        super(name, vertexShader, fragShader);
    }

    @Override
    public IBTMaterial getProgram(int programId, Program vertex, Program frag) {
        return new AnimatedMaterial(programId, vertex, frag);
    }
}
