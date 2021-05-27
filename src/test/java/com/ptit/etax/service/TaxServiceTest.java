package com.ptit.etax.service;

import com.ptit.etax.common.error.ConflictException;
import com.ptit.etax.common.error.NotFoundException;
import com.ptit.etax.controller.request.TaxRequest;
import com.ptit.etax.controller.request.UserRequest;
import com.ptit.etax.model.Tax;
import com.ptit.etax.model.User;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.repository.TaxRepository;
import com.ptit.etax.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 - Phạm vi test: Tầng nghiệp vụ của Tax liên quan đến khai báo thông tin cho mst
 - Mục đích: Test những nghiệp vụ riêng (VD khi tạo thì trường nào để mặc định, mặc định với gtri nào ...)
 * */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TaxServiceTest {
	/**
	 * Khởi tạo những bean cần thiết cho quá trình khởi tạo context
	 */
	@TestConfiguration
	public static class Configuration {
		@Bean
		TaxService taxService() {
			return new TaxService();
		}

		@Bean
		GenericMapper genericMapper() {
			return new GenericMapper();
		}
	}
	public static final String DEFAULT_ID = "a";

	@Autowired
	TaxService taxService;

	@Autowired
	TaxRepository taxRepository;

	/**
	 * Code 201
	 * Test tạo user chưa tồn tại trong db
	 * Input: TaxCreate
	 * Expected output: Tax;
	 * */
	@Test
	public void testCreateOK(){
		// Input
		TaxRequest.TaxCreate input = new TaxRequest.TaxCreate();
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
		Tax output = taxService.create(input);

		// Validate
		assertEquals(input.getId(), output.getId());
		assertEquals(input.getName(), output.getName());
		assertEquals(input.getAddress(), output.getAddress());
		assertEquals(input.getCity(), output.getCity());
		assertEquals(input.getDistrict(), output.getDistrict());
		assertEquals(input.getPhone(), output.getPhone());
		assertEquals(input.getFax(), output.getFax());
		assertEquals(input.getEmail(), output.getEmail());
		assertEquals(input.getBusiness(), output.getBusiness());
		assertEquals(input.getAccountNumber(), output.getAccountNumber());
	}


	/**
	 * Code 409
	 * Test tạo thông tin về MST nhưng đã tồn tại trong db
	 * 1 MST chỉ có 1 thông tin
	 * Input: UserCreate
	 * Expected output: ConflictException("Thông tin mã số thuế đã được đăng ký");
	 * */
	@Test
	public void testCreateCode409(){
		// Init database
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
		TaxRequest.TaxCreate input = new TaxRequest.TaxCreate();
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



		// Validate
		// param 1 : expected class exception
		// param 2 : code block will throw ConflictException
		// param 3 : message when exception
		assertThrows(ConflictException.class, () -> taxService.create(input), "Thông tin mã số thuế đã được đăng ký");
	}


	/**
	 * Code 200
	 * Test lấy ra thông tin về MST (Tax) đã tồn tại trong db
	 * Input: id
	 * Expected output: output.getId() = id;
	 * */
	@Test
	public void testGetOneCode200() {
		// Init database
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
		Tax output = taxService.getById(DEFAULT_ID);

		// Validate
		assertEquals(id,output.getId());
	}

	/**
	 * Code 409
	 * Test get thông tin về MST (Tax) chưa tồn tại trong db
	 * Input: id:String
	 * Expected output: NotFoundException("Không tìm thấy thông tin mã số thuế này");
	 * */
	@Test
	public void testGetOneCode409() {
		// Input
		String id = DEFAULT_ID;

		// Validate
		// param 1 : expected class exception
		// param 2 : code block will throw ConflictException
		// param 3 : message when exception
		assertThrows(NotFoundException.class, () -> taxService.getById(id), "Không tìm thấy thông tin mã số thuế này");
	}

	/**
	 * Sau mỗi test sẽ chạy vào hàm này để clear data test
	 * */
	@AfterEach
	public void clearDatabase(){
		taxRepository.deleteAll();
	}
}
