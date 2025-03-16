package net.minecraft.util;

import org.excellent.client.api.client.Constants;

public class Namespaced extends ResourceLocation {
    public Namespaced(String location) {
        super(Constants.NAMESPACE + "/" + location);
    }
}
