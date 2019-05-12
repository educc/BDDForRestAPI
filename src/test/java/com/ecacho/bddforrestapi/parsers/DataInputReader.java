package com.ecacho.bddforrestapi.parsers;

import java.util.Map;
import java.util.Optional;

public interface DataInputReader {

    void open(String filename) throws Exception;
    Optional<Map<String, Object>> readline() throws Exception;
    void close() throws Exception;
}
