package com.chaosbuffalo.bonetown.core.materials;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.BoneTownRegistry;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.logging.LogUtils;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.OptionalInt;
import java.util.Set;


@OnlyIn(Dist.CLIENT)
public class MaterialResourceManager implements ResourceManagerReloadListener {

    static final Logger LOGGER = LogUtils.getLogger();
    public static final MaterialResourceManager INSTANCE = new MaterialResourceManager();

    private ResourceManager manager;
    private HashMap<ResourceLocation, IBTMaterial> programCache = new HashMap<>();


    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        for (BTMaterialEntry entry : BoneTownRegistry.MATERIAL_REGISTRY.getValues()){
            loadProgram(entry);
        }
    }

    private void clearProgramCache(){
        programCache.values().forEach(ProgramManager::releaseProgram);
        programCache.clear();
    }

    public OptionalInt getProgramId(ResourceLocation location) {
        IBTMaterial prog = programCache.get(location);
        return prog == null ? OptionalInt.empty() : OptionalInt.of(prog.getId());
    }


    public IBTMaterial getShaderProgram(ResourceLocation location){
        return programCache.get(location);
    }


    private void loadProgram(BTMaterialEntry program){
        try {
            final var vert = createShader(manager, program.getVertexShader(), Program.Type.VERTEX);
            final var frag = createShader(manager, program.getFragShader(), Program.Type.FRAGMENT);
            int progId = ProgramManager.createProgram();
            IBTMaterial prog = program.getProgram(progId, vert, frag);
            ProgramManager.linkShader(prog);
            prog.setupUniforms();
            programCache.put(program.getRegistryName(), prog);
        } catch (IOException ex) {
            BoneTown.LOGGER.error("Failed to load program {}",
                    program.getRegistryName().toString(), ex);
        }
    }

    private static Program createShader(final ResourceManager manager, ResourceLocation location,
                                             Program.Type shaderType) throws IOException {

        final var fullResourcePath= FileUtil.getFullResourcePath("shaders/core/" + location.getPath() + shaderType.getExtension());

        BoneTown.LOGGER.info("Trying to create shader: {}", location.toString());
        try (InputStream is = new BufferedInputStream(manager.getResource(location).getInputStream())) {
            return Program.compileShader(shaderType, location.toString(), is, location.toString(), new GlslPreprocessor() {
                private final Set<String> importedPaths = Sets.newHashSet();
                public String applyImport(boolean p_173374_, @NotNull String path) {
                    path = FileUtil.normalizeResourcePath((p_173374_ ? fullResourcePath : "shaders/include/") + path);
                    if (!this.importedPaths.add(path)) {
                        return null;
                    } else {
                        final var resourcelocation1 = new ResourceLocation(path);

                        try {
                            Resource resource1 = manager.getResource(resourcelocation1);

                            String s2;
                            try {
                                s2 = IOUtils.toString(resource1.getInputStream(), StandardCharsets.UTF_8);
                            } catch (Throwable throwable1) {
                                if (resource1 != null) {
                                    try {
                                        resource1.close();
                                    } catch (Throwable throwable) {
                                        throwable1.addSuppressed(throwable);
                                    }
                                }

                                throw throwable1;
                            }

                            if (resource1 != null) {
                                resource1.close();
                            }

                            return s2;
                        } catch (IOException ioexception) {
                            LOGGER.error("Could not open GLSL import {}: {}", path, ioexception.getMessage());
                            return "#error " + ioexception.getMessage();
                        }
                    }
                }
            });
        }
    }

}
