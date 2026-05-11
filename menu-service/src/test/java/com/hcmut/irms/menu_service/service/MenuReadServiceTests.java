package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.application.MenuItemAvailabilityView;
import com.hcmut.irms.menu_service.exception.MenuNotFoundException;
import com.hcmut.irms.menu_service.mapper.MenuItemMapper;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.port.MenuItemPromotionReader;
import com.hcmut.irms.menu_service.port.MenuItemReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuReadServiceTests {
    @Mock
    private MenuItemReader menuItemReader;

    @Mock
    private MenuItemPromotionReader menuItemPromotionReader;

    @Mock
    private PriceCalculationService priceCalculationService;

    @Test
    void getItemAvailabilityReturnsAvailabilityFlag() {
        UUID itemId = UUID.randomUUID();
        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setAvailable(true);
        when(menuItemReader.findById(itemId)).thenReturn(Optional.of(item));

        MenuReadService service = new MenuReadService(
                menuItemPromotionReader,
                menuItemReader,
                new MenuItemMapper(priceCalculationService)
        );
        MenuItemAvailabilityView response = service.getItemAvailability(itemId);

        assertThat(response.itemId()).isEqualTo(itemId);
        assertThat(response.availableForOrder()).isTrue();
    }

    @Test
    void getItemAvailabilityThrowsNotFoundWhenMissing() {
        UUID itemId = UUID.randomUUID();
        when(menuItemReader.findById(itemId)).thenReturn(Optional.empty());

        MenuReadService service = new MenuReadService(
                menuItemPromotionReader,
                menuItemReader,
                new MenuItemMapper(priceCalculationService)
        );

        assertThatThrownBy(() -> service.getItemAvailability(itemId))
                .isInstanceOf(MenuNotFoundException.class)
                .hasMessage("Menu item not found: " + itemId);
    }
}
