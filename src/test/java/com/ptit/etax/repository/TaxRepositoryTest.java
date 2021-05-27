package com.ptit.etax.repository;

import com.ptit.etax.model.Tax;
import com.ptit.etax.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 - Phạm vi test : Tầng thao tác với database liên quan đến Tax
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
@DataJpaTest    // khởi tạo và load tất cả các class liên quan đến database vào context hiện tại
@ActiveProfiles("test")
public class TaxRepositoryTest {
	@Autowired // load từ context ra
	TaxRepository taxRepository;


	public static final String DEFAULT_ID = "a";

	/**
	 * Test tạo tax đủ thuộc tính
	 * Input: Tax
	 * Expected output: Tax;
	 * */
	@Test
	public void testCreateTax(){
		// Input
		Tax input = new Tax();
		input.setId(DEFAULT_ID);
		input.setName(DEFAULT_ID);
		input.setAddress(DEFAULT_ID);
		input.setCity(DEFAULT_ID);
		input.setDistrict(DEFAULT_ID);
		input.setPhone(DEFAULT_ID);
		input.setFax(DEFAULT_ID);
		input.setEmail(DEFAULT_ID);
		input.setBusiness(DEFAULT_ID);
		input.setAccountNumber(DEFAULT_ID);

		// Output
		Tax output = taxRepository.save(input);

		// Validate
		assertEquals(input, output);
	}


	/**
	 * Test lấy tax theo id
	 * Input: id
	 * Expected output: Tax;
	 * */
	@Test
	public void testGetOneTax(){
		// Init
		Tax tax = new Tax();
		tax.setId(DEFAULT_ID);
		tax.setName(DEFAULT_ID);
		tax.setAddress(DEFAULT_ID);
		tax.setCity(DEFAULT_ID);
		tax.setDistrict(DEFAULT_ID);
		tax.setPhone(DEFAULT_ID);
		tax.setFax(DEFAULT_ID);
		tax.setEmail(DEFAULT_ID);
		tax.setBusiness(DEFAULT_ID);
		tax.setAccountNumber(DEFAULT_ID);
		taxRepository.save(tax);

		// Input
		String id = DEFAULT_ID;

		// Output
		Tax output = taxRepository.findById(id).get();

		// Validate
		assertEquals(id, output.getId());
	}


	/**
	 * Sau mỗi test sẽ chạy vào hàm này để clear data test
	 * */
	@AfterEach
	public void clearDatabase(){
		taxRepository.deleteAll();
	}


}
