package com.hcmut.irms.ordering_service.adapter.external;

import com.hcmut.irms.ordering_service.dto.external.MenuItemResponse;
import com.hcmut.irms.ordering_service.port.MenuAvailabilityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Calls the menu-service REST API to verify item availability.
 * The caller's JWT token is forwarded so menu-service can authorize the request normally.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuServiceAdapter implements MenuAvailabilityPort {

    private final RestClient menuServiceRestClient;

    @Override
    public List<String> findUnavailableItemIds(List<String> menuItemIds, String bearerToken) {
        List<String> unavailable = new ArrayList<>();

        for (String itemId : menuItemIds) {
            try {
                MenuItemResponse response = menuServiceRestClient.get()
                        .uri("/api/menu/{itemId}/availability", itemId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .retrieve()
                        .body(MenuItemResponse.class);

                if (response == null || !response.isAvailable()) {
                    log.warn("Menu item {} is not available", itemId);
                    unavailable.add(itemId);
                }
            } catch (RestClientResponseException ex) {
                // 404 = item not found → treat as unavailable
                log.warn("Menu item {} returned HTTP {}: {}", itemId, ex.getStatusCode(), ex.getMessage());
                unavailable.add(itemId);
            } catch (Exception ex) {
                // Network or parsing error → fail safe: treat as unavailable
                log.error("Failed to check availability for item {}: {}", itemId, ex.getMessage());
                unavailable.add(itemId);
            }
        }

        return unavailable;
    }
}
