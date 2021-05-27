package com.ptit.etax.service;

import com.ptit.etax.common.error.ConflictException;
import com.ptit.etax.common.error.NotFoundException;
import com.ptit.etax.controller.request.UserRequest;
import com.ptit.etax.model.User;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 - Phạm vi test: Tầng nghiệp vụ của User liên quan đến định danh MST (Tạo MST,Định danh sử dụng MST)
 - Mục đích: Test những nghiệp vụ riêng (VD khi tạo thì trường nào để mặc định, mặc định với gtri nào ...)
 * */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserServiceTest {
	/**
	 * Khởi tạo những bean cần thiết cho quá trình khởi tạo context
	 */
	@TestConfiguration
	public static class Configuration {
		@Bean
		UserService userService() {
			return new UserService();
		}

		@Bean
		GenericMapper genericMapper() {
			return new GenericMapper();
		}
	}

	public static final String DEFAULT_ID = "a";



	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	/**
	 * Code 201
	 * Test tạo user chưa tồn tại trong db
	 * Input: UserCreate
	 * Expected output: User;
	 * */
	@Test
	public void testCreateOK(){
		// Input
		String id = DEFAULT_ID;
		UserRequest.UserCreate userCreate = new UserRequest.UserCreate();
		userCreate.setId(id);

		// Output
		User output = userService.create(userCreate);

		// Validate
		assertEquals(id, output.getId());
	}

	/**
	 * Code 409
	 * Test tạo MST đã tồn tại trong db
	 * Input: UserCreate
	 * Expected output: ConflictException("Mã số thuế đã tồn tại");
	 * */
	@Test
	public void testCreateCode409(){
		// Init database
		User initUser = new User();
		initUser.setId(DEFAULT_ID);
		userRepository.save(initUser);

		// Input
		String id = DEFAULT_ID;
		UserRequest.UserCreate userCreate = new UserRequest.UserCreate();
		userCreate.setId(id);

		// Output


		// Validate
		// param 1 : expected class exception
		// param 2 : code block will throw ConflictException
		// param 3 : message when exception
		assertThrows(ConflictException.class, () -> userService.create(userCreate), "Mã số thuế đã tồn tại");
	}

	/**
	 * Code 404
	 * Test login với MST chưa tồn tại trong db
	 * Input: UserLogin
	 * Expected output: NotFoundException("Mã số thuế chưa tồn tại");
	 * */
	@Test
	public void testLoginCode404() {
		// Inout
		UserRequest.UserLogin userLogin = new UserRequest.UserLogin();
		userLogin.setId(DEFAULT_ID);

		// Validate
		// param 1 : expected class exception
		// param 2 : code block will throw ConflictException
		// param 3 : message when exception
		assertThrows(NotFoundException.class, () -> userService.login(userLogin), "Mã số thuế chưa tồn tại");
	}

	/**
	 * Code 200
	 * Test login với MST đã tồn tại trong db
	 * Input: UserLogin
	 * Expected output: NotFoundException("Mã số thuế chưa tồn tại");
	 * */
	@Test
	public void testLoginCode200() {
		// Init database
		User initUser = new User();
		initUser.setId(DEFAULT_ID);
		userRepository.save(initUser);

		// Input
		UserRequest.UserLogin userLogin = new UserRequest.UserLogin();
		userLogin.setId(DEFAULT_ID);

		// Output
		User output = userService.login(userLogin);

		// Validate
		assertEquals(DEFAULT_ID,output.getId());
	}


	/**
	 * Sau mỗi test sẽ chạy vào hàm này để clear data test
	 * */
	@AfterEach
	public void clearDatabase(){
		userRepository.deleteAll();
	}
}
