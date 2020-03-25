package com.charsharing.bootcamp.page.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Comment {
    private String commentId;
    private String blockId;
    private String content;
    private String creator;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
    private Date createdAt;
}
