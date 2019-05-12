package com.ecacho.bddforrestapi.parsers;

import com.ecacho.bddforrestapi.parsers.impl.DataInputReaderCSV;

import java.util.Map;
import java.util.Optional;

public class DataInputReaderFactory {
    private static DataInputReaderFactory instance;

    public static DataInputReaderFactory getInstance() {
        if(instance == null){
            instance = new DataInputReaderFactory();
        }
        return instance;
    }

    public Optional<DataInputReader> getReaderByFilename(String filename){
        if(filename.toLowerCase().endsWith(".csv")){
            return Optional.of(new DataInputReaderCSV());
        }
        return Optional.empty();
    }
}
