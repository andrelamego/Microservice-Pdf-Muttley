package com.ms.pdf.pdf;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfServiceTest {
    @Test void respeitaOTamanhoDaPaginaDoDocumentoComFundoEFechaPagina() {
        Browser browser=mock(Browser.class); Page page=mock(Page.class); var service=new PdfService();
        ReflectionTestUtils.setField(service,"browser",browser);
        when(browser.newPage()).thenReturn(page); when(page.pdf(any())).thenReturn(new byte[]{37,80,68,70});
        assertThat(service.generatePdfFromHtml("<h1>Certificado</h1>")).containsExactly((byte)37,(byte)80,(byte)68,(byte)70);
        var ordem=inOrder(page); ordem.verify(page).setContent("<h1>Certificado</h1>");
        ordem.verify(page).waitForLoadState(LoadState.NETWORKIDLE);
        ordem.verify(page).emulateMedia(argThat(o -> o.media.orElse(null)==Media.PRINT));
        ordem.verify(page).pdf(argThat(o -> "A4".equals(o.format) && Boolean.TRUE.equals(o.printBackground)
                && Boolean.TRUE.equals(o.preferCSSPageSize)
                && "0".equals(o.margin.top) && "0".equals(o.margin.bottom)
                && "0".equals(o.margin.left) && "0".equals(o.margin.right)));
        ordem.verify(page).close();
    }
    @Test void falhaNaRenderizacaoAindaFechaPagina() {
        Browser browser=mock(Browser.class); Page page=mock(Page.class);var service=new PdfService();
        ReflectionTestUtils.setField(service,"browser",browser);when(browser.newPage()).thenReturn(page);
        doThrow(new IllegalStateException("falha simulada")).when(page).setContent(anyString());
        assertThatThrownBy(() -> service.generatePdfFromHtml("html")).isInstanceOf(IllegalStateException.class);
        verify(page).close();verify(page,never()).pdf(any());
    }
    @Test void cleanupFechaRecursosInicializados() {
        Browser browser=mock(Browser.class);Playwright playwright=mock(Playwright.class);var service=new PdfService();
        ReflectionTestUtils.setField(service,"browser",browser);ReflectionTestUtils.setField(service,"playwright",playwright);
        service.cleanup();verify(browser).close();verify(playwright).close();
    }
    @Test void cleanupSemInicializacaoEPermitido() { assertThatCode(() -> new PdfService().cleanup()).doesNotThrowAnyException(); }
}
