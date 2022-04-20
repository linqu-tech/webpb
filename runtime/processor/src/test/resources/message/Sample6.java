import com.fasterxml.jackson.annotation.JsonIgnore;
import tech.linqu.webpb.processor.model.FooRequest;
import tech.linqu.webpb.runtime.messaging.WebpbMessageMapping;

public class Sample6 {

    @JsonIgnore
    @WebpbMessageMapping
    void sample(FooRequest request) {
    }
}
