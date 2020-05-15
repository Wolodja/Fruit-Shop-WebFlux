package com.github.spring5webfluxrest.controllers;

import com.github.spring5webfluxrest.domain.Category;
import com.github.spring5webfluxrest.repositories.CategoryRepository;
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

class CategoryControllerTest {

    private static final String ID = "Id";
    private static final String API_URI = "/api/v1/categories";
    WebTestClient webTestClient;

    @Mock
    CategoryRepository categoryRepository;
    CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    public void listTest() {
        BDDMockito.given(categoryRepository.findAll())
                .willReturn(
                        Flux.just(
                                Category.builder().description("Cat1").build(),
                                Category.builder().description("Cat2").build())
                );

        webTestClient.get().uri(API_URI)
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    public void getByIdTest() {
        Category category = Category.builder().description("Cat1").id(ID).build();

        BDDMockito.given(categoryRepository.findById(ID))
                .willReturn(Mono.just(category));

        webTestClient.get().uri(API_URI + "/" + ID)
                .exchange()
                .expectBody(Category.class)
                .isEqualTo(category);
    }

    @Test
    public void testCreateCateogry() {
        BDDMockito.given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().description("descrp").build()));

        Mono<Category> catToSaveMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.post()
                .uri("/api/v1/categories")
                .body(catToSaveMono, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}