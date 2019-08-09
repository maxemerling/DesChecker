import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class WebTools {

    public static final String HEROARTS = "https://heroarts.com";
    private static final String SITEMAP_EXT = "/tools/sitemap/?page=", SITEMAP = HEROARTS + SITEMAP_EXT;


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

    private static final String INDICATOR = "&hellip;";
    private static final char END = '"';
    public static int getMaxPage() throws IOException {
        String html = getHTML(SITEMAP);
        int marker;
        return Integer.parseInt(html.substring(marker = html.indexOf(SITEMAP_EXT, html.indexOf(INDICATOR)) + SITEMAP_EXT.length(), html.indexOf(END, marker)));
    }

    private static final String SECTION_HEADER = "list-row type_products", SECTION_END = "<div class";
    private static String getSection(String html) {
        int marker;
        return html = html.substring(marker = html.indexOf(SECTION_HEADER), html.indexOf(SECTION_END, marker));
    }

    private static final String LINK_START = "<a href=\"";
    private static List<String> getProductLinks(String html) {
        String section = getSection(html);

        List<String> lines = new ArrayList<>();

        int next;
        while ((next = section.indexOf(LINK_START)) != -1) {
            int end = section.indexOf(END, next += LINK_START.length()) - 1;
            lines.add(HEROARTS + section.substring(next, end));
            section = section.substring(end);
        }

        return lines;
    }

    private static List<String> getAllProductLinks() {
        try {
            List<String> links = new ArrayList<>();
            int maxPage = getMaxPage();
            for (int page = 1; page <= maxPage; page++) {
                links.addAll(getProductLinks(getHTML(SITEMAP + page)));
            }
            return links;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        long start = System.nanoTime();
        List<String> links = getAllProductLinks();
        long end = System.nanoTime();


        for (String link : links) {
            System.out.println(link);
        }

        System.out.println("\n\n\nTime Elapsed: " + (end - start) * 1E-9 + " seconds");
    }
}
