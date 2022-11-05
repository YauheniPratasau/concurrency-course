package course.concurrency.m2_async.cf.min_price;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public Object getMinPrice(long itemId) {
        ExecutorService fixedExecutor = Executors.newFixedThreadPool(shopIds.size());
        CompletableFuture[] cfs = shopIds.stream()
                .map(shopId -> new CompletableFuture<Double>().completeAsync(
                                        () -> priceRetriever.getPrice(itemId, shopId), fixedExecutor
                                ).completeOnTimeout(Double.NaN, 2500, TimeUnit.MILLISECONDS) // 2,5 seconds because 3 seconds counter begins in tests, so there is we don't have full 3 seconds
                                .handle((price, exception) -> {
                                    if (exception != null) {
                                        return Double.NaN;
                                    }
                                    return price;
                                })
                )
                .collect(Collectors.toList())
                .toArray(new CompletableFuture[shopIds.size()]);

        return Arrays.stream(cfs)
                .map(CompletableFuture<Double>::join)
                .min(Comparator.comparing(Double::valueOf))
                .get();
    }
}
