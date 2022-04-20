import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

public class Sample2 {

    @WebpbRequestMapping(name = "sample2")
    void sample(FooRequest request) {
    }
}
