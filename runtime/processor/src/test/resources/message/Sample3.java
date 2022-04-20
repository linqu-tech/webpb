import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.messaging.WebpbMessageMapping;

public class Sample3 {

    @WebpbMessageMapping
    void sample(FooRequest request) {
    }
}
