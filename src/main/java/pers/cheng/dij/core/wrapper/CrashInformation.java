package pers.cheng.dij.core.wrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CrashInformation {
    private List<String> crashLines;

    public void parseCrashLinesFromFile(String filename) throws IOException {
        crashLines = new ArrayList<>();

        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(filename));
        String line = bufferedReader.readLine();
        while (line != null) {
            line = line.trim();
            if (!line.isBlank()) {
                crashLines.add(line);
            }
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
    }

    public List<String> getCrashLines() {
        return crashLines;
    }
}
