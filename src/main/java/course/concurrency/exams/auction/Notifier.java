package course.concurrency.exams.auction;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Notifier {

    private LinkedBlockingQueue<Bid> messageQueue = new LinkedBlockingQueue<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    {
        executorService.submit(() -> {
            while (true) {
                Bid bid = messageQueue.poll();
                if (bid != null) {
                    imitateSending();
                }
            }
        });
    }

    public void sendOutdatedMessage(Bid bid) {
        messageQueue.add(bid);
    }

    private void imitateSending() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

    public void shutdown() {
    }
}
