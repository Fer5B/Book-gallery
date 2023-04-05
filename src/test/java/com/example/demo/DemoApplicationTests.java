package com.example.demo;

import com.example.demo.controller.BookController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private BookController bookController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(bookController);
	}

}
