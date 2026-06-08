package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.repositories.TypesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/types")
public class TypesController {

    private final TypesRepository typesRepository;

    public TypesController(TypesRepository typesRepository) {
        this.typesRepository = typesRepository;
    }

    @GetMapping
    public ResponseEntity<?> findAllTypes() {
        return ResponseEntity.ok(typesRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findTypeById(@PathVariable Integer id) {
        return typesRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PetType> createType(@RequestBody PetType petType) {
        PetType savedType = typesRepository.save(petType);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateType(@PathVariable Integer id, @RequestBody PetType petType) {
        return typesRepository.findById(id)
                .map(existingType -> {
                    existingType.setName(petType.getName());
                    PetType updatedType = typesRepository.save(existingType);
                    return ResponseEntity.ok(updatedType);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteType(@PathVariable Integer id) {
        return typesRepository.findById(id)
                .map(existingType -> {
                    typesRepository.delete(existingType);
                    return ResponseEntity.ok("Delete ID: " + id);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}