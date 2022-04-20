import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.messaging.WebpbMessageMapping;

public enum Sample8 {
    A;

    @WebpbMessageMapping
    void sample(FooRequest request) {
    }
}
