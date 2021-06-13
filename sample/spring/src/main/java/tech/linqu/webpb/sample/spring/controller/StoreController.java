package tech.linqu.webpb.sample.spring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;
import tech.linqu.webpb.runtime.reactive.WebpbClient;
import tech.linqu.webpb.sample.proto.common.PageablePb;
import tech.linqu.webpb.sample.proto.common.PagingPb;
import tech.linqu.webpb.sample.proto.store.StoreDataRequest;
import tech.linqu.webpb.sample.proto.store.StoreDataResponse;
import tech.linqu.webpb.sample.proto.store.StoreGreetingRequest;
import tech.linqu.webpb.sample.proto.store.StoreGreetingResponse;
import tech.linqu.webpb.sample.proto.store.StoreListRequest;
import tech.linqu.webpb.sample.proto.store.StoreListResponse;
import tech.linqu.webpb.sample.proto.store.StorePb;

/**
 * Store controller.
 */
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final WebpbClient webpbClient;

    /**
     * Request a store data.
     *
     * @param request {@link StoreDataRequest}
     * @return {@link StoreDataResponse}
     */
    @WebpbRequestMapping
    public StoreDataResponse getStore(@Valid StoreDataRequest request) {
        Long id = request.getId();
        return new StoreDataResponse(new StorePb(id, "store-" + id, "Chengdu"));
    }

    /**
     * Request a list of stores.
     *
     * @param request {@link StoreListRequest}
     * @return {@link StoreListResponse}
     */
    @WebpbRequestMapping
    public StoreListResponse getStores(@Valid @RequestBody StoreListRequest request) {
        PageablePb pageablePb = request.getPageable();
        PagingPb pagingPb = pagingPb(pageablePb);
        List<StorePb> stores = randomStores(pagingPb);
        StoreGreetingResponse response = this.webpbClient
            .request(new StoreGreetingRequest(request.getCustomer()), StoreGreetingResponse.class);
        return new StoreListResponse(pagingPb(pageablePb), stores, response.getGreeting());
    }

    private PagingPb pagingPb(PageablePb pageablePb) {
        pageablePb = pageablePb == null ? new PageablePb() : pageablePb;
        int size = pageablePb.getSize() == null ? 10 : pageablePb.getSize();
        int page = pageablePb.getPage() == null ? 1 : pageablePb.getPage();
        int totalCount = ThreadLocalRandom.current().nextInt(100, 200);
        int totalPage = (totalCount + size - 1) / size;
        return new PagingPb(page, size, totalCount, totalPage);
    }

    private List<StorePb> randomStores(PagingPb pb) {
        int size = pb.getSize();
        int page = Math.min(pb.getPage(), pb.getTotalPage());
        List<StorePb> stores = new ArrayList<>();
        long from = (long) (page - 1) * size;
        long end = Math.min(page * size, pb.getTotalCount());
        for (long id = from; id < end; id++) {
            stores.add(new StorePb(id, "store-" + id, "Chengdu"));
        }
        return stores;
    }

    /**
     * Request a greeting message.
     *
     * @param request {@link StoreGreetingRequest}
     * @return {@link StoreGreetingResponse}
     */
    @WebpbRequestMapping
    public StoreGreetingResponse greeting(@Valid @RequestBody StoreGreetingRequest request) {
        return new StoreGreetingResponse("Welcome, " + request.getCustomer());
    }
}
