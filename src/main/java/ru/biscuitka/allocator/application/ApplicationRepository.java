package ru.biscuitka.allocator.application;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.biscuitka.allocator.district.District;
import ru.biscuitka.allocator.resource.Resource;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("SELECT a FROM Application a WHERE a.status = :status " +
            "AND a.type = :type " +
            "ORDER BY a.created ASC LIMIT 1")
    Application findUnallocatedApplication(@Param("status") ApplicationStatus status, @Param("type") ApplicationType type);

    @Query("SELECT a FROM Application a " +
            "JOIN ResourceRequest r ON a.id = r.application.id " +
            "WHERE r.resource = :resource " +
            "AND r.district = :district " +
            "AND a.type = :type " +
            "AND a.status = :status")
    List<Application> findAllByResourceAndDistrictAndTypeAndStatus(@Param("resource") Resource resource,
                                                                   @Param("district") District district,
                                                                   @Param("type") ApplicationType type,
                                                                   @Param("status") ApplicationStatus status);


    @Query("SELECT a FROM Application a " +
            "WHERE ((:statuses) IS NULL OR a.status IN :statuses) " +
            "AND ((:type) IS NULL OR a.type = :type) " +
            "AND (a.created BETWEEN :rangeStart AND :rangeEnd)")
    List<Application> findAllByParams(@Param("statuses") List<ApplicationStatus> statuses,
                                      @Param("type") ApplicationType type,
                                      @Param("rangeStart") LocalDateTime rangeStart,
                                      @Param("rangeEnd") LocalDateTime rangeEnd,
                                      Pageable pageable);
}
