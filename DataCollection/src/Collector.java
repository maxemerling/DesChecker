import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Collector implements Runnable {
    private final Map<String, Item> entries;
    private final List<ExecutorService> subServices;
    private volatile int count;

    private static final String LINK_START = "<a href=\"";
    private static final char END = '"';

    private static final String OUTPUT_PATH = "output/data.md";

    Collector() {
        entries = new HashMap<>();
        subServices = new ArrayList<>();
    }

    @Override
    public void run() {
        int maxPage = WebTools.getMaxPage();

        ExecutorService executor = Executors.newFixedThreadPool(maxPage);

        for (int i = 1; i <= maxPage; i++) {
            final String html = WebTools.getPage(i);

            executor.execute(() -> processSiteMapHTML(html));
        }

        try {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

            for (ExecutorService subService : subServices) {
                subService.shutdown();
                subService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nWriting Data...\n");

        OutputTools.writeItemMap(entries, OUTPUT_PATH);
    }

    /**
     * Processes the given html source code of a sitemap page and extracts product links to be processed
     * @param html the html source code of a given sitemap page
     */
    private void processSiteMapHTML(String html) {
        //ExecutorService subService = Executors.newCachedThreadPool();
        ExecutorService subService = Executors.newSingleThreadExecutor();

        String section = WebTools.getSection(html);

        int next;
        while ((next = section.indexOf(LINK_START)) != -1) {
            int end = section.indexOf(END, next += LINK_START.length());
            final String itemLink = WebTools.HEROARTS + section.substring(next, end);
            section = section.substring(end);

            subService.execute(() -> processAndAdd(itemLink));
        }

        synchronized(subServices) {
            subServices.add(subService);
        }
    }

    /**
     * Processes the given link to a product page and adds the product info to the entries hashmap.
     * @param itemLink the link to the product page
     */
    private void processAndAdd(String itemLink) {
        Item item = WebTools.getItem(itemLink);
        synchronized (entries) {
            entries.put(itemLink, item);
            System.out.println("Processed " + count++ + " products.");
        }
    }
}
