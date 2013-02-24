package multicolumn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        String filename = "sample-input-01.txt";
        int columnCount = 4;
        int columnWidth = 30;
        int spaceBetweenColumns = 10;
        
        LinkedList<String> lines = new LinkedList<String>();
        ArrayList<String> formatedLines = new ArrayList<String>();
        
        String inputData = readFileContent(filename);
        
        for (String paragraph : getParagraphs(inputData)) {
            if (lines.size() != 0) {
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
        
        ListIterator<String> iterator = lines.listIterator();
        while (iterator.hasNext()) {
            
            String line = iterator.next().trim();
            //System.out.println(java.util.Arrays.toString(line.toCharArray()));
            System.out.print(String.format("%3s :", line.length()));
            int additionalSpace = columnWidth - line.length();
            
            if (!line.contains(" ") || additionalSpace == 0) {
                System.out.println(line);
                formatedLines.add(line);
                continue;
            }
            if (!iterator.hasNext()) {
                System.out.println(line);
                formatedLines.add(line);
                break;
            }
            if (iterator.next().equals("")) {
                System.out.println(line);
                formatedLines.add(line);
                iterator.previous();
                continue;
            }
            iterator.previous();
            
            String[] words = line.split(" ");
            int spaceCount = words.length - 1;
            int additionalSpaceWidth = additionalSpace / spaceCount;
            int longerSpaceCount = additionalSpace % spaceCount;
            
            String newLine = "";
            for (String word : words) {
                newLine += word;
                if (longerSpaceCount > 0) {
                    newLine += String.format("%" + (1 + additionalSpaceWidth + 1) + "s", "");
                } else if (spaceCount > 0) {
                    newLine += String.format("%" + (1 + additionalSpaceWidth) + "s", "");
                }
                longerSpaceCount--;
                spaceCount--;
            }

            System.out.println(newLine);
            formatedLines.add(newLine);
            
        }
        
        int linesPerColumn = formatedLines.size() / columnCount + 1;
        System.out.println(formatedLines.size());
        
        for (int i = 0; i < linesPerColumn; i++) {
            
            for (int j = 0; j < columnCount; j++) {
                
                int effectiveColumnWidth;
                if (j < columnCount - 1) {
                    effectiveColumnWidth = columnWidth + spaceBetweenColumns;
                } else {
                    effectiveColumnWidth = columnWidth;
                }
                
                int lineIndex = i + j * linesPerColumn;
                if (lineIndex < formatedLines.size()) {
                    
                    System.out.print(String.format("%-" + effectiveColumnWidth + "s", formatedLines.get(lineIndex)));
                    
                }
                
            }
            
            System.out.println();
            
        }
        
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
    
    private static String[] getParagraphs(String data) {
        return data.split("(\r\n){2}|\r{2}|\n{2}");
    }
    
    private static String[] getWords(String data) {
        return data.split("[ \t\r\n]+");
    }
    
    private static String[] getSplittedWordIfTooLong(String word, int maxWidth) {
        if (word.length() > maxWidth) {
            return new String[] {
                word.substring(0, maxWidth - 1) + "-",
                word.substring(maxWidth - 1)
            };
        } else {
            return new String[] { word };
        }
    }
    
    private static boolean fitsIntoLine(String line, String word, int maxWidth) {
        return (line.length() == 0 || line.length() + 1 + word.length() <= maxWidth);
    }
    
    private static String appendToLine(String line, String word) {
        if (line.length() != 0) {
            line += " ";
        }
        line += word;
        return line;
    }
}
