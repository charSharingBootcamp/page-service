package com.charsharing.bootcamp.page.domain;

import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

@Data
public class PageModel {
    private String title;
    private String creator;
    private Date createdAt;
    private Date updatedAt;
    private String content;
    private boolean isArchived;
    private String qrCode;
}
