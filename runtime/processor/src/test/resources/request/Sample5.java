import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

public class Sample5 {

    @WebpbRequestMapping
    void sample(int id, FooRequest request, String name) {
    }
}
