package com.charsharing.bootcamp.page.service;

import com.charsharing.bootcamp.page.domain.Block;
import com.charsharing.bootcamp.page.domain.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

@Service
public class CommentService {

    @Value("${charSharing.document.service.url}")
    private String documentServiceURL;

    @Value("${charSharing.comment.service.url}")
    private String commentServiceURL;

    private RestTemplateBuilder templateBuilder;

    public CommentService(RestTemplateBuilder templateBuilder) {
        this.templateBuilder = templateBuilder;
    }

    public void getCommentPageInfo(String documentTitle, int tabIndex, String blockId, Model model) {
        String getBlockURL = String.format("%sdocuments/%s/%s/%s", documentServiceURL, documentTitle, tabIndex, blockId);
        String getCommentsURL = String.format("%scomments/%s", commentServiceURL, blockId);
        ResponseEntity<Block> blockResponseEntity = templateBuilder.build().getForEntity(getBlockURL, Block.class);
        ResponseEntity<Comment[]> commentsResponseEntity = templateBuilder.build().getForEntity(getCommentsURL, Comment[].class);

        Block block = blockResponseEntity.getBody();
        List<Comment> comments = commentsResponseEntity.getBody() == null ? List.of() : Arrays.asList(commentsResponseEntity.getBody());
        model.addAttribute("block", block);
        model.addAttribute("comments", comments);
    }

    public Comment addComment(Comment comment){
        ResponseEntity<Comment> exchange = templateBuilder.build().exchange(commentServiceURL + "comments", HttpMethod.POST, new HttpEntity<>(comment), Comment.class);
        return exchange.getStatusCode() == HttpStatus.OK? exchange.getBody():null;
    }
}
