import java.io.*;

public class firstLine {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        String line;
        String macroLine = "";

        while ((line = br.readLine()) != null) {
            if (line.trim().equals("MACRO")) {
                macroLine = br.readLine();
                System.out.println(macroLine);
                break; // Exit after finding first macro
            }
        }
        br.close();
    }
}