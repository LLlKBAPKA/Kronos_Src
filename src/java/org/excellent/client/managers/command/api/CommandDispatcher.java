package org.excellent.client.managers.command.api;


import org.excellent.client.managers.command.DispatchResult;

public interface CommandDispatcher {
    DispatchResult dispatch(String command);
}
