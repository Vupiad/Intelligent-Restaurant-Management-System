package com.hcmut.irms.ordering_service.domain.exception;

import java.util.List;

public class MenuItemUnavailableException extends RuntimeException {
    public MenuItemUnavailableException(List<String> unavailableIds) {
        super("The following menu items are unavailable: " + String.join(", ", unavailableIds));
    }
}
