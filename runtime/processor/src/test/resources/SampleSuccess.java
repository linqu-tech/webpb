import org.springframework.web.bind.annotation.RequestMapping;
import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

public class SampleSuccess {

    @WebpbRequestMapping
    void sample1(FooRequest request) {
    }

    @WebpbRequestMapping(name = "sample2")
    void sample2(FooRequest request) {
    }

    @WebpbRequestMapping("sample3")
    void sample3(FooRequest request) {
    }

    @WebpbRequestMapping(message = FooRequest.class)
    void sample4(FooRequest request) {
    }

    @RequestMapping
    void sample5() {
    }
}
