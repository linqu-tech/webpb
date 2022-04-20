import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

public enum Sample8 {
    A;

    @WebpbRequestMapping
    void sample(FooRequest request) {
    }
}
