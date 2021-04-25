package com.app.Form;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;

public class UpdateDataFromExcelForm {

    private String tableName;
    private MultipartFile excelFile;

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public MultipartFile getExcelFile() { return excelFile; }
    public void setExcelFile(MultipartFile excelFile) { this.excelFile = excelFile; }
}
