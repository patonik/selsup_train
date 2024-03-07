package org.sel.client.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Doc {
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    private static class Description {
        private String participantInn;

        public String getParticipantInn() {
            return participantInn;
        }
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private Description description;
    private String docId;
    private String docStatus;
    private String docType;
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private boolean importRequest;
    private String ownerInn, participantInn, producerInn, productionDate;
    private ProdType productionType;
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private List<Product> products;
    private String regDate;
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private String regNumber;

    public enum ProdType {
        OWN_PRODUCTION, CONTRACT_PRODUCTION
    }

    private Doc() {
    }

    public String getDocId() {
        return docId;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public String getDocType() {
        return docType;
    }

    public boolean isImportRequest() {
        return importRequest;
    }

    public String getOwnerInn() {
        return ownerInn;
    }

    public String getParticipantInn() {
        return participantInn;
    }

    public String getProducerInn() {
        return producerInn;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public ProdType getProductionType() {
        return productionType;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getRegDate() {
        return regDate;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public Description getDescription() {
        return description;
    }

    public static DocBuilder newDocBuilder() {
        return new Doc().new DocBuilder();
    }

    public class DocBuilder {
        private DocBuilder() {
        }

        public void setDocId(String docId) {
            Doc.this.docId = docId;
        }

        public DocBuilder setDocStatus(String docStatus) {
            Doc.this.docStatus = docStatus;
            return this;
        }

        public DocBuilder setDocType(String docType) {
            Doc.this.docType = docType;
            return this;
        }

        public DocBuilder setImportRequest(boolean importRequest) {
            Doc.this.importRequest = importRequest;
            return this;
        }

        public DocBuilder setOwnerInn(String ownerInn) {
            Doc.this.ownerInn = ownerInn;
            return this;
        }

        public DocBuilder setParticipantInn(String participantInn) {
            Doc.this.participantInn = participantInn;
            return this;
        }

        public DocBuilder setProducerInn(String producerInn) {
            Doc.this.producerInn = producerInn;
            return this;
        }

        public DocBuilder setProductionDate(String productionDate) {
            Doc.this.productionDate = productionDate;
            return this;
        }

        public DocBuilder setProductionType(ProdType productionType) {
            Doc.this.productionType = productionType;
            return this;
        }

        public DocBuilder setProducts(List<Product> products) {
            Doc.this.products = products;
            return this;
        }

        public DocBuilder setRegDate(String regDate) {
            Doc.this.regDate = regDate;
            return this;
        }

        public DocBuilder setRegNumber(String regNumber) {
            Doc.this.regNumber = regNumber;
            return this;
        }

        public DocBuilder setDescription(String participantInn) {
            Doc.this.description = new Description();
            Doc.this.description.participantInn = participantInn;
            return this;
        }

        public Doc build() {
            return Doc.this;
        }

    }

}
