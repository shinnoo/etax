package com.ptit.etax.service;

import com.ptit.etax.common.constant.StatusConstant;
import com.ptit.etax.common.error.BadRequestException;
import com.ptit.etax.common.error.ConflictException;
import com.ptit.etax.common.error.NotFoundException;
import com.ptit.etax.common.util.CalculateUtil;
import com.ptit.etax.controller.request.PayRequest;
import com.ptit.etax.controller.request.UserRequest;
import com.ptit.etax.model.Pay;
import com.ptit.etax.model.Tax;
import com.ptit.etax.model.User;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.repository.PayRepository;
import com.ptit.etax.repository.TaxRepository;
import com.ptit.etax.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 - Phạm vi test: Tầng nghiệp vụ của Pay liên quan đến khai báo thuế, đóng thuế
 - Mục đích: Test những nghiệp vụ riêng (VD khi tạo thì trường nào để mặc định, mặc định với gtri nào ...)
 * */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PayServiceTest {
	/**
	 * Khởi tạo những bean cần thiết cho quá trình khởi tạo context
	 */
	@TestConfiguration
	public static class Configuration {
		@Bean
		PayService payService() {
			return new PayService();
		}

		@Bean
		GenericMapper genericMapper() {
			return new GenericMapper();
		}
	}

	public static final String DEFAULT_ID = "a";

	@Autowired
	PayService payService;

	@Autowired
	PayRepository payRepository;

	@Autowired
	TaxRepository taxRepository;

	/**
	 * Code 200
	 * Test tạo Pay chưa tồn tại trong db
	 * Input: PayCreate
	 * Expected output: Pay;
	 * */
	@Test
	public void testCreateOK(){
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
		PayRequest.PayCreate input = new PayRequest.PayCreate();
		input.setTaxId(id);
		input.setIncome(18000000l);
		input.setCalculateDate(Instant.ofEpochSecond(0l));
		input.setNumberOfDependents(1);

		// Output
		Pay output = payService.create(input);

		// Validate
		assertEquals(input.getTaxId(), output.getTaxId());
		assertEquals(input.getCalculateDate(), output.getCalculateDate());
		assertEquals(StatusConstant.CREATED, output.getStatus());
		assertEquals(CalculateUtil.calculate(input.getIncome()), output.getPrice());
		assertEquals(input.getIncome(), output.getIncome());
		assertEquals(input.getNumberOfDependents(), output.getNumberOfDependents());
		assertEquals(0, output.getPayment());
	}

	/**
	 * Code 404
	 * Test khai báo nhưng chưa có thông tin về MST
	 * Input: PayCreate
	 * Expected output: NotFoundException("Thông tin thuế chưa được khai báo");
	 * */
	@Test
	public void testCreateCode404(){
		// Input
		String id = DEFAULT_ID;
		PayRequest.PayCreate input = new PayRequest.PayCreate();
		input.setTaxId(id);
		input.setIncome(18000000l);
		input.setCalculateDate(Instant.ofEpochSecond(0l));
		input.setNumberOfDependents(1);

		// Validate
		// param 1 : expected class exception
		// param 2 : code block will throw NotFoundException
		// param 3 : message when exception
		assertThrows(NotFoundException.class, () -> payService.create(input), "Thông tin thuế chưa được khai báo");
	}

	/**
	 * Code 404
	 * Test đóng thuế nhưng chưa khai báo
	 * Input: PayUpdate
	 * Expected output: NotFoundException("Không tìm thấy lần khai báo này");
	 * */
	@Test
	public void testUpdateCode404() {
		// Input
		PayRequest.PayUpdate input = new PayRequest.PayUpdate();
		input.setPaymentAmount(100000l);

		// Validate
		// param 1 : expected class exception
		// param 2 : code block will throw ConflictException
		// param 3 : message when exception
		assertThrows(NotFoundException.class, () -> payService.update(DEFAULT_ID,input), "Thông tin thuế chưa được khai báo");
	}

	/**
	 * Code 201
	 * Test đóng thuế thành công nhưng chưa đủ
	 * Input: PayUpdate
	 * Expected output:
	 * - Status="Chưa nộp đủ"
	 * - Tổng tiền đã nộp = tổng đã nộp trước đó + mới nộp
	 * */
	@Test
	public void testUpdateCode201() {
		// Init database
		Pay pay = new Pay();
		pay.setId(DEFAULT_ID);
		pay.setCalculateDate(Instant.ofEpochMilli(0l));
		pay.setIncome(1800000l);
		pay.setNumberOfDependents(0);
		pay.setPayment(0l);
		pay.setPrice(450000l);
		pay.setStatus(StatusConstant.CREATED);
		pay.setTaxId(DEFAULT_ID);
		String idInput = payRepository.save(pay).getId();

		// Input
		PayRequest.PayUpdate requestInput = new PayRequest.PayUpdate();
		requestInput.setPaymentAmount(100000l);

		// Output
		Pay output = payService.update(idInput,requestInput);

		// Validate
		assertEquals(requestInput.getPaymentAmount(),output.getPayment());
		assertEquals(StatusConstant.INCOMPLETE,output.getStatus());
	}

	/**
	 * Code 201
	 * Test đóng thuế thành công và đã đóng đủ
	 * Input: PayUpdate
	 * Expected output:
	 * - Status="Đã nộp đủ"
	 * - Tổng tiền đã nộp = tổng đã nộp trước đó + mới nộp
	 * */
	@Test
	public void testUpdateCode201Done() {
		// Init database
		Pay pay = new Pay();
		pay.setId(DEFAULT_ID);
		pay.setCalculateDate(Instant.ofEpochMilli(0l));
		pay.setIncome(1800000l);
		pay.setNumberOfDependents(0);
		pay.setPayment(50000l);
		pay.setPrice(450000l);
		pay.setStatus(StatusConstant.INCOMPLETE);
		pay.setTaxId(DEFAULT_ID);
		String idInput = payRepository.save(pay).getId();

		// Input
		PayRequest.PayUpdate requestInput = new PayRequest.PayUpdate();
		requestInput.setPaymentAmount(400000l);

		// Output
		Pay output = payService.update(idInput,requestInput);

		// Validate
		assertEquals(450000l,output.getPayment());
		assertEquals(StatusConstant.COMPLETE,output.getStatus());
	}

	/**
	 * Code 400
	 * Test đóng thuế đủ rồi nhưng vẫn đóng tiếp
	 * Input: PayUpdate
	 * Expected output: BadRequestException("Lần khai báo này đã được đóng đủ tiền")
	 * */
	@Test
	public void testUpdateCode400() {
		// Init database
		Pay pay = new Pay();
		pay.setId(DEFAULT_ID);
		pay.setCalculateDate(Instant.ofEpochMilli(0l));
		pay.setIncome(1800000l);
		pay.setNumberOfDependents(0);
		pay.setPayment(450000l);
		pay.setPrice(450000l);
		pay.setStatus(StatusConstant.COMPLETE);
		pay.setTaxId(DEFAULT_ID);
		String idInput = payRepository.save(pay).getId();

		// Input
		PayRequest.PayUpdate requestInput = new PayRequest.PayUpdate();
		requestInput.setPaymentAmount(100000l);

		// Output

		// Validate
		assertThrows(BadRequestException.class, () -> payService.update(idInput,requestInput), "Lần khai báo này đã được đóng đủ tiền");
	}
}
