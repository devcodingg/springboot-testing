package com.ntloc.demo.customer;

public record CreateCustomerRequest(String name,
                                    String email,
                                    String address) {
}
