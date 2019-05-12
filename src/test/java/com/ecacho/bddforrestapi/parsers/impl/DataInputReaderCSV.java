package com.ecacho.bddforrestapi.parsers.impl;

import com.ecacho.bddforrestapi.parsers.DataInputReader;

import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DataInputReaderCSV implements DataInputReader {

    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private List<String> columns;
    private String lastReadLine;
    private int counterLines;

    private static final String COLUMN_SEPARATOR = ";";

    public DataInputReaderCSV() {
        this.counterLines = 1;
    }

    @Override
    public void open(String filename) throws Exception {
        if(!Files.exists(Paths.get(filename))){
            throw new FileNotFoundException("File doesn't exists: " + filename);
        }
        fileReader = new FileReader(filename);
        bufferedReader = new BufferedReader(fileReader);
        Optional<List<String>> cols = this.parseLine(this.readLine());
        if(!cols.isPresent() || cols.get().isEmpty()){
            throw new Exception("Cannot find columns. First line of file musk have columns.");
        }
        this.columns = cols.get();
        this.countLine();
    }

    @Override
    public Optional<Map<String, Object>> readline() throws Exception {
        if(fileReader == null || bufferedReader == null){
            throw new Exception("Call the 'open' method first before use readline. FileReader is null");
        }
        if(this.columns == null){
            throw new Exception("This file doesn't have columns. Please call the 'open' method first");
        }
        Optional<List<String>> parts = this.parseLine(this.readLine());
        if(!parts.isPresent()){
            return Optional.empty();
        }
        if(parts.get().size() != this.columns.size()){
            throw new Exception(String.format("This line items doesn't match the total columns; " +
                    "columns = %d and items = %d; " +
                    "line = %s", this.columns.size(), parts.get().size(), this.lastReadLine));
        }
        Map<String, Object> result = new HashMap<>();
        int size = this.columns.size();
        for(int i = 0; i < size; i++){
            String column = this.columns.get(i);
            String item = parts.get().get(i);

            result.put(column, item);
        }
        return Optional.of(result);
    }

    @Override
    public void close() throws Exception {
        if(bufferedReader != null){
            bufferedReader.close();
        }
        if(fileReader != null){
            fileReader.close();
        }
    }

    private Optional<List<String>> parseLine(String line) throws IOException {
        List<String> result = new ArrayList<>();
        if(line == null){
            return Optional.empty();
        }
        for(String col: this.splitLine(line)){
            result.add(col);
        }
        return Optional.of(result);
    }

    private String readLine() throws IOException {
        this.lastReadLine = this.bufferedReader.readLine();
        return this.lastReadLine;
    }

    private String[] splitLine(String line){
        return line.split(COLUMN_SEPARATOR);
    }

    private void countLine(){
        this.counterLines += 1;
    }
}
