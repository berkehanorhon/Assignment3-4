import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatReader {

    private String fileContent;

    public DatReader(String filename) {
        Path filePath = Path.of(filename);
        try {
            fileContent = Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the necessary Regular Expression to extract the filename given as the string and
     * surrounded by double quotation marks
     * @return the result as String
     */
    public String getStringVar(String varName) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*\"([^\"]+)\"");
        Matcher m = p.matcher(fileContent);
        m.find();
        return m.group(1);
    }

    /**
     * Write the necessary Regular Expression to extract floating point numbers from the input file
     * Your regular expression should support floating point numbers with an arbitrary number of
     * decimals or without any (e.g. 5, 5.2, 5.02, 5.0002, etc.).
     * @return the result as Double
     */
    public Double getDoubleVar(String varName) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+(?:\\.[0-9]+)?)");
        Matcher m = p.matcher(fileContent);

        if (m.find()) {
            String doubleString = m.group(1);
            try {
                return Double.parseDouble(doubleString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Method with a Regular Expression to extract integer numbers from the input file
     * @return the result as int
     */
    public int getIntVar(String varName) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract a Point object from the input file
     * points are given as an x and y coordinate pair surrounded by parentheses and separated by a comma
     * @return the result as a Point object
     */
    public Point getPointVar(String varName) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=\\s*\\((\\s*\\d+\\s*),(\\s*\\d+\\s*)\\)");
        Matcher m = p.matcher(fileContent);
        m.find();
        int x = Integer.parseInt(m.group(1).trim());
        int y = Integer.parseInt(m.group(2).trim());
        return new Point(x, y);
    }

}
