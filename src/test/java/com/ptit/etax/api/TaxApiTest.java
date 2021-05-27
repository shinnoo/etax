package com.ptit.etax.api;

import com.google.gson.Gson;
import com.ptit.etax.common.enums.ResponseCodeEnum;
import com.ptit.etax.controller.api.TaxController;
import com.ptit.etax.controller.api.UserController;
import com.ptit.etax.controller.request.TaxRequest;
import com.ptit.etax.controller.request.UserRequest;
import com.ptit.etax.model.Tax;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.repository.TaxRepository;
import com.ptit.etax.repository.UserRepository;
import com.ptit.etax.service.TaxService;
import com.ptit.etax.service.UserService;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Phạm vi test: Tầng giao tiếp với FrontEnd (cụ thể: API response)
 * Mục đích test:
 * - Test output trả ra có đúng định dạng hay không (Để giao tiếp với FrontEnd không bị thay đổi mỗi lần chỉnh sửa code)
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(TaxController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TaxApiTest {
	public static final String DEFAULT_STRING = "a";
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

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Tạo ra những bean giả
	 */
	@MockBean
	TaxRepository taxRepository;


	/**
	 * Code 409
	 * Test trường hợp tạo thông tin thuế nhưng nó đã tồn tại trong db
	 * */
	@Test
	public void testCreateCode409() throws Exception {
		// Tạo request
		TaxRequest.TaxCreate request =  new TaxRequest.TaxCreate();
		request.setId(DEFAULT_STRING);
		request.setName(DEFAULT_STRING);
		request.setAddress(DEFAULT_STRING);
		request.setCity(DEFAULT_STRING);
		request.setDistrict(DEFAULT_STRING);
		request.setPhone(DEFAULT_STRING);
		request.setFax(DEFAULT_STRING);
		request.setEmail(DEFAULT_STRING);
		request.setBusiness(DEFAULT_STRING);
		request.setAccountNumber(DEFAULT_STRING);

		// Map request sang định dạng json
		String requestJson = new Gson().toJson(request);

		// Logic mock
		// Mỗi khi hàm findById được thực hiện với param là MST của request
		// Thì sẽ trả ra 1 Tax (tức là đã tìm thấy thông tin về MST này trong database)
		given(taxRepository.findById(eq(request.getId()))).willReturn(Optional.of(new Tax()));

		// TH thuận lợi
		mockMvc.perform(
				post("/api/v1/taxs").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isConflict())
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Thông tin mã số thuế đã được đăng ký"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_409.name()));
	}

	/**
	 * Code 201
	 * Test trường hợp tạo thông tin thuế thành công
	 * */
	@Test
	public void testCreateCode201() throws Exception {
		// Tạo request
		TaxRequest.TaxCreate request =  new TaxRequest.TaxCreate();
		request.setId(DEFAULT_STRING);
		request.setName(DEFAULT_STRING);
		request.setAddress(DEFAULT_STRING);
		request.setCity(DEFAULT_STRING);
		request.setDistrict(DEFAULT_STRING);
		request.setPhone(DEFAULT_STRING);
		request.setFax(DEFAULT_STRING);
		request.setEmail(DEFAULT_STRING);
		request.setBusiness(DEFAULT_STRING);
		request.setAccountNumber(DEFAULT_STRING);

		// Map request sang định dạng json
		String requestJson = new Gson().toJson(request);

		// Logic mock
		// Mỗi khi hàm findById được thực hiện với param là MST của request
		// Thì sẽ trả ra null (tức là ko tìm thấy thông tin về MST này trong database)
		// Lúc này tạo mới hợp lệ
		// hàm này có ý nghĩa là khi lưu vào db thì sẽ giả sử là đã lưu thành công mặc dù ko có bản ghi nào
		// và nó sẽ trả ra đúng như lúc truyền vào
		given(taxRepository.save(isA(Tax.class))).willAnswer(i -> i.getArgument(0));

		// Validate
		mockMvc.perform(
				post("/api/v1/taxs").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Đã đăng kí thông tin mã số thuế " + DEFAULT_STRING + " thành công"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_201.name()));
	}


	/***
	 * Code 200
	 * Test trường hợp lấy thông tin mst thành công
	 */
	@Test
	public void testGetOneCode200() throws Exception {
		// Tạo request
		Tax tax = new Tax();
		tax.setId(DEFAULT_STRING);
		tax.setName(DEFAULT_STRING);
		tax.setAddress(DEFAULT_STRING);
		tax.setCity(DEFAULT_STRING);
		tax.setDistrict(DEFAULT_STRING);
		tax.setPhone(DEFAULT_STRING);
		tax.setFax(DEFAULT_STRING);
		tax.setEmail(DEFAULT_STRING);
		tax.setBusiness(DEFAULT_STRING);
		tax.setAccountNumber(DEFAULT_STRING);


		// Logic mock
		// Mỗi khi hàm findById được thực hiện với param là MST của request
		// Thì sẽ trả ra 1 bản ghi như trên
		given(taxRepository.findById(eq(DEFAULT_STRING))).willReturn(Optional.of(tax));

		// TH thuận lợi
		mockMvc.perform(get("/api/v1/taxs/" + DEFAULT_STRING))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_200.name()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.id").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.name").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.address").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.city").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.district").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.phone").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.fax").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.email").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.business").value(DEFAULT_STRING))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.accountNumber").value(DEFAULT_STRING));
	}

	/***
	 * Code 404
	 * Test trường hợp lấy thông tin mst không tồn tại trong db
	 */
	@Test
	public void testGetOneCode404() throws Exception {
		mockMvc.perform(get("/api/v1/taxs/" + DEFAULT_STRING))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Không tìm thấy thông tin mã số thuế này"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_404.name()));
	}
}
