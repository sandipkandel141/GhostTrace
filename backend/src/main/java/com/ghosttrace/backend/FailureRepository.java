package com.ghosttrace.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FailureRepository extends JpaRepository<FailureEntity, String> {
    List<FailureEntity> findByServiceNameIgnoreCaseOrderByOccurredAtDesc(String serviceName);
}
