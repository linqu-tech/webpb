package tech.linqu.webpb.ts.utils;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.TsFieldOpts;
import tech.linqu.webpb.utilities.utils.DescriptorUtils;
import tech.linqu.webpb.utilities.utils.OptionUtils;

/**
 * Utilities.
 */
public class TsUtils {

    private TsUtils() {
    }

    /**
     * Check if a {@link Descriptor} should generate toAlias function.
     *
     * @param descriptor {@link Descriptor}
     * @return true if with toAlias
     */
    public static boolean toAlias(Descriptor descriptor) {
        for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            if (StringUtils.isNotEmpty(getAlias(fieldDescriptor))) {
                return true;
            }
            if (!DescriptorUtils.isMessage(fieldDescriptor)) {
                continue;
            }
            if (toAlias(fieldDescriptor.getMessageType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get alias of a field.
     *
     * @param fieldDescriptor {@link FieldDescriptor}
     * @return alias
     */
    public static String getAlias(FieldDescriptor fieldDescriptor) {
        TsFieldOpts fieldOpts = OptionUtils.getOpts(fieldDescriptor, FieldOpts::hasTs).getTs();
        return fieldOpts.getAlias();
    }
}
