package org.excellent.client.managers.command.api;

public interface CommandProvider {
    Command command(String alias);
}
