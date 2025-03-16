package org.excellent.client.managers.command.api;


import org.excellent.client.managers.command.AdviceCommand;

public interface AdviceCommandFactory {
    AdviceCommand adviceCommand(CommandProvider commandProvider);
}
