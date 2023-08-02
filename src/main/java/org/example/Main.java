//package org.example;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.google.gson.annotations.Expose;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public class Main {
//    public static void main(String[] args) {
//        System.out.println("Hello world!");
//        CrptApi crptApi = CrptApi.getInstance(TimeUnit.MINUTES, 1);
//        Description description = new Description("getParticipantInn");
//        Product product = new Product("certificate_document", Date.from(Instant.now()), "certificate_document_number", "owner_inn", "producer_inn", Date.from(Instant.now()), "tnved_code", "uit_code", "uitu_code");
//        List<Product> products = new ArrayList<>();
//        products.add(product);
//        Document document = new Document(description, "doc_id", "doc_status", "doc_type", true, "owner_inn", "participant_inn", "producer_inn", Date.from(Instant.now()), "production_type", products, Date.from(Instant.now()), "reg_number");
//        try {
//            crptApi.createDocument( DocumentFormat.LP_INTRODUCE_GOODS_XML, document, "product_group", "signature", "type");
//            crptApi.createDocument( DocumentFormat.LP_INTRODUCE_GOODS_XML, document, "product_group", "signature", "type");
//            crptApi.createDocument( DocumentFormat.LP_INTRODUCE_GOODS_XML, document, "product_group", "signature", "type");
//            crptApi.createDocument( DocumentFormat.LP_INTRODUCE_GOODS_XML, document, "product_group", "signature", "type");
//            crptApi.createDocument( DocumentFormat.LP_INTRODUCE_GOODS_CSV, document, "product_group", "signature", "type");
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    public enum DocumentFormat {
//        LP_INTRODUCE_GOODS,
//        LP_INTRODUCE_GOODS_CSV,
//        LP_INTRODUCE_GOODS_XML;
//    }
//
//    public static class Document {
//        @JsonProperty(required = false)
//        private Description description; // не обязательный параметр
//        private String doc_id;
//        private String doc_status;
//        private String doc_type;
//        @JsonProperty(required = false)
//        private Boolean importRequest = true; // не обязательный параметр но в примере стоит true
//        private String owner_inn;
//        private String participant_inn;
//        private String producer_inn;
//        private Date production_date;
//        // формат 2020-01-23
//        private String production_type;
//        private List<Product> products;// не обязательный параметр
//        private Date reg_date; // Автоматически присваивается при регистрации
//        // то есть мы тут null передаем, а система потом присваивает
//        // формат 2020-01-23
//        @Expose
//        private String reg_number; //Генерируется автоматически при регистрации документа
//        // Не присваивается, а именно генерируется. Значит не надо передавать это поле
//
//        public Document(Description description,
//                        String doc_id,
//                        String doc_status,
//                        String doc_type,
//                        Boolean importRequest,
//                        String owner_inn,
//                        String participant_inn,
//                        String producer_inn,
//                        Date production_date,
//                        String production_type,
//                        List<Product> products,
//                        Date reg_date,
//                        String reg_number) {
//            this.description = description;
//            this.doc_id = doc_id;
//            this.doc_status = doc_status;
//            this.doc_type = doc_type;
//            this.importRequest = importRequest;
//            this.owner_inn = owner_inn;
//            this.participant_inn = participant_inn;
//            this.producer_inn = producer_inn;
//            this.production_date = production_date;
//            this.production_type = production_type;
//            this.products = products;
//            this.reg_date = reg_date;
//            this.reg_number = reg_number;
//        }
//
//        public Description getDescription() {
//            return description;
//        }
//
//        public String getDoc_id() {
//            return doc_id;
//        }
//
//        public String getDoc_status() {
//            return doc_status;
//        }
//
//        public String getDoc_type() {
//            return doc_type;
//        }
//
//        public Boolean getImportRequest() {
//            return importRequest;
//        }
//
//        public String getOwner_inn() {
//            return owner_inn;
//        }
//
//        public String getParticipant_inn() {
//            return participant_inn;
//        }
//
//        public String getProducer_inn() {
//            return producer_inn;
//        }
//
//        public Date getProduction_date() {
//            return production_date;
//        }
//
//        public String getProduction_type() {
//            return production_type;
//        }
//
//        public List<Product> getProducts() {
//            return products;
//        }
//
//        public Date getReg_date() {
//            return reg_date;
//        }
//
//        public String getReg_number() {
//            return reg_number;
//        }
//    }
//
//    public static class Description {
//        private String participantInn;
//
//        public Description(String participantInn) {
//            this.participantInn = participantInn;
//        }
//
//        public String getParticipantInn() {
//            return participantInn;
//        }
//    }
//
//    public static class Product {
//        private String certificate_document; // не обязательный параметр
//        private Date certificate_document_date; // не обязательный параметр
//        private String certificate_document_number; // не обязательный параметр
//        private String owner_inn;
//        private String producer_inn;
//        private Date production_date;
//        private String tnved_code; // Обязательный, если не указан uitu
//        private String uit_code; // Обязательный, если не указан uitu
//        private String uitu_code; // Обязательный, если не указан uit
//
//        public Product(String certificate_document,
//                       Date certificate_document_date,
//                       String certificate_document_number,
//                       String owner_inn,
//                       String producer_inn,
//                       Date production_date,
//                       String tnved_code,
//                       String uit_code,
//                       String uitu_code) {
//            this.certificate_document = certificate_document;
//            this.certificate_document_date = certificate_document_date;
//            this.certificate_document_number = certificate_document_number;
//            this.owner_inn = owner_inn;
//            this.producer_inn = producer_inn;
//            this.production_date = production_date;
//            this.tnved_code = tnved_code;
//            this.uit_code = uit_code;
//            this.uitu_code = uitu_code;
//        }
//
//        public String getCertificate_document() {
//            return certificate_document;
//        }
//
//        public Date getCertificate_document_date() {
//            return certificate_document_date;
//        }
//
//        public String getCertificate_document_number() {
//            return certificate_document_number;
//        }
//
//        public String getOwner_inn() {
//            return owner_inn;
//        }
//
//        public String getProducer_inn() {
//            return producer_inn;
//        }
//
//        public Date getProduction_date() {
//            return production_date;
//        }
//
//        public String getTnved_code() {
//            return tnved_code;
//        }
//
//        public String getUit_code() {
//            return uit_code;
//        }
//
//        public String getUitu_code() {
//            return uitu_code;
//        }
//    }
//}