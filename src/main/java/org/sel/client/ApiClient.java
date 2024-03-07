package org.sel.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sel.client.entity.Doc;
import org.sel.client.entity.Product;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

final public class ApiClient {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final URI uri;
    private final HttpClient httpClient;
    //ApiClient object is used to control throughput.
    private static ApiClient instance;
    private final Logger logger;

    private int requestCount;
    private final long[] releaseTime;

    private ApiClient(TimeUnit timeUnit, int requestLimit, URI uri, HttpClient httpClient) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.releaseTime = new long[requestLimit];
        this.uri = uri;
        this.httpClient = httpClient;
        this.logger = Logger.getAnonymousLogger();
        //logger.addHandler(new ConsoleHandler());
    }

    public static synchronized ApiClient getInstance() throws IOException {
        if (instance == null) {
            String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
            String propPath = rootPath + "ApiClient.prop";
            Properties properties = new Properties();
            properties.load(new FileInputStream(propPath));
            instance = new ApiClient(
                    TimeUnit.valueOf(properties.getProperty("TimeUnit", "SECOND")),
                    Integer.parseInt(properties.getProperty("RequestLimit", "2")),
                    URI.create(properties.getProperty("URI", "https://ismp.crpt.ru/api/v3/lk/documents/create")),
                    buildHttpClient(properties));
        }
        return instance;
    }

    private static HttpClient buildHttpClient(Properties properties) {
        //Include more options if required
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(Long.parseLong(properties.getProperty("TimeOut", "20"))))
                .followRedirects(HttpClient.Redirect.valueOf(properties.getProperty("Redirect", "NEVER")))
                .version(HttpClient.Version.valueOf(properties.getProperty("HttpVersion", "HTTP_2")))
                .build();
    }

    public CompletableFuture<HttpResponse<byte[]>> sendRFDoc(String doc, String ucds) throws IOException, InterruptedException {
        //For future customization
        HttpRequest httpRequest = HttpRequest.
                newBuilder(this.uri).
                header("accept", "*/*").
                method("POST", HttpRequest.BodyPublishers.ofString(doc)).
                build();
        CompletableFuture<HttpResponse<byte[]>> completableFuture;
        synchronized (this) {
            if (requestLimit > 0) {
                requestCount++;
                while (releaseTime[(requestCount - 1) % releaseTime.length] > System.currentTimeMillis()) {
                    long sleepTime = System.currentTimeMillis() - releaseTime[(requestCount - 1) % releaseTime.length];
                    if (sleepTime > 0) this.wait(sleepTime);
                }
                releaseTime[(requestCount - 1) % releaseTime.length] = System.currentTimeMillis() + timeUnit.toMillis(1);
                if (requestCount > requestLimit) {
                    requestCount = 1;
                }
                this.notifyAll();
            }
        }
        completableFuture = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        logger.log(Level.INFO, "Requested: " + LocalTime.now() + ", "
                + LocalTime.ofInstant(Instant.ofEpochMilli(releaseTime[(requestCount - 1) % releaseTime.length]), ZoneId.systemDefault()) + ", "
                + requestCount);
        return completableFuture;
    }

    public enum MarshallType {
        JSON,
        CSV,
        XML
    }

    public String marshallDoc(MarshallType marshallType, Doc doc) throws JsonProcessingException {
        switch (marshallType) {
            case JSON -> {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(doc);
            }
            case CSV -> {
                //TODO
                //CSV document
                return null;
            }
            case XML -> {
                //TODO
                //XML document
                return null;
            }
            default -> {
                return null;
            }
        }
    }

    public static void main(String[] args) {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(40);
        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, blockingQueue);
        Runnable runnable = () -> {
            List<Product> productList = new ArrayList<>();
            productList.add(Product.newProductBuilder().build());
            ApiClient apiClient;
            try {
                apiClient = ApiClient.getInstance();
                String doc = apiClient.marshallDoc(MarshallType.JSON, Doc.newDocBuilder().setProducts(productList).setDescription("fdfdf").build());
                CompletableFuture<HttpResponse<byte[]>> completableFuture = apiClient.sendRFDoc(doc, "signature");
                apiClient.logger.log(Level.INFO, completableFuture.get().statusCode() + ", " + LocalTime.now());
            } catch (IOException | InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };

        for (int i = 0; i < 40; i++) {
            executorService.execute(runnable);
        }
        executorService.shutdown();
    }
}
