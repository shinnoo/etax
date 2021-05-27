package com.ptit.etax.repository;

import com.ptit.etax.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 - Phạm vi test : Tầng thao tác với database liên quan đến User
 - Mục đích test:
 	+ Test sự thay đổi của database mỗi khi có thao tác liên quan (thêm,sửa)
 	+ Test dữ liệu truyền xuống database có hợp lệ hay không (đúng định dạng ko ,trong khoảng hợp lệ ko, .....)
 - Lưu ý:
 	+ Việc test database sẽ sử dụng H2 database (in-memory database)
 	+ Mục đích :
 		. Để đảm bảo không có phụ thuộc giữa môi trường test và database (một số phụ thuộc : cùng db engine, cùng version giữa test và dev/production)
 		. Không làm bất kỳ thay đổi nào đối với dữ liệu của database bên ngoài
*/
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest	// khởi tạo và load tất cả các class liên quan đến database vào context hiện tại
@ActiveProfiles("test")
public class UserRepositoryTest {
	@Autowired	// load từ context ra
	UserRepository userRepository;


	public static final String DEFAULT_ID = "a";

	/**
	 * Test tạo user đủ thuộc tính
	 * Input: User("a")
	 * Expected output: User("a");
	 * */
	@Test
	public void testCreateUser(){
		// Input
		User userInput = new User();
		userInput.setId(DEFAULT_ID);

		// Output
		User userOutput = userRepository.save(userInput);

		// Validate
		assertEquals(userInput, userOutput);
	}

	/**
	 * Test tạo user với thuộc tính hợp lệ (id:String)
	 * Input: id:String
	 * Expected output: id:String;
	 * */
	@Test
	public void testCreateUserValid(){
		// Input
		String idInput = DEFAULT_ID;
		User userInput = new User();
		userInput.setId(idInput);

		// Output
		User userOutput = userRepository.save(userInput);
		String idOutput = userOutput.getId();

		// Validate
		assertEquals(idInput.getClass(), idOutput.getClass());
	}


	/**
	 * Sau mỗi test sẽ chạy vào hàm này để clear data test
	 * */
	@AfterEach
	public void clearDatabase(){
		userRepository.deleteAll();
	}
}
