package com.ms.pdf.pdf;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.Margin;
import com.microsoft.playwright.options.Media;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class PdfService {
    private Playwright playwright;
    private Browser browser;

    @PostConstruct
    public void init(){
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    public byte[] generatePdfFromHtml(String htmlContent) {
        try (Page page = browser.newPage()) {
            page.setContent(htmlContent);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.emulateMedia(new Page.EmulateMediaOptions().setMedia(Media.PRINT));

            // O documento manda no tamanho e nas margens (@page); A4 vale só se ele não declarar nada.
            return page.pdf(new Page.PdfOptions()
                    .setFormat("A4")
                    .setPreferCSSPageSize(true)
                    .setPrintBackground(true)
                    .setMargin(new Margin()
                            .setTop("0").setBottom("0")
                            .setLeft("0").setRight("0")));
        }
    }

    @PreDestroy
    public void cleanup() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
