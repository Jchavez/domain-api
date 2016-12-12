package com.baeldung.example;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.baeldung.example.model.Domain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DomainApiApplication.class)
@WebAppConfiguration
public class DomainApiApplicationTests {

    @Rule
    public final RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    public void shouldGetDomain() throws Exception {
        mockMvc.perform(get("/domains/example.com/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("domain-get-success",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("name").description("The domain name"),
                                fieldWithPath("tld").description("The tld of the domain"),
                                fieldWithPath("expirationDate").description("The date on which the domain will expire"),
                                fieldWithPath("autorenewal").description("The flag that tells if the domain will renew at the expiration date or not"))));
    }

    @Test
    public void shouldGetDomainNotFound() throws Exception {
        mockMvc.perform(get("/domains/notfounddomain.com/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("domain-get"));
    }

    @Test
    public void shouldUpdateDomain() throws Exception {
        mockMvc.perform(put("/domains/example.com/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(existingDomainAsString())).andExpect(status().isOk())
                .andDo(document("domain-put",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("name").description("The domain name"),
                                fieldWithPath("tld").description("The tld of the domain"),
                                fieldWithPath("expirationDate").description("The date on which the domain will expire"),
                                fieldWithPath("autorenewal").description("The flag that tells if the domain will renew at the expiration date or not"))));
    }

    @Test
    public void shouldUpdateDomainNotFound() throws Exception {
        mockMvc.perform(put("/domains/notfounddomain.com/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(newDomainAsString()))
                .andExpect(status().isNotFound())
                .andDo(document("domain-put"));
    }

    @Test
    public void shouldUpdateDomainBadRequest() throws Exception {
        mockMvc.perform(put("/domains/example.com/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(newDomainAsString()))
                .andExpect(status().isBadRequest())
                .andDo(document("domain-put"));
    }

    @Test
    public void shouldCreateDomain() throws Exception {
        String content = newDomainAsString();
        mockMvc.perform(post("/domains")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isCreated())
                .andDo(document("domain-post"));
    }

    @Test
    public void shouldCreateDomainBadRequest() throws Exception {
        String content = domainWithNullDomainNameAsString();
        mockMvc.perform(post("/domains")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andDo(document("domain-post"));
    }

    @Test
    public void shouldCreateDomainConflict() throws Exception {
        String content = existingDomainAsString();
        mockMvc.perform(post("/domains")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isConflict())
                .andDo(document("domain-post"));
    }

    @Test
    public void shouldDeleteDomain() throws Exception {
        mockMvc.perform(delete("/domains/example.nyc/")).andExpect(status().isNoContent())
                .andDo(document("domain-delete"));
    }

    @Test
    public void shouldDeleteDomainNotFound() throws Exception {
        mockMvc.perform(delete("/domains/notfounddomain.com/")).andExpect(status().isNotFound())
                .andDo(document("domain-delete"));
    }

    private String domainWithNullDomainNameAsString() {
        return domainAsString(null);
    }

    private String existingDomainAsString() {
        return domainAsString("example.com");
    }

    private String newDomainAsString() {
        return domainAsString("thisisanewdomain.com");
    }

    private String domainAsString(String domainName) {
        try {
            Domain domain = new Domain(domainName, "com", "2016-04-01", false);
            return objectMapper.writeValueAsString(domain);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create domain");
        }
    }
}
