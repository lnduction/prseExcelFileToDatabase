package com.app.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface ParseExcelService {

    List<List<String>> parseExcelFile(MultipartFile file);
}
