/*
 * Copyright (c) 2020 linqu.tech, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.linqu.webpb.utilities.context;


import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend;
import tech.linqu.webpb.utilities.utils.Const;
import tech.linqu.webpb.utilities.utils.DescriptorUtils;
import tech.linqu.webpb.utilities.utils.OptionUtils;

/**
 * Context wrapper for generator.
 */
@Getter
public class RequestContext {

    private List<FileDescriptor> descriptors;

    private List<FileDescriptor> targetDescriptors;

    private WebpbExtend.FileOpts fileOpts;

    /**
     * Create context with a file option filter.
     *
     * @param predicate predicates for file option.
     * @throws Exception if eny exceptions
     */
    public RequestContext(Predicate<WebpbExtend.FileOpts> predicate) throws Exception {
        CodeGeneratorRequest request = createRequest();
        initDescriptors(request);
        initWebpbOptions(predicate);
    }

    private CodeGeneratorRequest createRequest() throws IOException, URISyntaxException {
        CodeGeneratorRequest request = null;
        if (StringUtils.equalsIgnoreCase("true", System.getProperty("debug"))) {
            URL url = RequestContext.class.getClassLoader().getResource("request.txt");
            if (url != null) {
                String input = new String(Files.readAllBytes(Paths.get(url.toURI())));
                byte[] bytes = Base64.getDecoder().decode(input.trim());
                request = CodeGeneratorRequest.parseFrom(bytes);
            }
        }
        if (request == null) {
            request = CodeGeneratorRequest.parseFrom(System.in);
        }
        return request;
    }

    private void initDescriptors(CodeGeneratorRequest request)
        throws DescriptorValidationException {
        Map<String, FileDescriptor> filesMap = new HashMap<>();
        for (DescriptorProtos.FileDescriptorProto proto : request.getProtoFileList()) {
            FileDescriptor[] dependencies = proto.getDependencyList().stream()
                .map(filesMap::get)
                .toArray(FileDescriptor[]::new);

            FileDescriptor descriptor = FileDescriptor.buildFrom(proto, dependencies);
            filesMap.put(proto.getName(), descriptor);
        }
        descriptors = new ArrayList<>(filesMap.values());
        targetDescriptors = request.getFileToGenerateList().stream()
            .map(filesMap::get)
            .collect(Collectors.toList());
    }

    private void initWebpbOptions(Predicate<WebpbExtend.FileOpts> predicate) {
        FileDescriptor webpbDescriptor =
            DescriptorUtils.resolveDescriptor(descriptors, Const.WEBPB_OPTIONS);
        this.fileOpts = OptionUtils.getOpts(webpbDescriptor, predicate);
    }
}
