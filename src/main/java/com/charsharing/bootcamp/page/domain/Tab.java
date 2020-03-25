package com.charsharing.bootcamp.page.domain;

import lombok.Data;

import java.util.List;

@Data
public class Tab {

    private List<Block> textBlocks;

    private String title;
}
