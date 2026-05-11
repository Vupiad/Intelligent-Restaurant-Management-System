package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.MenuItem;

public interface MenuItemWriter {
    MenuItem save(MenuItem item);

    void delete(MenuItem item);
}
