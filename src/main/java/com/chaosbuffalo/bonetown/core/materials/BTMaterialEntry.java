package com.chaosbuffalo.bonetown.core.materials;

import com.mojang.blaze3d.shaders.Program;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public class BTMaterialEntry implements IForgeRegistryEntry<BTMaterialEntry> {
    private ResourceLocation location;
    private ResourceLocation vertexShader;
    private ResourceLocation fragShader;

    public BTMaterialEntry(ResourceLocation name, ResourceLocation vertexShader,
                           ResourceLocation fragShader){
        setRegistryName(name);
        this.vertexShader = vertexShader;
        this.fragShader = fragShader;
    }

    public ResourceLocation getVertexShader() {
        return vertexShader;
    }

    public ResourceLocation getFragShader() {
        return fragShader;
    }

    @Override
    public BTMaterialEntry setRegistryName(ResourceLocation name) {
        location = name;
        return this;
    }

    public IBTMaterial getProgram(int programId, Program vertex, Program frag){
        return new BTMaterial(programId, vertex, frag);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return location;
    }

    @Override
    public Class<BTMaterialEntry> getRegistryType() {
        return BTMaterialEntry.class;
    }
}
