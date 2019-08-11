public class Main {
    public static void main(String[] args) {
        long start = System.nanoTime();

        Collector collector = new Collector();
        collector.run();

        System.out.println("Time elapsed: " + (int) ((System.nanoTime() - start) * 1E-9) + " seconds.");
    }
}
