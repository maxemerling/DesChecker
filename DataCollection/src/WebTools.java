import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class WebTools {

    static final String HEROARTS = "https://heroarts.com";
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

    static String getPage(int page) {
        try {
            return getHTML(SITEMAP + page);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* methods for parsing sitemap */

    private static final String INDICATOR = "&hellip;";
    private static final char END = '"';
    static int getMaxPage() {
        int marker;
        String html = getPage(1);
        assert html != null;
        return Integer.parseInt(html.substring(marker = html.indexOf(SITEMAP_EXT, html.indexOf(INDICATOR)) + SITEMAP_EXT.length(), html.indexOf(END, marker)));
    }

    private static final String SECTION_HEADER = "list-row type_products", SECTION_END = "<div class";
    static String getSection(String html) {
        int marker;
        return html.substring(marker = html.indexOf(SECTION_HEADER), html.indexOf(SECTION_END, marker));
    }

    private static final String ITEM_START = "\"product_name\" itemprop=\"name\">", DES_INDICATOR = "<div class=\"description\" itemprop=\"description\">",
            DES_START = "<p>", DES_END = "</p>";
    private static final char ITEM_END = '<';
    static Item getItem(String link) {
        try {
            String html = getHTML(link);
            int marker;
            String number = html.substring(marker = html.indexOf(ITEM_START) + ITEM_START.length(), marker = html.indexOf(" ", marker));
            String name = html.substring(marker + 1, html.indexOf(ITEM_END, marker));

            String description = html.substring(marker = html.indexOf(DES_START, html.indexOf(DES_INDICATOR)) + DES_START.length(), html.indexOf(DES_END, marker));

            return new Item(number, name, description);
        } catch (IOException e) {
            System.out.println(link);
            e.printStackTrace();
            return null;
        }
    }
}
