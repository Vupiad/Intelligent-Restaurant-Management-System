package com.hcmut.irms.menu_service.repository;

import com.hcmut.irms.menu_service.model.Category;
import com.hcmut.irms.menu_service.port.CategoryExistenceChecker;
import com.hcmut.irms.menu_service.port.CategoryNameChecker;
import com.hcmut.irms.menu_service.port.CategoryReader;
import com.hcmut.irms.menu_service.port.CategoryReferenceProvider;
import com.hcmut.irms.menu_service.port.CategoryWriter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>,
        CategoryReader,
        CategoryExistenceChecker,
        CategoryNameChecker,
        CategoryReferenceProvider,
        CategoryWriter {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
