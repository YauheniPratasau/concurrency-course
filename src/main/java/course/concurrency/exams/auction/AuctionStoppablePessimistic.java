package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private Bid latestBid;

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

    public Bid stopAuction() {
        return latestBid;
    }
}
