package com.app.service.Impl;

import com.app.service.JDBCInsertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Statement;
import java.util.*;

@Service
public class JDBCInsertServiceImplementation implements JDBCInsertService {

    private final Logger logger = LoggerFactory.getLogger(JDBCInsertServiceImplementation.class);
    private final DataSource dataSource;
    private final String sqlInsertPattern = "INSERT INTO ${TABLE_NAME} ( ${HEADERS} ) VALUES \n${VALUES}";
    private static final String HEAD_DELIMITER = "-";
    private final String defaultHead = "string";
    private final Map<String, String> extensions = Map.of(
            "int", "integer",
            "varchar", "string",
            "text","string",
            "decimal","double",
            "tinyint", "small",
            "date", "date",
            "datetime", "time"
    );

    @Autowired
    JDBCInsertServiceImplementation(DataSource dataSource){ this.dataSource = dataSource; }

    @Override
    public boolean addLines(List<List<String>> parsedFile, String tableName) {
        List<String> headers = parseHeaders(parsedFile.get(0));
        parsedFile.remove(0);
        try(Statement statement = dataSource.getConnection().createStatement()) {
            String sql = sqlInsertPattern;
            sql = sql.replace("${TABLE_NAME}", tableName);
            sql = addHeaders(headers, sql);
            sql = addValues(parsedFile, headers, sql);
            logger.info("SQL query was created like \n" + sql);
            statement.executeUpdate(sql);
        } catch (Exception e) {
            logger.error("error insert data see logs " + e.getMessage());
            return false;
        } return true;
    }

    private List<String> parseHeaders(List<String> headers) {
        List<String> parsedHeaders = new ArrayList<>();
        for (String head: headers) parsedHeaders.add(parseHead(head));
        logger.info("header " + headers.toString() + " parsed like " + headers.toString());
        return parsedHeaders;
    }

    private String parseHead(String head) {
        String[] kayValue = head.split(HEAD_DELIMITER);
        if (kayValue.length != 2) {
            kayValue = new String[]{kayValue[0], kayValue[1]};
            logger.warn("head " + head + "parsed with error, result of parsing " + kayValue[0] + HEAD_DELIMITER + kayValue[1]);
        }
        String headName = kayValue[0].trim();
        String extension = kayValue[1].toLowerCase().trim();
        String parsedExtension = extensions.get(extension);
        if (parsedExtension == null) {
            logger.warn("extension " + extension + " of head " + head + "is not support, will be replaced like " + defaultHead);
            extension = extensions.get(defaultHead);
        } return headName + HEAD_DELIMITER + parsedExtension;
    }

    private String addHeaders(List<String> headers, String sql) {
        StringBuilder headersStringBuilder = new StringBuilder();
        for (String head : headers) headersStringBuilder.append(head.split(HEAD_DELIMITER)[0]).append(", ");
        String result = headersStringBuilder.append(" ").toString().replace(",  ", "");
        logger.info("headers " + result + " was add to SQL query");
        return sql.replace("${HEADERS}", result );
    }

    private String addValues(List<List<String>> parsedFile, List<String> headers, String sql) {
        StringBuilder valuesStringBuilder = new StringBuilder();
        for(List<String> row: parsedFile) addValueLine(row, headers, valuesStringBuilder);
        valuesStringBuilder.append(";");
        String result = valuesStringBuilder.toString().replaceAll(", {2}", "").replace("), \n;", ");");
        logger.info("values was add to SQL query");
        return sql.replace("${VALUES}", result );
    }

    private void addValueLine(List<String> values, List<String> headers, StringBuilder valuesStringBuilder){
        valuesStringBuilder.append("( ");
        for (int i = 0; i < values.size(); i++)
            valuesStringBuilder.append(getCorrectValue(values.get(i), headers.get(i))).append(", ");
        valuesStringBuilder.append("  ), \n");
    }

    private String getCorrectValue(String value, String head) {
        String extension = head.split(HEAD_DELIMITER)[1];
        if (value.equals("DEFAULT")) return value;
        if (value.isEmpty()) return "NULL";
        switch (extension) {
            case ("string") : case ("date"): case ("time") : return "'" + value + "'";
            case ("integer") : case ("double") : case ("small"): return value;
        } return "'" + value + "'";
    }

}
