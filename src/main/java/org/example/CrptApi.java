package org.example;


import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CrptApi {

    private final String URL = "/api/v3/lk/documents/commissioning/contract/create"; //47 страница
    /*
    на 47 страницы url
    а на 108 шаблон документа
     */
    private final String CLIENT_TOKEN = "clientToken";
    private final String USER_NAME = "userName";
    // коллекция в которую будем добавлять время обращения и в итоге будем сравнивать длину коллекции и с requestLimit
    // или создать отдельную переменную requestCount и startRequest немного меньше памяти будет занимать
    private final int requestLimit; // положительное значение, которое определяет максимальное количество запросов в этом промежутке времени.
    private final TimeUnit timeUnit; //timeUnit – указывает промежуток времени – секунда, минута и пр.
    private int requestCount; // счетчик количество запросов
    private Long startRequest; // время стартового запроса
    private final Lock lock = new ReentrantLock();

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        if (requestLimit > 0) {
            this.requestLimit = requestLimit;
            this.requestCount = 0;
            this.startRequest = 0L;
        } else {
            throw new IllegalArgumentException("Ноль запросов или отрицательное число запросов ?");
        }
    }

    public synchronized void createDocument(Document document, String sub) throws InterruptedException {
        checkRequestLimit();
        try {
            lock.lock();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    //Реализовать нужно единственный метод – Создание документа для ввода в оборот
    //??? данные для документа откуда брать или он сам приходить откуда-то ??

    /**
     * Проверка на лимит и на время
     *
     * @throws InterruptedException
     */
    private void checkRequestLimit() throws InterruptedException {
        final long current = System.currentTimeMillis(); // время текущего запроса


        // проверка на время запросов
        if (current - startRequest >= timeUnit.toMillis(1)) {
            // если текущее время минус время последнего запроса больше чем заданный интервал, то чистим очередь и добавляем
            startRequest = current;
            requestCount = 0;

        }


        if (requestCount >= requestLimit) {
            // проверка на количество запросов

            final int difference = (int) (current - startRequest);

            if (difference <= timeUnit.toMillis(1)) {
                final long toSleep = timeUnit.toMillis(1) - difference;
                Thread.sleep(toSleep);
//                При превышении лимита запрос должен блокироваться, чтобы не превысить
//                максимальное количество запросов к API и продолжить выполнение, когда ограничение
//                не превышено.
            }
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
        private Boolean importRequest = true;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private Date production_date;
        // формат 2020-01-23
        private String production_type;
        private List<Product> products;// не обязательный параметр
        private Date reg_date; // Автоматически присваивается при регистрации
        // то есть мы тут null передаем а система потом присваивает
        // формат 2020-01-23
        @Expose
        private String reg_number; //Генерируется автоматически при регистрации документа
        // не присваеватся, а именно генерируется .Значит не надо передавать это поле

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
}
