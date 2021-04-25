package com.app.service.Impl;

import com.app.service.ParseExcelService;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class ParseExcelServiceImplementation implements ParseExcelService {

    private final Logger logger = LoggerFactory.getLogger(ParseExcelServiceImplementation.class);
    private final int DEFAULT_NUMBER_OF_LIST_FOR_PARSING = 0;
    private final int MAX_LENGTH_OF_CELL_FOR_LOGGING = 15;

    @Override
    public List<List<String>> parseExcelFile(MultipartFile file) {
        List<List<String>> parsedTable = new ArrayList<>();
        try( OPCPackage opcPackage = OPCPackage.open(file.getInputStream())) {
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            XSSFSheet sheet = workbook.getSheetAt(DEFAULT_NUMBER_OF_LIST_FOR_PARSING);
            for (Row row : sheet) {
                Iterator<Cell> cellIterator = row.iterator();
                List<String> cellList = new ArrayList<>();
                while (cellIterator.hasNext())
                    cellList.add(cellIterator.next().toString());
                 parsedTable.add(cellList);
            } } catch (Exception e){
            logger.error("error in excel parser, " + e.getMessage());
            return Collections.emptyList();
        } loggTable(parsedTable);
        return parsedTable;
    }

    private void loggTable(List<List<String>> table){
        StringBuilder loggTable = new StringBuilder("\n");
        List<String> heads = table.get(0);
        loggDivLine(loggTable, heads.size());
        loggLine(loggTable, heads);
        loggDivLine(loggTable, heads.size());
        for (int i = 1; i < table.size(); i++) loggLine(loggTable, table.get(i));
        loggDivLine(loggTable, heads.size());
        loggTable.append(";");
        logger.info("Table was parsed like " + loggTable.toString().replace("\n;", ""));
    }

    private void loggDivLine(StringBuilder loggTable, int size) {
        loggTable.append("|");
        for (int i = 0; i < size * (MAX_LENGTH_OF_CELL_FOR_LOGGING + 1); i++)
            if (i % (MAX_LENGTH_OF_CELL_FOR_LOGGING + 1) == MAX_LENGTH_OF_CELL_FOR_LOGGING) loggTable.append("|");
            else loggTable.append("-");
        loggTable.append("\n");
    }
    private void loggLine(StringBuilder loggTable, List<String> line) {
        loggTable.append("|");
        for (String value: line) {
            String cell = value;
            if (cell.length() >= MAX_LENGTH_OF_CELL_FOR_LOGGING) cell = cell.substring(0, MAX_LENGTH_OF_CELL_FOR_LOGGING) + "|";
            else cell = cell + " ".repeat(MAX_LENGTH_OF_CELL_FOR_LOGGING - cell.length()) + "|";
            loggTable.append(cell);
        } loggTable.append("\n");
    }
}
