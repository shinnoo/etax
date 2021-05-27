package com.ptit.etax.api;

import com.google.gson.Gson;
import com.ptit.etax.common.enums.ResponseCodeEnum;
import com.ptit.etax.controller.api.PayController;
import com.ptit.etax.controller.api.TaxController;
import com.ptit.etax.controller.request.PayRequest;
import com.ptit.etax.model.Pay;
import com.ptit.etax.model.Tax;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.repository.PayRepository;
import com.ptit.etax.repository.TaxRepository;
import com.ptit.etax.service.PayService;
import com.ptit.etax.service.TaxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Phạm vi test: Tầng giao tiếp với FrontEnd (cụ thể: API response)
 * Mục đích test:
 * - Test output trả ra có đúng định dạng hay không (Để giao tiếp với FrontEnd không bị thay đổi mỗi lần chỉnh sửa code)
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(PayController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PayApiTest {
	public static final String DEFAULT_STRING = "a";
	public static final Instant DEFAULT_TIME = Instant.ofEpochMilli(0l);
	public static final Long DEFAULT_LONG = 1l;
	public static final Integer DEFAULT_INT = 1;
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

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Tạo ra những bean giả
	 */
	@MockBean
	PayRepository payRepository;

	@MockBean
	TaxRepository taxRepository;

	/**
	 * Code 200
	 * Test trường hợp lấy danh sách thuế đã khai báo theo MST
	 * Nhưng không tìm thấy bản ghi nào
	 * */
	@Test
	public void testGetByTaxId() throws Exception {
		// validate
		mockMvc.perform(
				get("/api/v1/pays/taxs/"+DEFAULT_STRING))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_200.name()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items").isEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalItems").value(0));
	}

	/**
	 * Code 200
	 * Test trường hợp lấy danh sách thuế đã khai báo theo MST
	 * */
	@Test
	public void testGotByTaxId() throws Exception {
		// Init
		Pay pay = new Pay();
		pay.setId(DEFAULT_STRING);
		pay.setTaxId(DEFAULT_STRING);
		pay.setCalculateDate(DEFAULT_TIME);
		pay.setStatus(DEFAULT_STRING);
		pay.setPrice(DEFAULT_LONG);
		pay.setIncome(DEFAULT_LONG);
		pay.setNumberOfDependents(DEFAULT_INT);
		pay.setPayment(DEFAULT_LONG);
		List<Pay> pays = new ArrayList<>();
		pays.add(pay);


		// Logic mock
		// Mỗi khi hàm findAllByTaxId được thực hiện với param là MST của request
		// Thì sẽ trả ra 1 list Pay (tức là đã tìm thấy thông tin về lần khai báo này trong database)
		given(payRepository.findAllByTaxId(eq(DEFAULT_STRING))).willReturn(pays);

		// validate
		mockMvc.perform(
				get("/api/v1/pays/taxs/"+DEFAULT_STRING))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_200.name()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalItems").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items.[0].id").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items.[0].taxId").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items.[0].calculateDate").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items.[0].status").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items.[0].price").value(DEFAULT_LONG))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items.[0].income").value(DEFAULT_LONG))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items.[0].numberOfDependents").value(DEFAULT_INT))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items.[0].payment").value(DEFAULT_LONG));
	}

	/**
	 * Code 404
	 * Test  không tìm thấy thông tin về MST
	 * -> chưa khai báo thông tin thuế (tên người nộp thế, địa chỉ, sđt , ...)
	 * */
	@Test
	public void testCreateCode404() throws Exception {
		// Map request sang định dạng json
		String requestJson = "{\"taxId\":\"a\",\"calculateDate\":0,\"income\":1,\"numberOfDependents\":1}";

		// Logic mock
		// Mỗi khi hàm findById được thực hiện với param là MST của request
		// Thì sẽ trả ra 1 Tax (tức là đã tìm thấy thông tin về MST này trong database)
		given(taxRepository.findById(eq("a"))).willReturn(Optional.ofNullable(null));


		// Validate
		mockMvc.perform(
				post("/api/v1/pays").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Thông tin thuế chưa được khai báo"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_404.name()));
	}

	/**
	 * Code 201
	 * Test khai báo thuế thành công
	 * */
	@Test
	public void testCreateCode201() throws Exception {
		// Map request sang định dạng json
		String requestJson = "{\"taxId\":\"a\",\"calculateDate\":0,\"income\":1,\"numberOfDependents\":1}";

		// Logic mock
		// Mỗi khi hàm findById được thực hiện với param là MST của request
		// Thì sẽ trả ra 1 Tax (tức là đã tìm thấy thông tin về MST này trong database)
		given(taxRepository.findById(eq("a"))).willReturn(Optional.of(new Tax()));


		// Validate
		mockMvc.perform(
				post("/api/v1/pays").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Thêm thành công"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_201.name()));
	}

	/**
	 * Code 400
	 * Test khai báo thuế thành công
	 * */
	@Test
	public void testCreateCode400() throws Exception {
		// Map request sang định dạng json
		String requestJson = "{\"taxId\":\"a\",\"calculateDate\":0,\"income\":1,\"numberOfDependents\":1}";

		// Logic mock
		// Mỗi khi hàm findById được thực hiện với param là MST của request
		// Thì sẽ trả ra 1 Tax (tức là đã tìm thấy thông tin về MST này trong database)
		given(taxRepository.findById(eq("a"))).willReturn(Optional.of(new Tax()));


		// Validate
		mockMvc.perform(
				post("/api/v1/pays").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Thêm thành công"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_201.name()));
	}

	/**
	 * Code 404
	 * Test đóng thuế vào lượt khai báo không tồn tại trong db
	 * */
	@Test
	public void testUpdateCode404() throws Exception {
		// Map request sang định dạng json
		String requestJson = "{\"paymentAmount\":1}";

		// Logic mock
		// Mỗi khi hàm findById được thực hiện với param là MST của request
		// Thì sẽ trả ra null (tức là ko tìm thấy thông tin về MST này trong database)
		given(taxRepository.findById(eq("a"))).willReturn(Optional.ofNullable(null));


		// Validate
		mockMvc.perform(
				post("/api/v1/pays/b").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Không tìm thấy lần khai báo này"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_404.name()));
	}

	/**
	 * Code 201
	 * Test đóng thuế thành công
	 * */
	@Test
	public void testUpdateCode201() throws Exception {
		// Map request sang định dạng json
		String requestJson = "{\"paymentAmount\":1}";

		// Logic mock
		// Mỗi khi hàm findById được thực hiện với param là MST của request
		// Thì sẽ trả ra 1 Tax (tức là đã tìm thấy thông tin về MST này trong database)
		given(taxRepository.findById(eq("a"))).willReturn(Optional.ofNullable(new Tax()));


		// Validate
		mockMvc.perform(
				post("/api/v1/pays/b").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Không tìm thấy lần khai báo này"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_404.name()));
	}

	/**
	 * Code 200
	 * Test api tính thuế trả về số tiền phải nộp thuế
	 * */
	@Test
	public void testCalculateTax() throws Exception {
		PayRequest.PayCalculate request = new PayRequest.PayCalculate();
		request.setIncome(18000000l);
		request.setNumberOfDependents(2);

		String requestJson = new Gson().toJson(request);
		mockMvc.perform(
				post("/api/v1/pays/calculate").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$").value(450000));
	}

	/**
	 * Code 200
	 * Test api tính thuế trả về ko có tiền thuế (ko phải nộp thuế)
	 * */
	@Test
	public void testCalculateTax1() throws Exception {
		PayRequest.PayCalculate request = new PayRequest.PayCalculate();
		request.setIncome(1l);
		request.setNumberOfDependents(2);

		String requestJson = new Gson().toJson(request);
		mockMvc.perform(
				post("/api/v1/pays/calculate").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$").value(0));
	}


}
