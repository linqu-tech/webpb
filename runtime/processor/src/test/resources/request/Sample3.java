import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

public class Sample3 {

    @WebpbRequestMapping("sample3")
    void sample(FooRequest request) {
    }
}
