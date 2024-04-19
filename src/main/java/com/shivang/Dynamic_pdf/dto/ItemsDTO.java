package com.shivang.Dynamic_pdf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ItemsDTO {
    private String name;
    private String quantity;
    private BigDecimal rate;
    private BigDecimal amount;

}
