package org.excellent.client.managers.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.excellent.client.managers.command.api.Prefix;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefixImpl implements Prefix {
    String prefix = ".";

    @Override
    public void set(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String get() {
        return prefix;
    }
}
