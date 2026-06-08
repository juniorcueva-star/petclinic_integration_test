package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.entities.PetType;
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
public class TypesControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllTypes() throws Exception {
        mockMvc.perform(get("/types"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testFindTypeOK() throws Exception {
        mockMvc.perform(get("/types/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void testFindTypeKO() throws Exception {
        mockMvc.perform(get("/types/9999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateType() throws Exception {
        PetType petType = new PetType();
        petType.setName("hamster-test");

        mockMvc.perform(post("/types")
                        .content(om.writeValueAsString(petType))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("hamster-test")));
    }

    @Test
    public void testUpdateType() throws Exception {
        PetType petType = new PetType();
        petType.setName("rabbit-test");

        ResultActions result = mockMvc.perform(post("/types")
                        .content(om.writeValueAsString(petType))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = result.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        PetType updatedType = new PetType();
        updatedType.setName("rabbit-updated-test");

        mockMvc.perform(put("/types/" + id)
                        .content(om.writeValueAsString(updatedType))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", is("rabbit-updated-test")));
    }

    @Test
    public void testDeleteType() throws Exception {
        PetType petType = new PetType();
        petType.setName("bird-delete-test");

        ResultActions result = mockMvc.perform(post("/types")
                        .content(om.writeValueAsString(petType))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = result.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/types/" + id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteTypeKO() throws Exception {
        mockMvc.perform(delete("/types/9999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}