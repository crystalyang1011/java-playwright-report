# Java + Playwright PDF 报表生成

使用 **Spring Boot + Playwright 无头浏览器** 在后端生成高质量 PDF 报表。支持 ECharts 图表、复杂 CSS 排版、中文、分页等。

**前端只需一个 HTTP 请求，可以是任何技术栈。**

## 效果预览

生成的 PDF 包含：

- 报表标题与统计周期
- 统计卡片（总销量、总销售额、平均单价）
- **ECharts 销售趋势图**（折线图 + 柱状图）
- **ECharts 分类占比图**（环形饼图）
- 销售明细数据表格
- 合计行与页脚

## 技术架构

```
前端（任意技术栈）
    ↓  GET /api/schedule/report/download
后端 Spring Boot
    ↓  1. 生成数据
    ↓  2. Thymeleaf 渲染 HTML（含 ECharts）
    ↓  3. Playwright + Chromium → PDF
    ↓  返回 PDF 文件流
浏览器自动下载
```

## 项目结构

```
java-playwright-report/
├── backend/                          # Java 后端（核心）
│   ├── src/main/java/com/example/report/
│   │   ├── config/
│   │   │   ├── CorsConfig.java       # 跨域配置
│   │   │   └── PlaywrightConfig.java # 浏览器生命周期管理
│   │   ├── controller/
│   │   │   └── ReportController.java # REST API
│   │   ├── dto/
│   │   │   ├── ReportData.java       # 报表数据 DTO
│   │   │   └── SalesRecord.java      # 销售记录 DTO
│   │   ├── service/
│   │   │   └── ReportService.java    # 数据生成 + PDF 生成
│   │   └── PlaywrightReportApplication.java
│   ├── src/main/resources/
│   │   ├── templates/
│   │   │   └── report.html           # Thymeleaf + ECharts 模板
│   │   └── application.yml
│   └── pom.xml
│
└── demo.html                         # 纯 HTML 前端示例（一个下载按钮）
```

## 环境准备

### Java 17

已内置在 `C:\java\jdk-17`，环境变量已配置：
- `JAVA_HOME = C:\java\jdk-17`
- `PATH` 包含 `%JAVA_HOME%\bin`

验证：
```bash
java -version
```

### Maven 3.9.6

已内置在 `C:\java\apache-maven-3.9.6`，环境变量已配置：
- `MAVEN_HOME = C:\java\apache-maven-3.9.6`
- `PATH` 包含 `%MAVEN_HOME%\bin`

**已配置阿里云镜像**，依赖下载更快。配置位置：`C:\java\apache-maven-3.9.6\conf\settings.xml`

验证：
```bash
mvn -version
```

### Playwright 环境变量

已配置用户级环境变量：
- `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = 1`（跳过自动下载其他浏览器）

## 安装 Chromium 浏览器

Playwright 依赖 Chromium，执行一次即可：

```bash
cd backend

# 在 PowerShell 中设置环境变量后执行
$env:JAVA_HOME = 'C:\java\jdk-17'
$env:MAVEN_HOME = 'C:\java\apache-maven-3.9.6'
$env:PATH = "$env:PATH;C:\java\jdk-17\bin;C:\java\apache-maven-3.9.6\bin"
$env:PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = '1'

mvn exec:java -e '-Dexec.mainClass=com.microsoft.playwright.CLI' '-Dexec.args=install chromium'
```

Chromium 将安装在 `%USERPROFILE%\AppData\Local\ms-playwright\chromium-1091`

## 启动项目

### 1. 启动后端（端口 8080）

```bash
cd backend

# PowerShell 中先设置环境变量
$env:JAVA_HOME = 'C:\java\jdk-17'
$env:MAVEN_HOME = 'C:\java\apache-maven-3.9.6'
$env:PATH = "$env:PATH;C:\java\jdk-17\bin;C:\java\apache-maven-3.9.6\bin"
$env:PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = '1'

mvn spring-boot:run
```

日志出现 `Tomcat started on port 8080` 和 `Playwright 浏览器初始化完成` 即表示成功。

### 2. 测试下载

**方式一：浏览器打开 demo.html**

直接用浏览器打开 `demo.html` 文件，点击下载按钮。

**方式二：curl 命令**

```bash
curl http://localhost:8080/api/schedule/report/download -o report.pdf
```

**方式三：浏览器地址栏直接访问**

```
http://localhost:8080/api/schedule/report/download
```

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/schedule/report-data` | GET | 获取报表 JSON 数据（调试用） |
| `/api/schedule/report` | POST | 接收 JSON 数据生成 PDF |
| `/api/schedule/report/download` | GET | **一键生成并下载 PDF**（推荐） |

## 工作原理

### 核心流程

```
用户触发下载
    ↓
后端生成数据
    ↓
Thymeleaf 渲染 HTML 模板（report.html）
    ↓  模板中嵌入 ECharts CDN，JS 渲染图表
    ↓  document.body.setAttribute('data-charts-ready', 'true')
Playwright 启动 Chromium（无头模式）
    ↓
等待页面加载完成 + ECharts 渲染完成
    ↓
生成 A4 尺寸 PDF
    ↓
返回 PDF 文件流
```

### 核心代码

**Playwright 生成 PDF（ReportService.java）**：

```java
// 1. Thymeleaf 渲染 HTML
String html = templateEngine.process("report", context);

// 2. Playwright 截图生成 PDF
Page page = browserContext.newPage();
page.setContent(html);
page.waitForLoadState(LoadState.NETWORKIDLE);

// 3. 等待 ECharts 渲染完成
page.waitForFunction("document.body.getAttribute('data-charts-ready') === 'true'");
page.waitForTimeout(800);

// 4. 输出 PDF
byte[] pdf = page.pdf(new Page.PdfOptions()
    .setFormat("A4")
    .setPrintBackground(true)
);
```

**中文文件名编码（ReportController.java）**：

```java
String filename = URLEncoder.encode("销售报表_xxx.pdf", StandardCharsets.UTF_8)
    .replace("+", "%20");
headers.add(HttpHeaders.CONTENT_DISPOSITION, 
    "attachment; filename*=UTF-8''" + filename);
```

## 自定义扩展

### 修改报表模板

编辑 `backend/src/main/resources/templates/report.html`：

- 修改 CSS 样式
- 调整 ECharts 图表类型和配置
- 添加/删除表格列

### 替换为真实数据

修改 `ReportService.generateMockData()`，接入数据库：

```java
// 原来是生成模拟数据
public ReportData generateMockData() { ... }

// 改为从数据库查询
@Autowired
private SalesRecordRepository repository;

public ReportData generateReportData(LocalDate start, LocalDate end) {
    List<SalesRecord> records = repository.findByDateBetween(start, end);
    // ... 组装 ReportData
}
```

### 添加更多 ECharts 图表

在 `report.html` 中：

1. 添加 `<div id="xxxChart" class="chart-container"></div>`
2. 在 `<script>` 中初始化 `echarts.init()`
3. 最后设置 `document.body.setAttribute('data-charts-ready', 'true')`

### 调整 PDF 页面尺寸

修改 `ReportService.java`：

```java
page.pdf(new Page.PdfOptions()
    .setFormat("A4")        // A4, Letter, Legal 等
    .setLandscape(true)     // 横向
    .setMargin(new Margin()
        .setTop("15mm")
        .setBottom("15mm")
        .setLeft("12mm")
        .setRight("12mm"))
);
```

## 常见问题

### Q: 后端启动时提示 mvn 不是命令

PowerShell 需要设置环境变量（见「启动后端」步骤），或者**重启 PowerShell** 使环境变量生效。

### Q: Playwright 尝试下载 Firefox/WebKit

已配置 `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1`，只使用已安装的 Chromium。

### Q: 跨域报错

CorsConfig 已配置 `allowedOriginPatterns("*")`，任意前端均可访问。

### Q: 中文文件名乱码

已使用 `filename*=UTF-8''` RFC 5987 编码，支持所有浏览器。

### Q: 前端可以是任何技术栈吗？

是的。前端只需要发送一个 HTTP GET 请求到 `/api/schedule/report/download`，可以是：
- Vue / React / Angular
- 纯 HTML（如 demo.html）
- 小程序、App
- 甚至直接 curl 命令
