package com.shivang.Dynamic_pdf;

import com.shivang.Dynamic_pdf.dto.InputDto;
import com.shivang.Dynamic_pdf.dto.ItemsDTO;
import com.shivang.Dynamic_pdf.repository.PdfTblRepository;
import com.shivang.Dynamic_pdf.services.PdfGeneratorService;
import com.shivang.Dynamic_pdf.services.PdfGeneratorServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class PdfGeneratorServiceImplTest {

    @Mock
    PdfTblRepository pdfTblRepository;

    PdfGeneratorService pdfGeneratorService;

    @Before
    public void init(){
        pdfGeneratorService = new PdfGeneratorServiceImpl(pdfTblRepository);
    }

    @Test
    public void pdfGenerateTest(){
        InputDto inputDto = new InputDto();
        inputDto.setSeller("seller");
        inputDto.setSellerAddress("sellerAddress");
        inputDto.setSellerGstin("1234");
        inputDto.setBuyer("buyer");
        inputDto.setBuyerAddress("buyerAddress");
        inputDto.setBuyerGstin("1234");

        ItemsDTO itemsDTO = new ItemsDTO();
        itemsDTO.setName("Product1");
        itemsDTO.setQuantity("12");
        itemsDTO.setRate(new BigDecimal("212.00"));
        itemsDTO.setAmount(new BigDecimal("312.00"));

        inputDto.setItems(Arrays.asList(itemsDTO));


        ResponseEntity responseEntity = pdfGeneratorService.pdfGenerate(inputDto);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

}
