package com.charsharing.bootcamp.page.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class QRCodeService {
    public static BufferedImage generateQRCodeImage(String barcodeText){
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 400, 400);
        } catch (WriterException e) {
            e.printStackTrace();
            log.error("Fehler beim schreiben des QR Codes");
            return new BufferedImage(5,5, Image.SCALE_FAST);
        }

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
