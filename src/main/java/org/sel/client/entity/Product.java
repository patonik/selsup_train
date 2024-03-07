package org.sel.client.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Product {
    public enum CerType {
        CONFORMITY_CERTIFICATE, CONFORMITY_DECLARATION
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private CerType certificateDocument;
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private String certificateDocumentDate;
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private String certificateDocumentNumber;
    private String ownerInn;
    private String producerInn;
    private String productionDate;
    private String tnvedCode;
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private String uitCode;
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private String uituCode;

    private Product() {
    }

    public static ProductBuilder newProductBuilder() {
        return new Product().new ProductBuilder();
    }

    public CerType getCertificateDocument() {
        return certificateDocument;
    }

    public String getCertificateDocumentDate() {
        return certificateDocumentDate;
    }

    public String getCertificateDocumentNumber() {
        return certificateDocumentNumber;
    }

    public String getOwnerInn() {
        return ownerInn;
    }

    public String getProducerInn() {
        return producerInn;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public String getTnvedCode() {
        return tnvedCode;
    }

    public String getUitCode() {
        return uitCode;
    }

    public String getUituCode() {
        return uituCode;
    }

    public class ProductBuilder {
        private ProductBuilder() {
        }

        public ProductBuilder setCertificateDocument(CerType certificateDocument) {
            Product.this.certificateDocument = certificateDocument;
            return this;
        }

        public ProductBuilder setCertificateDocumentDate(LocalDate certificateDocumentDate) {
            Product.this.certificateDocumentDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(certificateDocumentDate);
            return this;
        }

        public ProductBuilder setCertificateDocumentNumber(String certificateDocumentNumber) {
            Product.this.certificateDocumentNumber = certificateDocumentNumber;
            return this;
        }

        public ProductBuilder setOwnerInn(String ownerInn) {
            Product.this.ownerInn = ownerInn;
            return this;
        }

        public ProductBuilder setProducerInn(String producerInn) {
            Product.this.producerInn = producerInn;
            return this;
        }

        public ProductBuilder setProductionDate(LocalDate productionDate) {
            Product.this.productionDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(productionDate);
            return this;
        }

        public ProductBuilder setTnvedCode(String tnvedCode) {
            Product.this.tnvedCode = tnvedCode;
            return this;
        }

        public ProductBuilder setUitCode(String uitCode) {
            Product.this.uitCode = uitCode;
            return this;
        }

        public ProductBuilder setUituCode(String uituCode) {
            Product.this.uituCode = uituCode;
            return this;
        }

        public Product build() {
            return Product.this;
        }
    }
}
