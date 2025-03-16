package org.excellent.client.managers.command.api.logger;


import org.excellent.client.managers.command.api.Logger;

public class ConsoleLogger implements Logger {
    @Override
    public void log(String message) {
        System.out.println("message = " + message);
    }
}
