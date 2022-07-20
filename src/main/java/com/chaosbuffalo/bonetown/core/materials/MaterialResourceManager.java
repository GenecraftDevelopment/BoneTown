package com.chaosbuffalo.bonetown.core.materials;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.BoneTownRegistry;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.util.*;


@OnlyIn(Dist.CLIENT)
public class MaterialResourceManager implements ResourceManagerReloadListener {

    static final Logger LOGGER = LogUtils.getLogger();
    public static final MaterialResourceManager INSTANCE = new MaterialResourceManager();

    private ResourceManager manager = Minecraft.getInstance().getResourceManager();
    private HashMap<ResourceLocation, IBTMaterial> programCache = new HashMap<>();


    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        for (BTMaterialEntry entry : BoneTownRegistry.MATERIAL_REGISTRY.getValues()) {
            loadMaterial(entry);
        }
        this.manager = resourceManager;
    }

    private void clearProgramCache() {
        programCache.values().forEach(ProgramManager::releaseProgram);
        programCache.clear();
    }

    public OptionalInt getProgramId(ResourceLocation location) {
        IBTMaterial prog = programCache.get(location);
        return prog == null ? OptionalInt.empty() : OptionalInt.of(prog.getId());
    }


    public IBTMaterial getShaderProgram(ResourceLocation location) {
        return programCache.get(location);
    }


    private void loadMaterial(BTMaterialEntry program) {
        try {
            final var vert = createShaderProgram(manager, program.getVertexShader(), Program.Type.VERTEX);
            final var frag = createShaderProgram(manager, program.getFragShader(), Program.Type.FRAGMENT);

            final int btProgramID = ProgramManager.createProgram();
            final var btProgram = program.getMaterial(btProgramID, vert, frag);

            ProgramManager.linkShader(btProgram);
            btProgram.setupUniforms();
            programCache.put(program.getRegistryName(), btProgram);
        } catch (IOException ex) {
            BoneTown.LOGGER.error("Failed to load program {}",
                    program.getRegistryName().toString(), ex);
        }
    }

    private static Program createShaderProgram(final ResourceManager manager, ResourceLocation location,
                                               Program.Type shaderType) throws IOException {
        BoneTown.LOGGER.info("Trying to create shader: {}", location.toString());

        try (var resource = manager.getResource(location)) {
            return Program.compileShader(
                    shaderType,
                    location.toString(),
                    resource.getInputStream(),
                    location.toString(),
                    new GlslPreprocessor() {
                        @Nullable
                        @Override
                        public String applyImport(boolean p_166480_, @NotNull String path) {
                            return null;
                        }
                    }
            );
        }
    }

}
