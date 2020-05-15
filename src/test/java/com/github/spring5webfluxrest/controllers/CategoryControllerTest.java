package com.github.spring5webfluxrest.controllers;

import com.github.spring5webfluxrest.domain.Category;
import com.github.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CategoryControllerTest {

    private static final String ID = "Id";
    private static final String API_URI = CategoryController.CATEGORY_API_URI;
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
        given(categoryRepository.findAll())
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

        given(categoryRepository.findById(ID))
                .willReturn(Mono.just(category));

        webTestClient.get().uri(API_URI + "/" + ID)
                .exchange()
                .expectBody(Category.class)
                .isEqualTo(category);
    }

    @Test
    public void testCreateCateogry() {
        given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().description("descrp").build()));

        Mono<Category> catToSaveMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.post()
                .uri(API_URI)
                .body(catToSaveMono, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void updateTest() {
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().build());

        webTestClient.put()
                .uri(API_URI + "/someId")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    public void testPatchWithChanges() {
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().build()));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("New Description").build());

        webTestClient.patch()
                .uri(API_URI + "/asdfasdf")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository).save(any());
    }

    @Test
    public void testPatchNoChanges() {
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().build()));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> catToUpdateMono = Mono.just(Category.builder().build());

        webTestClient.patch()
                .uri(API_URI + "/asdfasdf")
                .body(catToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository, never()).save(any());
    }
}