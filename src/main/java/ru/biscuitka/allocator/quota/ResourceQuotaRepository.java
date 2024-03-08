package ru.biscuitka.allocator.quota;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceQuotaRepository extends JpaRepository<ResourceQuota, Long> {
    ResourceQuota findByResourceIdAndDistrictId(Long resourceId, Long districtId);
}
