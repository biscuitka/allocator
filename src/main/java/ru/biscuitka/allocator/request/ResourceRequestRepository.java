package ru.biscuitka.allocator.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long> {
    List<ResourceRequest> findAllByApplicationId(long applicationId);

    List<ResourceRequest> findAllByApplicationIdIn(List<Long> applicationIds);

    /**
     * Находит запрос от выбранной в жеребьевке заявки
     *
     * @param applicationId номер рандомной заявки
     * @return запрос ресурса
     */
    ResourceRequest findByApplicationId(long applicationId);


    @Query("SELECT r FROM ResourceRequest r " +
            "JOIN Application a ON a.id = r.application.id " +
            "WHERE a.applicant.id = :applicantId " +
            "AND r.resource.id = :resourceId")
    List<ResourceRequest> findAllByApplicantIdAndResourceId(long applicantId, long resourceId);

    List<ResourceRequest> findAllByResourceIdAndStatus(long resourceId, ResourceStatus status);
}
