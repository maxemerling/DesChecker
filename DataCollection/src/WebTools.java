import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class WebTools {

    private static final String SITEMAP = "https://heroarts.com/tools/sitemap/?page=";


    /**
     * Gets the HTML source code for the webpage.
     * @param url the url of the webpage
     * @return a String containing the HTML source code of the webpage
     * @throws IOException when an exception occurs in establishing the url connection or reading the page
     */
    private static String getHTML(String url) throws IOException {
        URL urlObj = new URL(url);
        URLConnection con = urlObj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        /*
         * Try-with-resources statement: automatically closes all resources declared in parentheses
         * Any object that implements java.lang.AutoCloseable can be a resource
         */
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder builder = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                builder.append(inputLine);
            }
            return builder.toString();
        }
    }


    /* methods for parsing sitemap */

    private static final String SECTION_HEADER = "list-row type_products", SECTION_END = "</ul>";
    private static String getSection(String html) {
        html = html.substring(html.indexOf(SECTION_HEADER));
        html = html.substring(0, html.indexOf(SECTION_END));

        return html;
    }

    private static final String ITEM_START = "<a href=\"/", ITEM_END = "/a>";
    private static List<String> getSectionLines(String html) {
        String section = getSection(html);

        List<String> lines = new ArrayList<>();

        int nextRowIdx;
        while ((nextRowIdx = section.indexOf(ITEM_START)) != -1) {
            int rowEndIdx = section.indexOf(ITEM_END, nextRowIdx) - 1;
            lines.add(section.substring(nextRowIdx + ITEM_START.length(), rowEndIdx));
            section = section.substring(rowEndIdx);
        }

        return lines;
    }
}
