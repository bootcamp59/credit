package com.bootcamp.credit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {


    private String id;

    private String name;
    private CustomerType type;

    @Field("doc_type")
    private String docType;

    @Field("doc_number")
    private String docNumber;


    public enum CustomerType {
        PERSONAL, EMPRESARIAL
    }
}
