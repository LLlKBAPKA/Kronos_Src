package org.excellent.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.excellent.client.Excellent;
import org.excellent.client.managers.module.Category;
import org.excellent.client.managers.module.Module;
import org.excellent.client.managers.module.ModuleInfo;
import org.excellent.client.managers.module.settings.impl.StringSetting;
import org.excellent.client.utils.other.Instance;

import java.util.WeakHashMap;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NameProtect", category = Category.MISC)
public class NameProtect extends Module {

    private static final WeakHashMap<String, String> replaceCache = new WeakHashMap<>();

    public static NameProtect getInstance() {
        return Instance.get(NameProtect.class);
    }

    private final StringSetting customName = new StringSetting(this, "Введите желаемое имя", "protected")
            .onAction(() -> protectedName = customName().getValue());

    @Getter
    private String protectedName = customName.getValue();

    @Override
    public void toggle() {
        super.toggle();
        protectedName = customName.getValue();
        replaceCache.clear();
    }

    public static String getReplaced(String input) {
        if (!Excellent.initialized()) {
            return input;
        }

        NameProtect nameProtect = NameProtect.getInstance();
        if (!nameProtect.isEnabled()) {
            return input;
        }

        if (replaceCache.containsKey(input)) {
            return replaceCache.get(input);
        }

        String sessionName = mc.session.getProfile().getName();
        String protectedName = nameProtect.protectedName();

        StringBuilder replacedText = new StringBuilder(input.replace(sessionName, protectedName));

        Excellent.inst().friendManager().forEach(friend -> {
            int index = replacedText.indexOf(friend);
            if (index != -1) {
                replacedText.replace(index, index + friend.length(), protectedName);
            }
        });

        String result = replacedText.toString();
        replaceCache.put(input, result);

        return result;
    }

}
