import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.messaging.WebpbMessageMapping;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

public class Sample1 {

    @WebpbMessageMapping
    void sample1(FooRequest request) {
    }

    @WebpbRequestMapping
    void sample2(FooRequest request) {
    }
}

class Dummy {
}
