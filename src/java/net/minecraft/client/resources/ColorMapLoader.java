package net.minecraft.client.resources;

import java.io.IOException;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ColorMapLoader {

    public static int[] loadColors(IResourceManager manager, ResourceLocation location) throws IOException {
        int[] aint;

        try (
                IResource iresource = manager.getResource(location);
                NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
        ) {
            aint = nativeimage.makePixelArray();
        }

        return aint;
    }
}
