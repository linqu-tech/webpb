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

class WebpbMessageMappingProcessorTest {

    @Test
    void shouldProcessSample1Success() {
        Compilation compilation = javac().withProcessors(new WebpbMessageMappingProcessor())
            .compile(forResource("message/Sample1.java"));
        assertThat(compilation).succeeded();
    }

    @Test
    void shouldProcessSample2Success() {
        Compilation compilation = javac().withProcessors(new WebpbMessageMappingProcessor())
            .compile(forResource("message/Sample2.java"));
        assertThat(compilation).succeeded();
    }

    @Test
    void shouldProcessSample3Success() {
        Compilation compilation = javac().withProcessors(new WebpbMessageMappingProcessor())
            .compile(forResource("message/Sample3.java"));
        assertThat(compilation).succeeded();
    }

    @Test
    void shouldProcessSample4Success() {
        Compilation compilation = javac().withProcessors(new WebpbMessageMappingProcessor())
            .compile(forResource("message/Sample4.java"));
        assertThat(compilation).succeeded();
    }

    @Test
    void shouldProcessSample5Success() {
        Compilation compilation = javac().withProcessors(new WebpbMessageMappingProcessor())
            .compile(forResource("message/Sample5.java"));
        assertThat(compilation).succeeded();
    }

    @Test
    void shouldProcessSample6Success() {
        Compilation compilation = javac().withProcessors(new WebpbMessageMappingProcessor())
            .compile(forResource("message/Sample6.java"));
        assertThat(compilation).succeeded();
    }

    @Test
    void shouldProcessSample7Success() {
        Compilation compilation = javac().withProcessors(new WebpbMessageMappingProcessor())
            .compile(forResource("message/Sample7.java"));
        assertThat(compilation).succeeded();
    }

    @Test
    void shouldProcessSample8Success() {
        Compilation compilation = javac().withProcessors(new WebpbMessageMappingProcessor())
            .compile(forResource("message/Sample8.java"));
        assertThat(compilation).succeeded();
    }

    @Test
    void shouldProcessSampleFailed() {
        Compilation compilation = javac().withProcessors(new WebpbMessageMappingProcessor())
            .compile(forResource("message/SampleFailed1.java"));
        assertThat(compilation).failed();
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
