package com.chaosbuffalo.bonetown.core.utils;

import com.chaosbuffalo.bonetown.BoneTown;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Jacob on 1/25/2020.
 */
public class Utils {

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static int[] listIntToArray(List<Integer> list) {
        int[] result = list.stream().mapToInt((Integer v) -> v).toArray();
        return result;
    }


    public static void readLightTextureData(){
        LightTexture tex = Minecraft.getInstance().gameRenderer.lightTexture();

        Field privateField = null;
        try {
            // todo Access Transformer
            privateField = LightTexture.class.getDeclaredField("lightPixels");
            privateField.setAccessible(true);
            try {
                NativeImage image = (NativeImage)privateField.get(tex);
                for (int x = 0; x < image.getWidth(); x++){
                    for (int y = 0; y < image.getHeight(); y++){
                        int color = image.getPixelRGBA(x, y);
                        int alpha = color >>> 24;
                        int red = color >>> 16 & 0xFF;
                        int green = color >>> 8 & 0xFF;
                        int blue = color & 0xFF;
                        BoneTown.LOGGER.info("Pixel {}, {} color: r: {} g: {} b: {} a: {}", x, y, red, green, blue, alpha);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
