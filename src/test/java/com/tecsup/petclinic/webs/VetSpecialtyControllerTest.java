package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.entities.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class VetSpecialtyControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllVetSpecialties() throws Exception {
        mockMvc.perform(get("/vet-specialties"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testFindVetSpecialtyOK() throws Exception {
        mockMvc.perform(get("/vet-specialties/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void testFindVetSpecialtyKO() throws Exception {
        mockMvc.perform(get("/vet-specialties/9999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateVetSpecialty() throws Exception {
        Specialty specialty = new Specialty();
        specialty.setName("nutrition-test");

        mockMvc.perform(post("/vet-specialties")
                        .content(om.writeValueAsString(specialty))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("nutrition-test")));
    }

    @Test
    public void testUpdateVetSpecialty() throws Exception {
        Specialty specialty = new Specialty();
        specialty.setName("surgery-test");

        ResultActions result = mockMvc.perform(post("/vet-specialties")
                        .content(om.writeValueAsString(specialty))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = result.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        Specialty updatedSpecialty = new Specialty();
        updatedSpecialty.setName("surgery-updated-test");

        mockMvc.perform(put("/vet-specialties/" + id)
                        .content(om.writeValueAsString(updatedSpecialty))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", is("surgery-updated-test")));
    }

    @Test
    public void testDeleteVetSpecialty() throws Exception {
        Specialty specialty = new Specialty();
        specialty.setName("dental-delete-test");

        ResultActions result = mockMvc.perform(post("/vet-specialties")
                        .content(om.writeValueAsString(specialty))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = result.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/vet-specialties/" + id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteVetSpecialtyKO() throws Exception {
        mockMvc.perform(delete("/vet-specialties/9999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}