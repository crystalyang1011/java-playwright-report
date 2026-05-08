package com.example.report.config;

import com.microsoft.playwright.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class PlaywrightConfig {

    private static final Logger log = LoggerFactory.getLogger(PlaywrightConfig.class);

    @Value("${playwright.use-system-browser:false}")
    private boolean useSystemBrowser;

    private Playwright playwright;
    private Browser browser;

    @PostConstruct
    public void init() {
        log.info("正在初始化 Playwright 浏览器...");
        try {
            System.setProperty("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "true");
            
            playwright = Playwright.create();
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(java.util.Arrays.asList(
                            "--no-sandbox",
                            "--disable-setuid-sandbox",
                            "--disable-dev-shm-usage",
                            "--disable-gpu"
                    ));

            Path playwrightChromium = findPlaywrightChromium();
            if (playwrightChromium != null) {
                launchOptions.setExecutablePath(playwrightChromium);
                log.info("使用 Playwright Chromium: {}", playwrightChromium);
            } else if (useSystemBrowser) {
                Path chromePath = findChromePath();
                if (chromePath != null) {
                    launchOptions.setExecutablePath(chromePath);
                    log.info("使用系统 Chrome: {}", chromePath);
                }
            }

            browser = playwright.chromium().launch(launchOptions);
            log.info("Playwright 浏览器初始化完成");

        } catch (Exception e) {
            log.error("Playwright 初始化失败", e);
            throw new RuntimeException(
                "无法启动浏览器，请运行: mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=install chromium",
                e
            );
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("正在关闭 Playwright 浏览器...");
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
        log.info("Playwright 浏览器已关闭");
    }

    public Browser getBrowser() {
        return browser;
    }

    private Path findPlaywrightChromium() {
        String userHome = System.getProperty("user.home");
        String[] possiblePaths = {
            userHome + "/AppData/Local/ms-playwright/chromium-1091/chrome-win/chrome.exe",
            userHome + "/AppData/Local/ms-playwright/chromium-1084/chrome-win/chrome.exe",
            userHome + "/AppData/Local/ms-playwright/chromium-1076/chrome-win/chrome.exe"
        };

        for (String path : possiblePaths) {
            Path chromiumPath = Paths.get(path);
            if (java.nio.file.Files.exists(chromiumPath)) {
                return chromiumPath;
            }
        }
        return null;
    }

    private Path findChromePath() {
        String[] possiblePaths = {
            "C:/Program Files/Google/Chrome/Application/chrome.exe",
            "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe",
            System.getenv("LOCALAPPDATA") + "/Google/Chrome/Application/chrome.exe",
            System.getenv("PROGRAMFILES") + "/Google/Chrome/Application/chrome.exe",
            System.getenv("PROGRAMFILES(X86)") + "/Google/Chrome/Application/chrome.exe"
        };

        for (String path : possiblePaths) {
            if (path != null) {
                Path chromePath = Paths.get(path);
                if (java.nio.file.Files.exists(chromePath)) {
                    return chromePath;
                }
            }
        }
        return null;
    }
}
