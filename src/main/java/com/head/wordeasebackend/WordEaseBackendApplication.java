package com.head.wordeasebackend;

import com.head.wordeasebackend.once.WordDataImporter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@MapperScan("com.head.wordeasebackend.mapper")
@EnableScheduling
public class WordEaseBackendApplication {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext run = SpringApplication.run(WordEaseBackendApplication.class, args);

//        String cet4 = "C:\\Users\\headhead\\Desktop\\code\\project\\WordEase-backend\\doc\\cet-4-words.xlsx";
//        String cet6 = "C:\\Users\\headhead\\Desktop\\code\\project\\WordEase-backend\\doc\\cet-6-words.xlsx";
//        WordDataImporter wordDataImporter = run.getBean(WordDataImporter.class);
//        wordDataImporter.importData(cet4,1);
//        wordDataImporter.importData(cet6,2);
    }

}
