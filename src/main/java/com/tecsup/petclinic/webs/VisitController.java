package com.tecsup.petclinic.webs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VisitController {

    @GetMapping("/visits")
    public ResponseEntity<String> findAllVisits() {
        return ResponseEntity.ok("Visits OK");
    }

}
