package net.minecraft.client.renderer.texture;

import net.minecraft.client.resources.ReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.stream.Stream;

public abstract class SpriteUploader extends ReloadListener<AtlasTexture.SheetData> implements AutoCloseable {
    private final AtlasTexture textureAtlas;
    private final String prefix;

    public SpriteUploader(TextureManager textureManagerIn, ResourceLocation atlasTextureLocation, String prefixIn) {
        this.prefix = prefixIn;
        this.textureAtlas = new AtlasTexture(atlasTextureLocation);
        textureManagerIn.loadTexture(this.textureAtlas.getTextureLocation(), this.textureAtlas);
    }

    protected abstract Stream<ResourceLocation> getResourceLocations();

    /**
     * Gets a sprite associated with the passed resource location.
     */
    protected TextureAtlasSprite getSprite(ResourceLocation locationIn) {
        return this.textureAtlas.getSprite(this.resolveLocation(locationIn));
    }

    private ResourceLocation resolveLocation(ResourceLocation locationIn) {
        return new ResourceLocation(locationIn.getNamespace(), this.prefix + "/" + locationIn.getPath());
    }

    /**
     * Performs any reloading that can be done off-thread, such as file IO
     */
    protected AtlasTexture.SheetData prepare(IResourceManager resourceManagerIn) {


        AtlasTexture.SheetData atlastexture$sheetdata = this.textureAtlas.stitch(resourceManagerIn, this.getResourceLocations().map(this::resolveLocation), 0);


        return atlastexture$sheetdata;
    }

    protected void apply(AtlasTexture.SheetData objectIn, IResourceManager resourceManagerIn) {


        this.textureAtlas.upload(objectIn);


    }

    public void close() {
        this.textureAtlas.clear();
    }
}
