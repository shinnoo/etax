package com.ptit.etax.repository;

import com.ptit.etax.model.Pay;
import com.ptit.etax.model.Tax;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
public class PayRepositoryTest {
	@Autowired // load từ context ra
	PayRepository payRepository;

	public static final String DEFAULT_ID = "a";
	/**
	 * Test tạo tax đủ thuộc tính
	 * Input: Pay
	 * Expected output: Pay;
	 * */
	@Test
	public void testCreateTax(){
		// Input
		Pay input = new Pay();
		input.setId(DEFAULT_ID);
		input.setTaxId(DEFAULT_ID);
		input.setCalculateDate(Instant.ofEpochSecond(0l));
		input.setStatus(DEFAULT_ID);
		input.setPrice(450000l);
		input.setIncome(18000000l);
		input.setNumberOfDependents(0);
		input.setPayment(0l);

		// Output
		Pay output = payRepository.save(input);

		// Validate
		assertNotEquals(input.getId(),output.getId()); // vì id tự động gen
		assertEquals(input.getTaxId(),output.getTaxId());
		assertEquals(input.getCalculateDate(),output.getCalculateDate());
		assertEquals(input.getStatus(),output.getStatus());
		assertEquals(input.getPrice(),output.getPrice());
		assertEquals(input.getIncome(),output.getIncome());
		assertEquals(input.getNumberOfDependents(),output.getNumberOfDependents());
		assertEquals(input.getPayment(),output.getPayment());
	}

	/**
	 * Test lấy pay theo id
	 * Input: id
	 * Expected output: Tax;
	 * */
	@Test
	public void testGetOneTax() {
		// Init
		Pay input = new Pay();
		input.setId(DEFAULT_ID);
		input.setTaxId(DEFAULT_ID);
		input.setCalculateDate(Instant.ofEpochSecond(0l));
		input.setStatus(DEFAULT_ID);
		input.setPrice(450000l);
		input.setIncome(18000000l);
		input.setNumberOfDependents(0);
		input.setPayment(0l);
		Pay init = payRepository.save(input);

		// Output
		Pay output = payRepository.findById(init.getId()).get();

		// Validate
		assertEquals(init,output);
	}

	/**
	 * Sau mỗi test sẽ chạy vào hàm này để clear data test
	 * */
	@AfterEach
	public void clearDatabase(){
		payRepository.deleteAll();
	}
}
