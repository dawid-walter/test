package com.dwalter.bookaro.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Recipient {
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;
}
