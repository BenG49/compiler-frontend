package interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {
    private static final String NEWLINE = "\n";
    public static String readFile(String path) {
        try {
            StringBuilder output = new StringBuilder();

            File file = new File(path);
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                output.append(reader.nextLine());
                output.append(NEWLINE);
            }

            reader.close();
            return output.toString();
        } catch (FileNotFoundException e) {
            System.out.println("Must give a valid file path!");
            return null;
        }
    }
}
