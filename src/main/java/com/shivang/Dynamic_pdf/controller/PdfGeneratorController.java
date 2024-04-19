package com.shivang.Dynamic_pdf.controller;

import com.shivang.Dynamic_pdf.dto.InputDto;
import com.shivang.Dynamic_pdf.services.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("generator/")
public class PdfGeneratorController {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @PostMapping("generate")
    public ResponseEntity generate(@RequestBody InputDto inputDto) {
         return pdfGeneratorService.pdfGenerate(inputDto);
    }
}
