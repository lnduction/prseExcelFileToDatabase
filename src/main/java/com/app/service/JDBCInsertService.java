package com.app.service;

import com.app.Form.UpdateDataFromExcelForm;

import java.util.List;

public interface JDBCInsertService {

    boolean addLines(List<List<String>> parsedFile, String tableName);

}
