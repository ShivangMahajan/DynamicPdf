package com.shivang.Dynamic_pdf.services;

import com.shivang.Dynamic_pdf.dto.InputDto;
import org.springframework.http.ResponseEntity;

public interface PdfGeneratorService {

    public ResponseEntity pdfGenerate(InputDto inputDto);

}
