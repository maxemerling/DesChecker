import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

class OutputTools {

    private static final String HEADER = "**** ";
    private static final char SEPARATOR = '|';

    /**
     * Formats and writes the given Map to the given file path.
     * Precondition: the path is valid and is a .md file.
     * @param map the map to be written, mapping Strings to Items
     * @param path the file path to create the output file
     */
    static void writeItemMap(Map<String, Item> map, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)))) {
            for (String link : map.keySet()) {
                Item item = map.get(link);
                writer.write(HEADER + item.getNumber() + SEPARATOR + item.getName());
                writer.newLine();
                writer.write(link);
                writer.newLine();
                writer.write(item.getDescription());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
