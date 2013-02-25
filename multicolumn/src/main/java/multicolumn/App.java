package multicolumn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        // default values
        String inputFilename = "sample-input-01.txt";
        String outputFilename = "sample-output-01.txt";
        int columnCount = 4;
        int columnWidth = 30;
        
        // read commandline args
        if (args != null && args.length == 4) {
            inputFilename = args[0];
            outputFilename = args[1];
            columnCount = Integer.parseInt(args[2]);
            columnWidth = Integer.parseInt(args[3]);
        }
        
        // do the work
        String input = readFileContent(inputFilename);
        String output = new App().processInput(input, columnCount, columnWidth);
        writeContentToFile(outputFilename, output);
        
        if (args == null || args.length == 0) {
            System.out.println(output);
        }
    }
    
    private static final int spaceBetweenColumns = 10;
    
    private int columnCount;
    private int columnWidth;
    
    private ArrayList<String> lines = new ArrayList<String>();
    
    public String processInput(String input, int columnCount, int columnWidth) {
        this.columnCount = columnCount;
        this.columnWidth = columnWidth;
        
        splitContentIntoLines(input);
        return constructOutput(getLinesPerColumn());
    }
    
    private int getLinesPerColumn() {
        return lines.size() / columnCount + 1;
    }
    
    private String constructOutput(int linesPerColumn) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int rowIndex = 0; rowIndex < linesPerColumn; rowIndex++) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                int effectiveColumnWidth = getEffectiveColumnWidth(columnIndex);
                int lineIndex = rowIndex + columnIndex * linesPerColumn;
                String formattedLine = getFormattedLine(lineIndex);
                stringBuilder.append(String.format(
                        "%-" + effectiveColumnWidth + "s",
                        formattedLine));
            }
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }
    
    private String getFormattedLine(int lineIndex) {
        if (!lineExists(lineIndex)) {
            return "";
        }
        String line = lines.get(lineIndex);
        if (isNextLineEmpty(lineIndex)) {
            return line;
        }
        if (!requiresFormatting(line)) {
            return line;
        }
        return formatLine(line);
    }
    
    private String formatLine(String line) {
        String[] words = getWords(line);
        int numberOfSpaces = words.length - 1;
        int spaceWidth = getAdditionalSpace(line) / numberOfSpaces + 1;
        int numberOfSpacesThatNeedToBeLonger = getAdditionalSpace(line) % numberOfSpaces;
        
        String formattedLine = "";
        for (String word : words) {
            formattedLine += word;
            formattedLine += getSpaceToInsert(numberOfSpaces--, 
                    numberOfSpacesThatNeedToBeLonger--, spaceWidth);
        }
        return formattedLine;
    }
    
    private String getSpaceToInsert(int numberOfSpaces, 
            int numberOfSpacesThatNeedToBeLonger, int spaceWidth) {
        if (numberOfSpacesThatNeedToBeLonger > 0) {
            return String.format("%" + (spaceWidth + 1) + "s", "");
        } else if (numberOfSpaces > 0) {
            return String.format("%" + spaceWidth + "s", "");
        }
        return "";
    }
    
    private boolean requiresFormatting(String line) {
        return getAdditionalSpace(line) > 0
                && line.contains(" ");
    }
    
    private int getAdditionalSpace(String line) {
        return columnWidth - line.length();
    }
    
    private boolean isNextLineEmpty(int lineIndex) {
        return !lineExists(lineIndex + 1)
                || lines.get(lineIndex + 1).equals("");
    }
    
    private boolean lineExists(int lineIndex) {
        return lineIndex < lines.size();
    }
    
    private int getEffectiveColumnWidth(int columnIndex) {
        if (columnIndex < columnCount - 1) {
            return columnWidth + spaceBetweenColumns;
        } else {
            return columnWidth;
        }
    }
    
    private void splitContentIntoLines(String content) {
        for (String paragraph : getParagraphs(content)) {
            if (!lines.isEmpty()) {
                lines.add("");
            }
            String actualLine = "";
            for (String word : getWords(paragraph)) {
                for (String part : getSplittedWordIfTooLong(word, columnWidth)) {
                    if (fitsIntoLine(actualLine, part, columnWidth)) {
                        actualLine = appendToLine(actualLine, part);
                    } else {
                        lines.add(actualLine);
                        actualLine = part;
                    }
                }
            }
            lines.add(actualLine);
        }
    }
    
    private String[] getParagraphs(String data) {
        return data.split("(\r\n){2}|\r{2}|\n{2}");
    }
    
    private String[] getWords(String data) {
        return data.split("[ \t\r\n]+");
    }
    
    private String[] getSplittedWordIfTooLong(String word, int maxWidth) {
        if (word.length() > maxWidth) {
            return new String[] {
                word.substring(0, maxWidth - 1) + "-",
                word.substring(maxWidth - 1)
            };
        } else {
            return new String[] { word };
        }
    }
    
    private boolean fitsIntoLine(String line, String word, int maxWidth) {
        return (line.length() == 0 || line.length() + 1 + word.length() <= maxWidth);
    }
    
    private String appendToLine(String line, String word) {
        if (line.length() != 0) {
            line += " ";
        }
        line += word;
        return line;
    }
    
    private static String readFileContent(String filename) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[1024];
        int bytesRead;
        
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        try {
            while ((bytesRead = reader.read(buffer)) != -1) {
                String tempData = String.valueOf(buffer, 0, bytesRead);
                stringBuilder.append(tempData);
            }
        } finally {
            reader.close();
        }
        
        return stringBuilder.toString().trim();
    }
    
    private static void writeContentToFile(String filename, String content) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
        try {
            writer.write(content);
            writer.flush();
        } finally {
            writer.close();
        }
    }
}
