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

class WebpbRequestMappingProcessorTest {

    @Test
    void shouldProcessSampleSuccess() {
        Compilation compilation = javac().withProcessors(new WebpbRequestMappingProcessor())
            .compile(forResource("SampleSuccess.java"));
        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    void shouldProcessSampleFailed() {
        Compilation compilation = javac().withProcessors(new WebpbRequestMappingProcessor())
            .compile(forResource("SampleFailed1.java"));
        assertThat(compilation).failed();
    }

    @Test
    void shouldInitFailed() {
        WebpbRequestMappingProcessor processor = new WebpbRequestMappingProcessor();
        ProcessingEnvironment env = mock(ProcessingEnvironment.class);
        Messager messager = mock(Messager.class);
        when(env.getMessager()).thenReturn(messager);
        assertThrows(RuntimeException.class, () -> processor.init(env));
    }
}
