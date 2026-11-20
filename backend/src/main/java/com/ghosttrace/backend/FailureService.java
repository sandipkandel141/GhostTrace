
package com.ghosttrace.backend;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class FailureService {
    private final FailureRepository repository;

    public FailureService(FailureRepository repository) {
        this.repository = repository;
    }

    public FailureEntity create(CreateFailureRequest request) {
        if (repository.existsById(request.failureId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Failure ID already exists"
            );
        }

        return repository.save(new FailureEntity(
                request.failureId(),
                request.serviceName(),
                request.errorMessage(),
                request.severity(),
                request.traceId()
        ));
    }

    public List<FailureEntity> findAll(String service) {
        if (service != null && !service.isBlank()) {
            return repository.findByServiceNameIgnoreCaseOrderByOccurredAtDesc(service);
        }
        return repository.findAll();
    }

    public FailureEntity findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Failure not found"
                ));
    }

    public long count() {
        return repository.count();
    }
}
