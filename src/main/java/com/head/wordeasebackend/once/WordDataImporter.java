package com.head.wordeasebackend.once;

import com.head.wordeasebackend.mapper.WordMapper;
import com.head.wordeasebackend.model.entity.Word;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class WordDataImporter {

    @Resource
    private WordMapper wordMapper;

    public void importData(String filePath, int number) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0); // 读取第一个sheet

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // 从第2行开始读，跳过表头
            Row row = sheet.getRow(i);

            String spelling = row.getCell(0).getStringCellValue();
            String phonetic = row.getCell(1).getStringCellValue();
            String definition = row.getCell(2).getStringCellValue();

            // 构建Word对象
            Word word = new Word();
            word.setSpelling(spelling);
            word.setPhonetic(phonetic);
            word.setDefinition(definition);
            word.setWordType(number);  // 设置单词类型 1 - cet4 2 - cet6
            // 插入数据库
            wordMapper.insert(word);
        }

        workbook.close();
        fis.close();
    }


}
