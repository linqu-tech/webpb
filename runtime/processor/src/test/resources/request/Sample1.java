import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

public class Sample1 {

    @WebpbRequestMapping
    void sample(FooRequest request) {
    }
}

class Dummy {
}
