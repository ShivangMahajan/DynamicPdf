package com.shivang.Dynamic_pdf.services;

import com.shivang.Dynamic_pdf.dto.InputDto;
import com.shivang.Dynamic_pdf.entity.PdfTbl;
import com.shivang.Dynamic_pdf.repository.PdfTblRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService{

    private PdfTblRepository pdfTblRepository;
    @Autowired
    public PdfGeneratorServiceImpl(PdfTblRepository pdfTblRepository){
        this.pdfTblRepository = pdfTblRepository;
    }

    Logger logger = LoggerFactory.getLogger(PdfGeneratorServiceImpl.class);
    public ResponseEntity pdfGenerate(InputDto inputDto) {
        logger.info("pdfGenerate started");
        try {

            formatInputDto(inputDto);

            if (inputDto.getItems() != null && !inputDto.getItems().isEmpty()) {
                inputDto.getItems().forEach(item -> {
                    if (item.getRate() != null) {
                        item.setRate(item.getRate().setScale(2, RoundingMode.HALF_UP));
                    }
                    if (item.getAmount() != null) {
                        item.setAmount(item.getAmount().setScale(2, RoundingMode.HALF_UP));
                    }
                });
            }
            String hash = getHash(inputDto);
            logger.info("checking in DB");
            Path path = null;
            Optional<PdfTbl> pdfTblOptional = pdfTblRepository.findById(hash);
            if(pdfTblOptional.isPresent()){
                logger.info("found the file in DB");
               Path p = Path.of(pdfTblOptional.get().getLocation());
               if(new UrlResource(p.toUri()).exists()){
                   logger.info("file exists");
                   path = p;
               }
            }

            if(path == null) {
                logger.info("creating new file");
                String html = parseThymeleafTemplate(inputDto);
                String fileName = inputDto.getSeller() + "_" + inputDto.getBuyer();
                if(fileName.length() < 3){
                    fileName = "newFile";
                }
                String fileUrl = generatePdfFromHtml(html, fileName);
                path = Paths.get(System.getProperty("java.io.tmpdir") + "/" + fileUrl);

                PdfTbl pdfTbl = new PdfTbl();
                pdfTbl.setDataHash(hash);
                pdfTbl.setLocation(path.toString());

                pdfTblRepository.saveAndFlush(pdfTbl);
            }
            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error in pdfGenerate " + e);
        }
        return ResponseEntity.badRequest().body(null);
    }

    public void formatInputDto(InputDto inputDto) {
        if(inputDto.getSeller() == null){
            inputDto.setSeller("");
        }
        if(inputDto.getSellerAddress() == null){
            inputDto.setSellerAddress("");
        }
        if(inputDto.getSellerGstin() == null){
            inputDto.setSellerGstin("");
        }
        if(inputDto.getBuyer() == null){
            inputDto.setBuyer("");
        }
        if(inputDto.getBuyerAddress() == null){
            inputDto.setBuyerAddress("");
        }
        if(inputDto.getBuyerGstin() == null){
            inputDto.setBuyerGstin("");
        }
        if(inputDto.getItems() == null){
            inputDto.setItems(new ArrayList<>());
        }
    }

    private String parseThymeleafTemplate(InputDto inputDto) {
        logger.info("parseThymeleafTemplate started");
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("seller", inputDto.getSeller());
        context.setVariable("sellerAddress", inputDto.getSellerAddress());
        context.setVariable("sellerGstin", "GSTIN: " + inputDto.getSellerGstin());
        context.setVariable("buyer", inputDto.getBuyer());
        context.setVariable("buyerAddress", inputDto.getBuyerAddress());
        context.setVariable("buyerGstin", "GSTIN: " + inputDto.getBuyerGstin());
        context.setVariable("itemList", inputDto.getItems());

        return templateEngine.process("pdfTemplate", context);
    }

    public String generatePdfFromHtml(String html, String name) throws IOException {
        logger.info("generatePdfFromHtml started");
        File file = File.createTempFile(name, ".pdf");
        OutputStream outputStream = new FileOutputStream(file);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
        return file.getName();
    }

    public String getHash(InputDto inputDto) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append(inputDto.getSeller().hashCode())
                .append(inputDto.getSellerAddress().hashCode())
                .append(inputDto.getSellerGstin().hashCode())
                .append(inputDto.getBuyer().hashCode())
                .append(inputDto.getBuyerAddress().hashCode())
                .append(inputDto.getBuyerGstin().hashCode());

        if(inputDto.getItems() != null) {
            inputDto.getItems().forEach(item -> {
                stringBuilder.append(item.getName().hashCode())
                        .append(item.getQuantity().hashCode())
                        .append(item.getRate().hashCode())
                        .append(item.getAmount().hashCode());
            });
        }

        StringBuilder finalString;
        if(stringBuilder.toString().length() > 240){
            finalString = new StringBuilder(String.valueOf(stringBuilder.toString().hashCode()));
        } else{
            finalString = stringBuilder;
        }

        return finalString.toString();
    }

}
