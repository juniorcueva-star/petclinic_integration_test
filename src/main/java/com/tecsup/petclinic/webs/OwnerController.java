package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping(value = "/owners")
    public ResponseEntity<List<OwnerDTO>> findAllOwners() {
        List<OwnerDTO> owners = ownerService.findAll();
        return ResponseEntity.ok(owners);
    }

    @GetMapping(value = "/owners/{id}")
    public ResponseEntity<OwnerDTO> findById(@PathVariable Long id) {
        try {
            OwnerDTO ownerDTO = ownerService.findById(id);
            return ResponseEntity.ok(ownerDTO);
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/owners")
    public ResponseEntity<OwnerDTO> create(@RequestBody OwnerDTO ownerDTO) {
        OwnerDTO newOwner = ownerService.create(ownerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOwner);
    }

    @PutMapping(value = "/owners/{id}")
    public ResponseEntity<OwnerDTO> update(@RequestBody OwnerDTO ownerDTO,
                                           @PathVariable Long id) {
        try {
            OwnerDTO updateOwner = ownerService.findById(id);
            updateOwner.setFirstName(ownerDTO.getFirstName());
            updateOwner.setLastName(ownerDTO.getLastName());
            updateOwner.setAddress(ownerDTO.getAddress());
            updateOwner.setCity(ownerDTO.getCity());
            updateOwner.setTelephone(ownerDTO.getTelephone());
            ownerService.update(updateOwner);
            return ResponseEntity.ok(updateOwner);
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/owners/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            ownerService.delete(id);
            return ResponseEntity.ok("Delete ID: " + id);
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}