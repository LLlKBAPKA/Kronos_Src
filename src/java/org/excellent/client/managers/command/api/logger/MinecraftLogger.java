package org.excellent.client.managers.command.api.logger;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.excellent.client.managers.command.api.Logger;
import org.excellent.client.utils.chat.ChatUtil;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinecraftLogger implements Logger {
    @Override
    public void log(String message) {
        ChatUtil.addText(message);
    }
}
