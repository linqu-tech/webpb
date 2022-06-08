package tech.linqu.webpb.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.testing.compile.Compilation;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import org.junit.jupiter.api.Test;

class MixedProcessorTest {

    @Test
    void shouldProcessSample1Success() {
        Compilation compilation = javac().withProcessors(
                new WebpbMessageMappingProcessor(),
                new WebpbRequestMappingProcessor()
            )
            .compile(forResource("mixed/Sample1.java"));
        assertThat(compilation).succeeded();
    }

    @Test
    void shouldInitFailed() {
        WebpbMessageMappingProcessor processor = new WebpbMessageMappingProcessor();
        ProcessingEnvironment env = mock(ProcessingEnvironment.class);
        Messager messager = mock(Messager.class);
        when(env.getMessager()).thenReturn(messager);
        assertThrows(RuntimeException.class, () -> processor.init(env));
    }
}
