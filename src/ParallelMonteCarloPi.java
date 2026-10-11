import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

void main(String[] args) {
    if (args.length != 1) {
        IO.println("Please provide the number of threads as the first and only argument.");
        return;
    }

    int threads;
    try {
        threads = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
        IO.println("Invalid number of threads.");
        return;
    }

    long iterations = 1_000_000_000L;
    long iterationsPerThread = iterations / threads;
    AtomicLong totalPointsInCircle = new AtomicLong(0);

    long startTime = System.nanoTime();

    Thread[] threadArray = new Thread[threads];
    for (int i = 0; i < threads; i++) {
        threadArray[i] = new Thread(() -> {
            long pointsInCircle = 0;
            for (long j = 0; j < iterationsPerThread; j++) {
                double x = ThreadLocalRandom.current().nextDouble();
                double y = ThreadLocalRandom.current().nextDouble();

                if (x * x + y * y <= 1.0) {
                    pointsInCircle++;
                }
            }
            totalPointsInCircle.addAndGet(pointsInCircle);
        });
        threadArray[i].start();
    }

    for (int i = 0; i < threads; i++) {
        try {
            threadArray[i].join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    long endTime = System.nanoTime();
    double timeInMs = (endTime - startTime) / 1_000_000.0;

    long actualIterations = iterationsPerThread * threads;

    double pi = 4.0 * totalPointsInCircle.get() / actualIterations;

    System.out.printf("PI is %.5f%n", pi);
    IO.println("THREADS " + threads);
    System.out.printf("ITERATIONS %,d%n", actualIterations);
    System.out.printf("TIME %.2fms%n", timeInMs);
}
