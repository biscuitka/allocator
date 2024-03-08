package ru.biscuitka.allocator.quota;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.biscuitka.allocator.district.District;
import ru.biscuitka.allocator.resource.Resource;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "resource_quotas")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_quota_id")
    Long id;

    @Column(name = "resource_quota_quantity")
    Long quantity;

    @Column(name = "resource_quota_start_date")
    LocalDateTime startDate;

    @Column(name = "resource_quota_end_date")
    LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    Resource resource;

    @ManyToOne
    @JoinColumn(name = "district_id")
    District district;
}
