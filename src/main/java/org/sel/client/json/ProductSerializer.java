package org.sel.client.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.sel.client.entity.Product;

import java.io.IOException;

public class ProductSerializer extends StdSerializer<Product> {
    public ProductSerializer() {
        this(null);
    }

    public ProductSerializer(Class<Product> t) {
        super(t);
    }

    @Override
    public void serialize(Product product, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        String certDoc = product.getCertificateDocument().toString();
        if (certDoc != null) jsonGenerator.writeStringField("certificate_document", certDoc);
        String certDocDate = product.getCertificateDocumentDate();
        if (certDocDate != null) jsonGenerator.writeStringField("certificate_document_date", certDocDate);
        String certDocNum = product.getCertificateDocumentNumber();
        if (certDocNum != null) jsonGenerator.writeStringField("certificate_document_number", certDocNum);
    }
}
