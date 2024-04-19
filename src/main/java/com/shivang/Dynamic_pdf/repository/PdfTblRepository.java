package com.shivang.Dynamic_pdf.repository;

import com.shivang.Dynamic_pdf.entity.PdfTbl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfTblRepository extends JpaRepository<PdfTbl, String> {

}
