package com.charsharing.bootcamp.page.controller;


import com.charsharing.bootcamp.page.domain.BlockUpdateRequest;
import com.charsharing.bootcamp.page.domain.Comment;
import com.charsharing.bootcamp.page.domain.CommentFormModel;
import com.charsharing.bootcamp.page.domain.Document;
import com.charsharing.bootcamp.page.domain.DocumentFormModel;
import com.charsharing.bootcamp.page.service.CommentService;
import com.charsharing.bootcamp.page.service.PageService;
import com.charsharing.bootcamp.page.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static java.util.Objects.isNull;

@Controller
@RequestMapping(path = "/")
public class PageController {

    @Autowired
    private PageService pageService;
    private QRCodeService qrCodeService;

    @Autowired
    private CommentService commentService;

    @Value("${charSharing.start.url}")
    private String startURL;

    @Value("${charSharing.page.service.url}")
    private String pageServiceURL;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    /**
     * gets the document for a given title
     *
     * @param title the title of the document
     * @param model used for data in html
     * @return the page html
     */
    @GetMapping(path = "/{title}")
    public String getDocumentByTitle(@PathVariable(required = false) String title, Model model) {
        final Document document = pageService.findDocumentByTitle(title);
        if (isNull(document)) {
            return "redirect:" + startURL;
            //return "redirect:https://java-bootcamp.de";
        }
        model.addAttribute("document", document);
        model.addAttribute("documentFormModel", new DocumentFormModel());
        model.addAttribute("creatorName", "");
        model.addAttribute("nameError", false);
        model.addAttribute("startURL", startURL);
        model.addAttribute("pageUrl", pageServiceURL);

        BufferedImage img = qrCodeService.generateQRCodeImage(pageServiceURL + title);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", new File("tmpImage.png"));
            byte[] imageBytes = Files.readAllBytes(Paths.get("tmpImage.png"));
            Base64.Encoder encoder = Base64.getEncoder();
            String encoding = "data:image/png;base64," + encoder.encodeToString(imageBytes);
            model.addAttribute("qrcode", encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Document";
    }


    /**
     * adds a textblock to a tab in a document
     *
     * @param title     the totle of the document
     * @param tabIndex  the index of the tab in the document
     * @param formModel Object containing information about the new textblock
     * @param model     used for data in html
     * @return the page html
     */
    @PostMapping(path = "/{title}/{tabIndex}")
    public String updateDocument(
            @PathVariable("title") String title,
            @PathVariable("tabIndex") int tabIndex,
            @ModelAttribute DocumentFormModel formModel,
            Model model) {
        Document document;
        boolean nameError = formModel.getUserName().isBlank();
        if (nameError) {
            document = pageService.findDocumentByTitle(title);
            model.addAttribute("documentFormModel", formModel);
        } else {
            document = pageService.updateDocumentText(title, tabIndex, formModel.getText(), formModel.getUserName(), formModel.isCode());
            formModel.setText("");
            model.addAttribute("documentFormModel", formModel);
        }
        model.addAttribute("document", document);
        model.addAttribute("nameError", nameError);
        model.addAttribute("pageUrl", pageServiceURL);
        model.addAttribute("startURL", startURL);

        model.addAttribute("qrcode", qrCodeService.generateQRCodeImage(pageServiceURL + title));

        return "Document";
    }

    /**
     * adds a comment to a given block
     *
     * @param blockId   the id of the block
     * @param tabIndex  the index of the tab in the document
     * @param titel     the title of the document
     * @param formModel Object containing information about the comment
     * @param model     used for data in html
     * @return the comment html
     */
    @PostMapping(path = "/{title}/{tabIndex}/{blockId}")
    public String addComment(
            @PathVariable("blockId") String blockId,
            @PathVariable("tabIndex") int tabIndex,
            @PathVariable("title") String documentTitle,
            @ModelAttribute CommentFormModel formModel, Model model) {
        Comment comment;
        boolean nameError = formModel.getUserName().isBlank();
        if (nameError) {

            model.addAttribute("commentFormModel", formModel);
            model.addAttribute("pageUrl", pageServiceURL);
        } else {
            comment = new Comment();
            comment.setContent(formModel.getText());
            comment.setCreator(formModel.getUserName());
            comment.setBlockId(blockId);
            commentService.addComment(comment);

            formModel.setText("");
            model.addAttribute("commentFormModel", formModel);
        }
        commentService.getCommentPageInfo(documentTitle, tabIndex, blockId, model);
        model.addAttribute("nameError", nameError);
        model.addAttribute("documentTitle", documentTitle);
        model.addAttribute("pageUrl", pageServiceURL);
        model.addAttribute("startURL", startURL);

        return "Comment";
    }

    /**
     * gets the comment page for a given block
     *
     * @param documentTitle the title of the document
     * @param tabIndex      the index of the tab in the document
     * @param blockId       the id of the block
     * @param model         used for data in html
     * @return the comment html
     */
    @GetMapping(path = "/{title}/{tabIndex}/{blockId}")
    public String getCommentPage(
            @PathVariable("title") String documentTitle,
            @PathVariable("tabIndex") int tabIndex,
            @PathVariable("blockId") String blockId,
            Model model) {
        commentService.getCommentPageInfo(documentTitle, tabIndex, blockId, model);
        model.addAttribute("commentFormModel", new CommentFormModel());
        model.addAttribute("documentTitle", documentTitle);
        model.addAttribute("pageUrl", pageServiceURL);
        model.addAttribute("startURL", startURL);
        return "Comment";
    }

    /**
     * edits the text in a block
     *
     * @param updateRequest object containing information about the edited block
     * @param blockId       the id of the edited block
     * @param tabIndex      the index of the tab in the document
     * @param title         the title of the document
     * @return a http response with a message in the body
     */
    @PutMapping(path = "/{title}/{tabIndex}/{blockId}")
    public ResponseEntity<String> updateBlock(
            @RequestBody BlockUpdateRequest updateRequest,
            @PathVariable("blockId") String blockId,
            @PathVariable("tabIndex") int tabIndex,
            @PathVariable("title") String title) {
        if (updateRequest.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body("Name is required");
        }
        return pageService.updateBlock(updateRequest, title, tabIndex, blockId);
    }

    /**
     * adds a tab to a document
     *
     * @param tabName the name of the new tab
     * @param title   the title of the document in which the tab is added
     * @return a http response with a message in the body
     */
    @PostMapping(path = "/{title}")
    public ResponseEntity<String> addTab(
            @RequestBody String tabName,
            @PathVariable("title") String title
    ) {
        return pageService.addTab(tabName, title);
    }


}

