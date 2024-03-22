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
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

final public class ApiClient {
    private final TimeUnit timeUnit;
    private final URI uri;
    private final HttpClient httpClient;
    //ApiClient object is used to control throughput.
    private static ApiClient instance;
    private final Logger logger;
    private final Timer timer = new Timer(true);
    private final Semaphore semaphore;

    private ApiClient(TimeUnit timeUnit, int requestLimit, URI uri, HttpClient httpClient) {
        this.timeUnit = timeUnit;
        this.semaphore = new Semaphore(requestLimit);
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

    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(40);
        List<Callable<CompletableFuture<HttpResponse<byte[]>>>> callableList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            callableList.add(new ApiTask());
        }
        List<Future<CompletableFuture<HttpResponse<byte[]>>>> futureList = executorService.invokeAll(callableList);
        for (Future<CompletableFuture<HttpResponse<byte[]>>> completableFutureFuture : futureList) {
            ApiClient.getInstance().logger.log(Level.INFO, completableFutureFuture.get().get().statusCode() + ", " + LocalTime.now());
        }
        executorService.shutdown();

    }

    private static class ApiTask implements Callable<CompletableFuture<HttpResponse<byte[]>>> {

        @Override
        public CompletableFuture<HttpResponse<byte[]>> call() throws Exception {
            ApiClient apiClient = ApiClient.getInstance();
            List<Product> productList = new ArrayList<>();
            productList.add(Product.newProductBuilder().build());
            String doc = apiClient
                    .marshallDoc(MarshallType.JSON, Doc
                            .newDocBuilder()
                            .setProducts(productList)
                            .setDescription("fdfdf")
                            .build());
            HttpRequest httpRequest = HttpRequest.
                    newBuilder(apiClient.uri).
                    header("accept", "*/*").
                    method("POST", HttpRequest.BodyPublishers.ofString(Objects.requireNonNull(doc))).
                    build();
            apiClient.semaphore.acquire();
            apiClient.logger.log(Level.INFO, "semaphore acquired by ApiTask, " + Thread.currentThread().getName()
                    + ": " + LocalTime.now());
            apiClient.timer.schedule(new ApiTimerTask(), apiClient.timeUnit.toMillis(1));
            return apiClient.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        }
    }

    private static class ApiTimerTask extends TimerTask {

        @Override
        public void run() {
            ApiClient apiClient;
            try {
                apiClient = ApiClient.getInstance();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (apiClient != null) {
                apiClient.semaphore.release();
                apiClient.logger.log(Level.INFO
                        , "semaphore released by TimerTask, "
                                + Thread.currentThread().getName()
                                + ": "
                                + LocalTime.now());
            }
        }
    }
}
