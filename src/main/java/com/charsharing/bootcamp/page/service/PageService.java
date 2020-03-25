package com.charsharing.bootcamp.page.service;

import com.charsharing.bootcamp.page.domain.Block;
import com.charsharing.bootcamp.page.domain.BlockUpdateRequest;
import com.charsharing.bootcamp.page.domain.Document;
import com.charsharing.bootcamp.page.domain.Tab;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.List;

//import lombok.Value;

@Slf4j
@Service
public class PageService {

    private RestTemplateBuilder templateBuilder;
    @Value("${charSharing.document.service.url}")
    private String documentServiceURL;
    private boolean localTest;

    public PageService(RestTemplateBuilder templateBuilder, Environment environment) {
        localTest = environment.acceptsProfiles("localTest");
        this.templateBuilder = templateBuilder;
    }


    public Document findDocumentByTitle(String title) {
        if (localTest) {
            Document test = new Document("foo", "bar", new Date(), null, List.of(new Tab()), false);
            return test;
        }

        try {
            ResponseEntity<Document> responseEntity = templateBuilder.build().getForEntity(documentServiceURL + "documents/" + title, Document.class);
            if (responseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                return null;
            } else {
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    public Document updateDocumentText(String title, int tabIndex, String text, String name, boolean code) {
        ResponseEntity<Document> responseEntity = templateBuilder.build()
                .exchange(documentServiceURL + "documents/" + title + "/" + tabIndex + "?name=" + name + "&code=" + code, HttpMethod.PUT, new HttpEntity<>(text), Document.class);
        return responseEntity.getBody();
    }

    public ResponseEntity<String> updateBlock(BlockUpdateRequest updateRequest, String title, int tabIndex, String blockId) {
        //error if no username is set
        if (updateRequest.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body("Username is missing");
        }
        String url = String.format("%s%s/%s/%s/%s", documentServiceURL, "documents", title, tabIndex, blockId);

        try {
            templateBuilder.build()
                    .exchange(url, HttpMethod.PUT, new HttpEntity<>(updateRequest), Block.class);
        } catch (Exception e) {
            return ResponseEntity.ok("No changes done");
        }
        return ResponseEntity.ok("Updated text");

    }

    public ResponseEntity<String> addTab(String tabName, String title) {
        Tab tab = new Tab();
        tab.setTitle(tabName);
        try {
            templateBuilder.build()
                    .exchange(documentServiceURL + "documents/" + title, HttpMethod.POST, new HttpEntity<>(tab), Tab.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("created");
    }
}
