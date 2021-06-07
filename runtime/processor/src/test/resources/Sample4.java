import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

public class Sample4 {

    @WebpbRequestMapping(message = FooRequest.class)
    void sample(FooRequest request) {
    }
}
