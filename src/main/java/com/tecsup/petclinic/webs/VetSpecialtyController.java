package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.repositories.VetSpecialtyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vet-specialties")
public class VetSpecialtyController {

    private final VetSpecialtyRepository vetSpecialtyRepository;

    public VetSpecialtyController(VetSpecialtyRepository vetSpecialtyRepository) {
        this.vetSpecialtyRepository = vetSpecialtyRepository;
    }

    @GetMapping
    public ResponseEntity<?> findAllVetSpecialties() {
        return ResponseEntity.ok(
                vetSpecialtyRepository.findAll()
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findVetSpecialtyById(@PathVariable Integer id) {
        return vetSpecialtyRepository.findById(id)
                .map(specialty -> ResponseEntity.ok(toResponse(specialty)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createVetSpecialty(@RequestBody Specialty specialty) {
        Specialty savedSpecialty = vetSpecialtyRepository.save(specialty);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedSpecialty));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVetSpecialty(@PathVariable Integer id, @RequestBody Specialty specialty) {
        return vetSpecialtyRepository.findById(id)
                .map(existingSpecialty -> {
                    existingSpecialty.setName(specialty.getName());
                    Specialty updatedSpecialty = vetSpecialtyRepository.save(existingSpecialty);
                    return ResponseEntity.ok(toResponse(updatedSpecialty));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVetSpecialty(@PathVariable Integer id) {
        return vetSpecialtyRepository.findById(id)
                .map(existingSpecialty -> {
                    vetSpecialtyRepository.delete(existingSpecialty);
                    return ResponseEntity.ok("Delete ID: " + id);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> toResponse(Specialty specialty) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", specialty.getId());
        response.put("name", specialty.getName());
        return response;
    }
}