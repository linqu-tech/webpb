package tech.linqu.webpb.runtime.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.linqu.webpb.runtime.mvc.WebpbRequestUtils.mergeVariables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WebpbRequestUtilsTest {

    @Test
    void givenTwoEmptyMaps_ThenMergeSuccess() {
        assertTrue(mergeVariables(Collections.emptyMap(), Collections.emptyMap()).isEmpty());
    }

    @Test
    void givenAttributesNotEmpty_ThenMergeSuccess() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("a", "b");
        Map<String, String> map = mergeVariables(attributes, Collections.emptyMap());
        assertEquals(1, map.size());
        assertTrue(map.containsKey("a"));
    }

    @Test
    void givenParameterMapNotEmpty_ThenMergeSuccess() {
        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("a", new String[] { "b" });
        Map<String, String> map = mergeVariables(Collections.emptyMap(), parameters);
        assertEquals(1, map.size());
        assertTrue(map.containsKey("a"));
    }

    @Test
    void givenTwoNonEmptyMaps_ThenMergeSuccess() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("a", "b");

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("c", new String[] { "d" });

        Map<String, String> map = mergeVariables(attributes, parameters);
        assertEquals(2, map.size());
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("c"));
    }
}
