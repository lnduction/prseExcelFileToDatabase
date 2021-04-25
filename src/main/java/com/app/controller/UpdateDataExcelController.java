package com.app.controller;

import com.app.Form.UpdateDataFromExcelForm;
import com.app.service.JDBCInsertService;
import com.app.service.ParseExcelService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Objects;

@Controller
public class UpdateDataExcelController {

    ParseExcelService parseExcelService;
    JDBCInsertService jdbcInsertService;

    @Autowired
    UpdateDataExcelController(ParseExcelService parseExcelService, JDBCInsertService jdbcInsertService){
        this.parseExcelService = parseExcelService;
        this.jdbcInsertService = jdbcInsertService;
    }

    @RequestMapping(value="/update-data-from-excel", method= RequestMethod.GET)
    public String getUpdateDataFromExcel() { return "update-data-from-excel"; }

    @RequestMapping(value="/update-data-from-excel", method=RequestMethod.POST)
    public String postUpdateDataFromExcel(@ModelAttribute("UpdateDataFromExcelForm") UpdateDataFromExcelForm form, BindingResult bindingResult  , Model model) {
        if (form.getExcelFile() == null || !Objects.equals(FilenameUtils.getExtension(form.getExcelFile().getOriginalFilename()), "xlsx")) bindingResult.addError( new ObjectError("excelFile", "File extension is not excel"));
        if (form.getTableName().isEmpty()) bindingResult.addError( new ObjectError("tableName", "Table name is empty"));
        List<List<String>> parsedTable = parseExcelService.parseExcelFile(form.getExcelFile());
        if (parsedTable.isEmpty()) bindingResult.addError( new ObjectError("tableName", "Table is valid"));
        if (!jdbcInsertService.addLines(parsedTable, form.getTableName())) bindingResult.addError( new ObjectError("tableName", "SQL error^ see logs"));
        if (bindingResult.hasErrors()) {
            model.addAttribute("tableName", form.getExcelFile());
            model.addAttribute("excelFile", form.getExcelFile());
            return "update-data-from-excel";
        }
        return "index";
    }
}
