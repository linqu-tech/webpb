import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.messaging.WebpbMessageMapping;

public class Sample4 {

    @WebpbMessageMapping(message = FooRequest.class)
    void sample(FooRequest request) {
    }
}
