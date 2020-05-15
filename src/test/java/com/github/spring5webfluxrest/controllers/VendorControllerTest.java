package com.github.spring5webfluxrest.controllers;

import com.github.spring5webfluxrest.domain.Vendor;
import com.github.spring5webfluxrest.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

class VendorControllerTest {

    private static final String ID = "Id";
    private static final String VENDOR_API_URI = "/api/v1/vendors";
    @Mock
    VendorRepository vendorRepository;

    VendorController vendorController;

    WebTestClient webTestClient;

    Vendor vendor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        vendorController = new VendorController(vendorRepository);

        webTestClient = WebTestClient.bindToController(vendorController).build();

        vendor = Vendor.builder().build();
    }

    @Test
    void listTest() {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(
                        Vendor.builder().firstName("John").lastName("Nowak").build(),
                        Vendor.builder().firstName("Ann").lastName("Kowal").build()
                ));
        webTestClient.get().uri(VENDOR_API_URI)
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        Vendor vendor = Vendor.builder().id(ID).build();

        BDDMockito.given(vendorRepository.findById(ID))
                .willReturn(Mono.just(vendor));

        webTestClient.get().uri(VENDOR_API_URI + "/" + ID)
                .exchange()
                .expectBody(Vendor.class)
                .isEqualTo(vendor);
    }

    @Test
    public void testCreateCateogry() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(vendor));

        Mono<Vendor> vendorToSaveMono = Mono.just(vendor);

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void updateTest() {
        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(vendor));

        Mono<Vendor> vendorToUpdateMono = Mono.just(vendor);

        webTestClient.put()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

    }
}