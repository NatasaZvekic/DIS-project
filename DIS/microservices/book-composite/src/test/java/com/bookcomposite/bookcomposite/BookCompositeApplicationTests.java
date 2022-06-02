//package com.bookcomposite.bookcomposite;
//
//import com.bookcomposite.bookcomposite.services.BookCompositeIntegration;
//import composite.book.BookAggregate;
//import core.book.Book;
//import core.comments.Comment;
//import core.rates.Rate;
//import core.readers.Reader;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
//import static java.util.Collections.singletonList;
//import static org.mockito.Mockito.when;
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
//import static org.springframework.http.HttpStatus.OK;
//import static reactor.core.publisher.Mono.just;
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment=RANDOM_PORT)
//public class BookCompositeApplicationTests {
//
//	private static final int BOOK_ID_OK = 1;
//	@Autowired
//	private WebTestClient client;
//
//	@Test
//	public void contextLoads() {
//	}
//
//	@MockBean
//	private BookCompositeIntegration compositeIntegration;
//
//	@Before
//	public void setUp() {
//
//		when(compositeIntegration.getBook(BOOK_ID_OK)).
//				thenReturn(new Book(BOOK_ID_OK, "name"));
//		when(compositeIntegration.getComments(BOOK_ID_OK)).
//				thenReturn(singletonList(new Comment(BOOK_ID_OK, 1, "comment")));
//		when(compositeIntegration.getRate(BOOK_ID_OK)).
//				thenReturn(singletonList(new Rate(BOOK_ID_OK, 1, 4)));
//		when(compositeIntegration.getReader(BOOK_ID_OK)).
//				thenReturn(singletonList(new Reader(BOOK_ID_OK, 1, "firstName", "lastName")));
//	}
//
//	@Test
//	public void createCompositeBook() {
//		BookAggregate compositeBook = new BookAggregate(145, null, null, null, "name");
//		postAndVerifyBook(compositeBook, OK);
//	}
//
//	private void postAndVerifyBook(BookAggregate compositeBook, HttpStatus expectedStatus) {
//		client.post()
//				.uri("/book-composite")
//				.body(just(compositeBook), BookAggregate.class)
//				.exchange()
//				.expectStatus().isEqualTo(expectedStatus);
//	}
//
//	@Test
//	public void deleteCompositeBook() {
//		BookAggregate bookAggregate = new BookAggregate(1,
//				singletonList(new Comment(1,4, "a")),
//				singletonList(new Reader(1, 34, "s", "c")),
//				singletonList(new Rate(1, 43, 1)), "name");
//
//		postAndVerifyBook(bookAggregate, OK);
//
//		deleteAndVerifyBook(bookAggregate.getBookId(), OK);
//	}
//
//	private void deleteAndVerifyBook(int bookId, HttpStatus expectedStatus) {
//		client.delete()
//				.uri("/book-composite/" + bookId)
//				.exchange()
//				.expectStatus().isEqualTo(expectedStatus);
//	}
//
//}
