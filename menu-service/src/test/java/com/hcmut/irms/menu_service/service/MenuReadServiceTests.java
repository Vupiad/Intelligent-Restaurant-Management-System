package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.MenuItemAvailabilityResponseDTO;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuReadServiceTests {
    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private PriceCalculationService priceCalculationService;

    @Test
    void getItemAvailabilityReturnsAvailabilityFlag() {
        UUID itemId = UUID.randomUUID();
        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setAvailable(true);
        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        MenuReadService service = new MenuReadService(menuItemRepository, priceCalculationService);
        MenuItemAvailabilityResponseDTO response = service.getItemAvailability(itemId);

        assertThat(response.getItemId()).isEqualTo(itemId);
        assertThat(response.isAvailableForOrder()).isTrue();
    }

    @Test
    void getItemAvailabilityThrowsNotFoundWhenMissing() {
        UUID itemId = UUID.randomUUID();
        when(menuItemRepository.findById(itemId)).thenReturn(Optional.empty());

        MenuReadService service = new MenuReadService(menuItemRepository, priceCalculationService);

        assertThatThrownBy(() -> service.getItemAvailability(itemId))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
