package com.charsharing.bootcamp.page.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Block {

    private String id;

    private String content;

    private String creator;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
    private Date createdAt;

    private boolean code;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
    private Date updatedAt;

    private String updatedBy;
}
