package com.ghosttrace.backend;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/failures")
public class FailureController {
    private final FailureService service;

    public FailureController(FailureService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FailureEntity create(@Valid @RequestBody CreateFailureRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<FailureEntity> all(@RequestParam(required = false) String serviceName) {
        return service.findAll(serviceName);
    }

    @GetMapping("/count")
    public Map<String, Long> count() {
        return Map.of("count", service.count());
    }

    @GetMapping("/{id}")
    public FailureEntity one(@PathVariable String id) {
        return service.findById(id);
    }
}
