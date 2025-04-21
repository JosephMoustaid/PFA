package spring.bricole.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileReaderUtil {

    // read file
    // retunr a list of strings

    public List<String> readFile(String filePath){
        BufferedReader reader ;
        List<String> lines = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();

            while (line != null) {
                log.info(line);
                lines.add(line);
                // read next line
                line = reader.readLine();
            }

            reader.close();
        }  catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("File read successfully");
        }
        return lines;
    }


}
