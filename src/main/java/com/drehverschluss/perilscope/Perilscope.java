package com.drehverschluss.perilscope;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * Loader independent constants shared by all parts of the mod.
 */
public final class Perilscope {
    public static final String MOD_ID = "perilscope";
    public static final Logger LOGGER = LogUtils.getLogger();

    private Perilscope() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
