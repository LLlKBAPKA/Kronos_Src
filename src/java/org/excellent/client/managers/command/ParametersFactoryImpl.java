package org.excellent.client.managers.command;

import org.excellent.client.managers.command.api.Parameters;
import org.excellent.client.managers.command.api.ParametersFactory;

public class ParametersFactoryImpl implements ParametersFactory {

    @Override
    public Parameters createParameters(String message, String delimiter) {
        return new ParametersImpl(message.split(delimiter));
    }
}
