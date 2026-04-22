package com.hcmut.irms.menu_service.repository;

import com.hcmut.irms.menu_service.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {
    @Query("""
            select p
            from Promotion p
            where p.isActive = true
              and p.startTime <= CURRENT_TIMESTAMP
              and p.endTime >= CURRENT_TIMESTAMP
            """)
    List<Promotion> findActivePromotions();

    Optional<Promotion> findByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
