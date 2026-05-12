package com.hcmut.irms.menu_service.repository;

import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.port.MenuItemBulkReader;
import com.hcmut.irms.menu_service.port.MenuItemCategoryUsageChecker;
import com.hcmut.irms.menu_service.port.MenuItemPromotionReader;
import com.hcmut.irms.menu_service.port.MenuItemReader;
import com.hcmut.irms.menu_service.port.MenuItemWriter;
import com.hcmut.irms.menu_service.port.PromotionMenuItemLinkReader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID>,
        MenuItemReader,
        MenuItemPromotionReader,
        MenuItemBulkReader,
        MenuItemCategoryUsageChecker,
        PromotionMenuItemLinkReader,
        MenuItemWriter {
    @Query("""
            select distinct m
            from MenuItem m
            left join fetch m.promotions
            """)
    List<MenuItem> findAllWithPromotions();

    @Query("""
            select distinct m
            from MenuItem m
            left join fetch m.promotions
            where m.isAvailable = true
            """)
    List<MenuItem> findAvailableWithPromotions();

    @Query("""
            select distinct m
            from MenuItem m
            left join fetch m.promotions
            where m.id = :id
            """)
    java.util.Optional<MenuItem> findByIdWithPromotions(UUID id);

    List<MenuItem> findByPromotions_Id(UUID promotionId);

    boolean existsByCategory_Id(UUID categoryId);
}
