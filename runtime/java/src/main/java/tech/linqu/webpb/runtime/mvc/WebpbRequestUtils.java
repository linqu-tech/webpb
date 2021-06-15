package tech.linqu.webpb.runtime.mvc;

import java.util.HashMap;
import java.util.Map;
import tech.linqu.webpb.runtime.WebpbMessage;

/**
 * Utilities to update a {@link WebpbMessage}.
 */
public class WebpbRequestUtils {

    private WebpbRequestUtils() {
    }

    /**
     * Merge attributes and parameterMap to a variablesMap.
     *
     * @param attributes   map of attributes
     * @param parameters multimap of parameters
     * @return map of variables
     */
    public static Map<String, String> mergeVariables(Map<String, String> attributes,
                                                     Map<String, String[]> parameters) {
        Map<String, String> variablesMap =
            attributes == null ? new HashMap<>() : new HashMap<>(attributes);
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length > 0) {
                variablesMap.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        return variablesMap;
    }
}
