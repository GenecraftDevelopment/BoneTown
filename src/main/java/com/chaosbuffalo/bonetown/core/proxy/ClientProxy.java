package com.chaosbuffalo.bonetown.core.proxy;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.materials.MaterialResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerHandlers() {
        super.registerHandlers();
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager())
                .registerReloadListener(MaterialResourceManager.INSTANCE);
    }
}
