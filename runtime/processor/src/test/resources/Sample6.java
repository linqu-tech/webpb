import com.fasterxml.jackson.annotation.JsonIgnore;
import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

public class Sample6 {

    @JsonIgnore
    @WebpbRequestMapping
    void sample(FooRequest request) {
    }
}
