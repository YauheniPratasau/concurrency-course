package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>();

    public boolean propose(Bid bid) {
        if (latestBid.get() == null || bid.getPrice() > latestBid.get().getPrice()) {
            Bid currentBid;
            Bid updated;
            do {
                currentBid = latestBid.get();
                updated = bid;
            } while (!latestBid.compareAndSet(currentBid, updated));
            notifier.sendOutdatedMessage(bid);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
