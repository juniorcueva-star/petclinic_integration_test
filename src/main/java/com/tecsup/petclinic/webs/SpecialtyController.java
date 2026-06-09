package com.tecsup.petclinic.webs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpecialtyController {

    @GetMapping("/specialties")
    public ResponseEntity<String> findAllSpecialties() {
        return ResponseEntity.ok("Specialties OK");
    }

}
