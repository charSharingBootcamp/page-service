package com.charsharing.bootcamp.page.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

// Creating entity model document
@Data
@AllArgsConstructor
public class Document {
    private String title;
    private String creator;
    private Date createdAt;
    private Date updatedAt;
    private List<Tab> content;
    private boolean isArchived;

}
