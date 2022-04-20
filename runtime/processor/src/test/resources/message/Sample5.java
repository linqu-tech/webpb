import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.messaging.WebpbMessageMapping;

public class Sample5 {

    @WebpbMessageMapping
    void sample(int id, FooRequest request, String name) {
    }
}
