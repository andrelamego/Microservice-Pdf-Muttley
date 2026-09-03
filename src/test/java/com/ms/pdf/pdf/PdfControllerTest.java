package com.ms.pdf.pdf;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfControllerTest {
    @Test void respostaPreservaBytesPdfETipoDeConteudo() {
        PdfService service=mock(PdfService.class);var controller=new PdfController();ReflectionTestUtils.setField(controller,"pdfService",service);
        byte[] bytes={37,80,68,70};when(service.generatePdfFromHtml("html")).thenReturn(bytes);
        var response=controller.gerarPdf(new PdfController.GerarPdfRequest("html"));
        assertThat(response.getStatusCode().value()).isEqualTo(200);assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(response.getBody()).isEqualTo(bytes);
    }
    @Test void falhaDeRenderizacaoRetorna500() {
        PdfService service=mock(PdfService.class);var controller=new PdfController();ReflectionTestUtils.setField(controller,"pdfService",service);
        when(service.generatePdfFromHtml("html")).thenThrow(new IllegalStateException("falha simulada"));
        assertThat(controller.gerarPdf(new PdfController.GerarPdfRequest("html")).getStatusCode().value()).isEqualTo(500);
    }
}
