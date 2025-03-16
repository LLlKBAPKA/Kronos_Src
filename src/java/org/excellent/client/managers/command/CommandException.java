package org.excellent.client.managers.command;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class CommandException extends RuntimeException {
    String message;
}
