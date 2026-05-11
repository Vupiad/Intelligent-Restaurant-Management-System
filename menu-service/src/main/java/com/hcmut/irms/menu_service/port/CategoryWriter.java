package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.Category;

public interface CategoryWriter {
    Category save(Category category);

    void delete(Category category);
}
