package com.shivang.Dynamic_pdf.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PDF_DATA")
@Getter
@Setter
@NoArgsConstructor
public class PdfTbl {
    @Id
    @Column(name = "DATA_HASH")
    private String dataHash;

    @Column(name = "LOCATION")
    private String location;
}
