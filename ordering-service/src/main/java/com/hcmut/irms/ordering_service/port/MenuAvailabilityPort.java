package com.hcmut.irms.ordering_service.port;

import java.util.List;

public interface MenuAvailabilityPort {
    /**
     * Returns the IDs of menu items that are currently NOT available.
     *
     * @param menuItemIds list of menu item UUIDs (as Strings) to check
     * @param bearerToken JWT token to pass to the menu-service (Authorization header)
     * @return list of unavailable item IDs (empty if all available)
     */
    List<String> findUnavailableItemIds(List<String> menuItemIds, String bearerToken);
}
