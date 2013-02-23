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
        
        StringBuffer data = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        char[] buffer = new char[1024];
        int bytesRead;
        while ((bytesRead = reader.read(buffer)) != -1) {
            String tempData = String.valueOf(buffer, 0, bytesRead);
            data.append(tempData);
        }
        reader.close();
        
        String inputData = data.toString().trim();
        System.out.println(inputData);
        
        String[] paragraphs = inputData.split("(\r\n){2}|\r{2}|\n{2}");
        
        for (String paragraph : paragraphs) {
            
            String[] words = paragraph.split("[ \t\r\n]+");
            System.out.println(words.length);
            
            String actualLine = "";
            for (String word : words) {
                
                String[] parts;
                if (word.length() > columnWidth) {
                    parts = new String[] {
                        word.substring(0, columnWidth - 1) + "-",
                        word.substring(columnWidth - 1)
                    };
                } else {
                    parts = new String[] { word };
                }
                
                for (String part : parts) {
                    if (actualLine.length() + 1 + part.length() <= columnWidth) {
                        if (actualLine.length() != 0) {
                            actualLine += " ";
                        }
                        actualLine += part;
                    } else {
                        lines.add(actualLine);
                        actualLine = part;
                    }
                }
                
            }
            
            lines.add(actualLine);
            lines.add("");
            
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
}
