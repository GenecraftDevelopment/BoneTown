package com.chaosbuffalo.bonetown.core.model;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.BoneTownConstants;
import com.chaosbuffalo.bonetown.core.BoneTownRegistry;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFArmorModel;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFModelLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.io.IOUtils;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class BTArmorModelEntry implements IForgeRegistryEntry<BTArmorModelEntry> {
    private ResourceLocation name;
    public ResourceLocation modelName;
    public ResourceLocation modelFile;
    private BoneTownConstants.MeshTypes meshType;
    private final List<String> headMeshes;
    private final List<String> bodyMeshes;
    private final List<String> legMeshes;
    private final List<String> feetMeshes;
    private final ArmorMaterial material;

    public BTArmorModelEntry(ResourceLocation name, ResourceLocation modelName,
                             ResourceLocation modelFile,
                             List<String> headMeshes, List<String> bodyMeshes,
                             List<String> legMeshes, List<String> feetMeshes){
        this(name, modelName, modelFile, headMeshes, bodyMeshes, legMeshes, feetMeshes, null);
    }

    public BTArmorModelEntry(ResourceLocation name, ResourceLocation modelName,
                             ResourceLocation modelFile,
                             List<String> headMeshes, List<String> bodyMeshes,
                             List<String> legMeshes, List<String> feetMeshes,
                             ArmorMaterial material){
        this.name = name;
        this.headMeshes = headMeshes;
        this.bodyMeshes = bodyMeshes;
        this.legMeshes = legMeshes;
        this.feetMeshes = feetMeshes;
        this.modelName = modelName;
        this.modelFile = modelFile;
        this.material = material;
        this.meshType = BoneTownConstants.MeshTypes.BONEMF;
    }

    @Override
    public BTArmorModelEntry setRegistryName(ResourceLocation name) {
        this.name = name;
        return this;
    }

    public void load(){
        String meshExt = BoneTownConstants.stringFromMeshType(meshType);
        ResourceLocation name = getRegistryName();
        ResourceLocation animLocation = new ResourceLocation(modelFile.getNamespace(),
                BoneTownConstants.BONETOWN_MODELS_DIR +
                        "/" + modelFile.getPath() + "." + meshExt);
        BTModel model = BoneTownRegistry.MODEL_REGISTRY.getValue(this.modelName);
        if (model instanceof BTAnimatedModel){
            try {
                InputStream stream = Minecraft.getInstance().getResourceManager()
                        .getResource(animLocation)
                        .getInputStream();
                byte[] _data = IOUtils.toByteArray(stream);
                ByteBuffer data = MemoryUtil.memCalloc(_data.length + 1);
                data.put(_data);
                data.put((byte) 0);
                data.flip();
                stream.close();
                try {
                    BoneMFArmorModel armorModel = BoneMFModelLoader.loadArmor(data, name, headMeshes,
                            bodyMeshes, legMeshes, feetMeshes);
                    if (material == null){
                        ((BTAnimatedModel) model).addDefaultArmor(armorModel);
                    } else {
                        ((BTAnimatedModel) model).addArmorOverride(material, armorModel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MemoryUtil.memFree(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            BoneTown.LOGGER.error("Trying to load {} armor models for animated model: {}, " +
                    "but model wasn't found or it is not animated", getRegistryName(), modelName);
        }


    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public Class<BTArmorModelEntry> getRegistryType() {
        return BTArmorModelEntry.class;
    }
}


