package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    public boolean propose(Bid bid) {
        if (latestBid == null || bid.getPrice() > latestBid.getPrice()) {
            synchronized (this) {
                latestBid = bid;
            }
            notifier.sendOutdatedMessage(bid);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
