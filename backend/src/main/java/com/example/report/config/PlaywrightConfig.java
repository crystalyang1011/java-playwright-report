package com.example.report.config;

import com.microsoft.playwright.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Playwright 浏览器管理配置
 * 应用启动时初始化浏览器实例，关闭时释放资源
 */
@Slf4j
@Configuration
public class PlaywrightConfig {

    @Value("${playwright.use-system-browser:false}")
    private boolean useSystemBrowser;

    private Playwright playwright;
    private Browser browser;

    @PostConstruct
    public void init() {
        log.info("正在初始化 Playwright 浏览器...");
        try {
            // 跳过自动下载其他浏览器，只使用已安装的 Chromium
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

            // 优先使用已下载的 Playwright Chromium
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

    /**
     * 查找 Playwright 已下载的 Chromium 路径
     */
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

    /**
     * 查找系统中已安装的 Chrome 路径
     */
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
