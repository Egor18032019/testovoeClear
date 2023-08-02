package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CrptApi {
    private final String URL = "https://ismp.crpt.ru/api/v3/lk/documents/commissioning/contract/create"; //47 страница

    /*
    на 47 страницы url
    а на 108 шаблон документа
    а на 44 описан общий случай
    То есть на /api/v3/lk/documents/create это общий
    а на /api/v3/lk/documents/commissioning/contract/create это частность
     */
    private final String Signature = "signature";

    /**
     * положительное значение, которое определяет максимальное количество запросов в этом промежутке времени.
     */
    private final int requestLimit;
    /**
     * указывает промежуток времени – секунда, минута и пр.
     */
    private final TimeUnit timeUnit;
    /**
     * счетчик количество запросов
     */
    private int requestCount;
    /**
     * время стартового запроса
     */
    private Long startRequest;
    private final Lock lock = new ReentrantLock();

    private CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        if (requestLimit > 0) {
            this.requestLimit = requestLimit;
            this.requestCount = 0;
            this.startRequest = System.currentTimeMillis();
        } else {
            throw new IllegalArgumentException("Ноль запросов или отрицательное число запросов ?");
        }
    }

    private static volatile CrptApi instance;

    public static CrptApi getInstance(TimeUnit timeUnit, int requestLimit) {
        CrptApi localInstance = instance;
        if (localInstance == null) {
            synchronized (CrptApi.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new CrptApi(timeUnit, requestLimit);
                }
            }
        }
        return localInstance;
    }


    //Реализовать нужно единственный метод – Создание документа для ввода в оборот
    public synchronized void createDocument(DocumentFormat documentFormat,
                                            Document document,
                                            String productGroup,
                                            String signature,
                                            String type) throws InterruptedException {
        checkRequestLimit();
        try {
            lock.lock();
            ObjectMapper objectMapper = new ObjectMapper();
            String product_document = Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(document));
            String requestBody = String.format("{ " +
                    "\"document_format\":\"%s\"," +
                    "\"product_document\":\"%s\"," +
                    "\"product_group\":\"%s\"," +
                    "\"signature\":\"%s\"," +
                    "\"type\":\"%s\"" +
                    "}", documentFormat, product_document, productGroup, signature, type);
            httpRequest(requestBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Отправка
     *
     * @param json Тело запроса страница 44 только product_document меняем на Document со 108 страницы
     */
    private void httpRequest(String json) {
//        При реализации можно использовать библиотеки HTTP клиента,
        // это =>     <groupId>org.apache.httpcomponents</groupId>
        //            <artifactId>httpclient</artifactId>
        // или какая то другая ?

        try {
            HttpPost post = new HttpPost(URL);
            StringEntity entity = new StringEntity(json);
            post.addHeader("content-type", "application/json");

            post.setEntity(entity);
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            httpClient.execute(post);
            httpClient.close();
            requestCount++;
            System.out.println("отправил");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Проверка на лимит и на время
     *
     * @throws InterruptedException
     */
    private void checkRequestLimit() throws InterruptedException {
        // В одну секунду не больше 5 запросов
        System.out.println("checkRequestLimit");
        final long current = System.currentTimeMillis(); // время текущего запроса
        // проверка на время запросов
        if (current - startRequest >= timeUnit.toMillis(1)) {
            startRequest = current;
            requestCount = 0;
        }


        if (requestCount >= requestLimit) {
            // проверяем на кол-во и если больше то
            // смотрим сколько времени осталось до лимита и на остаток ложим спать

            final int difference = (int) (current - startRequest);

            if (difference <= timeUnit.toMillis(1)) {
                final long toSleep = timeUnit.toMillis(1) - difference;
                System.out.println("toSleep " + toSleep);
                Thread.sleep(toSleep);
//                При превышении лимита запрос должен блокироваться, чтобы не превысить
//                максимальное количество запросов к API и продолжить выполнение, когда ограничение
//                не превышено.
            }
        }
    }

    public synchronized void createDocument(Main.DocumentFormat documentFormat, Main.Document document, String productGroup, String signature, String type) throws InterruptedException {
        checkRequestLimit();
        try {
            lock.lock();
            ObjectMapper objectMapper = new ObjectMapper();
            String product_document = Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(document));
            String requestBody = String.format("{ " +
                    "\"document_format\":\"%s\"," +
                    "\"product_document\":\"%s\"," +
                    "\"product_group\":\"%s\"," +
                    "\"signature\":\"%s\"," +
                    "\"type\":\"%s\"" +
                    "}", documentFormat, product_document, productGroup, signature, type);
            httpRequest(requestBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

    }

    /**
     * 108 страница
     */
    public class Document {
        private Description description; // не обязательный параметр
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private Boolean importRequest = true; // не обязательный параметр но в примере стоит true
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private Date production_date;
        // формат 2020-01-23
        private String production_type;
        private List<Product> products;// не обязательный параметр
        private Date reg_date; // Автоматически присваивается при регистрации
        // то есть мы тут null передаем, а система потом присваивает
        // формат 2020-01-23
        @Expose
        private String reg_number; //Генерируется автоматически при регистрации документа
        // не присваивается, а именно генерируется. Значит не надо передавать это поле

        public Document(Description description,
                        String doc_id,
                        String doc_status,
                        String doc_type,
                        Boolean importRequest,
                        String owner_inn,
                        String participant_inn,
                        String producer_inn,
                        Date production_date,
                        String production_type,
                        List<Product> products,
                        Date reg_date,
                        String reg_number) {
            this.description = description;
            this.doc_id = doc_id;
            this.doc_status = doc_status;
            this.doc_type = doc_type;
            this.importRequest = importRequest;
            this.owner_inn = owner_inn;
            this.participant_inn = participant_inn;
            this.producer_inn = producer_inn;
            this.production_date = production_date;
            this.production_type = production_type;
            this.products = products;
            this.reg_date = reg_date;
            this.reg_number = reg_number;
        }

        public Description getDescription() {
            return description;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public Boolean getImportRequest() {
            return importRequest;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public Date getProduction_date() {
            return production_date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public List<Product> getProducts() {
            return products;
        }

        public Date getReg_date() {
            return reg_date;
        }

        public String getReg_number() {
            return reg_number;
        }
    }

    public class Description {
        private String participantInn;

        public Description(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }
    }

    public class Product {
        private String certificate_document; // не обязательный параметр
        private Date certificate_document_date; // не обязательный параметр
        private String certificate_document_number; // не обязательный параметр
        private String owner_inn;
        private String producer_inn;
        private Date production_date;
        private String tnved_code; // Обязательный, если не указан uitu
        private String uit_code; // Обязательный, если не указан uitu
        private String uitu_code; // Обязательный, если не указан uit

        public Product(String certificate_document,
                       Date certificate_document_date,
                       String certificate_document_number,
                       String owner_inn,
                       String producer_inn,
                       Date production_date,
                       String tnved_code,
                       String uit_code,
                       String uitu_code) {
            this.certificate_document = certificate_document;
            this.certificate_document_date = certificate_document_date;
            this.certificate_document_number = certificate_document_number;
            this.owner_inn = owner_inn;
            this.producer_inn = producer_inn;
            this.production_date = production_date;
            this.tnved_code = tnved_code;
            this.uit_code = uit_code;
            this.uitu_code = uitu_code;
        }

        public String getCertificate_document() {
            return certificate_document;
        }

        public Date getCertificate_document_date() {
            return certificate_document_date;
        }

        public String getCertificate_document_number() {
            return certificate_document_number;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public Date getProduction_date() {
            return production_date;
        }

        public String getTnved_code() {
            return tnved_code;
        }

        public String getUit_code() {
            return uit_code;
        }

        public String getUitu_code() {
            return uitu_code;
        }
    }

    public enum DocumentFormat {
        MANUAL,
        XML,
        CSV;
    }
}
