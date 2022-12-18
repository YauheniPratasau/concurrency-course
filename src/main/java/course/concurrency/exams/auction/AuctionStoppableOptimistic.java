package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBid = new AtomicReference<>();

    public boolean propose(Bid bid) {
        if (latestBid == null || bid.getPrice() > latestBid.get().getPrice()) {
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

    public Bid stopAuction() {
        return latestBid.get();
    }
}
