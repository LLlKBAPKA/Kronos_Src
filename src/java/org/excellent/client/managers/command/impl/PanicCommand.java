package org.excellent.client.managers.command.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.text.TextFormatting;
import org.excellent.client.Excellent;
import org.excellent.client.api.interfaces.IMinecraft;
import org.excellent.client.managers.command.api.Command;
import org.excellent.client.managers.command.api.Logger;
import org.excellent.client.managers.command.api.MultiNamedCommand;
import org.excellent.client.managers.command.api.Parameters;
import org.excellent.client.managers.module.Module;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PanicCommand implements Command, MultiNamedCommand, IMinecraft {
    final Logger logger;

    @Override
    public void execute(Parameters parameters) {
        int disabledModulesCount = 0;
        for (Module module : Excellent.inst().moduleManager().values()) {
            if (!module.isAutoEnabled() && module.isAllowDisable() && module.isEnabled()) {
                module.setEnabled(false, false);
                disabledModulesCount++;
            }
        }
        logger.log(TextFormatting.GRAY + "Все функции клиента (" + disabledModulesCount + " шт) были выключены.");
    }

    @Override
    public String name() {
        return "panic";
    }

    @Override
    public String description() {
        return "Выключает все функции клиента.";
    }

    @Override
    public List<String> aliases() {
        return List.of("p");
    }
}
