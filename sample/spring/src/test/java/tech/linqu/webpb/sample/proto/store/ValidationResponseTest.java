package tech.linqu.webpb.sample.proto.store;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class ValidationResponseTest {

    @Test
    void test() {
        ValidationResponse response = new ValidationResponse()
            .setErrors(Collections.emptyMap());
        assertNotNull(response.webpbMeta());
        assertNotNull(response.getErrors());
    }
}
