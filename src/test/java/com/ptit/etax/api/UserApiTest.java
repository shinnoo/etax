package com.ptit.etax.api;

import com.google.gson.Gson;
import com.ptit.etax.common.enums.ResponseCodeEnum;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.controller.api.UserController;
import com.ptit.etax.controller.request.UserRequest;
import com.ptit.etax.model.User;
import com.ptit.etax.repository.UserRepository;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Phạm vi test: Tầng giao tiếp với FrontEnd (cụ thể: API response)
 * Mục đích test:
 * - Test output trả ra có đúng định dạng hay không (Để giao tiếp với FrontEnd không bị thay đổi mỗi lần chỉnh sửa code)
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserApiTest {
	public static final String DEFAULT_STRING = "a";
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

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Tạo ra những bean giả
	 */
	@MockBean
	UserRepository userRepository;


	/**
	 * Test trường hợp login vào bằng mst chưa tồn tại trong db
	 * */
	@Test
	public void testLoginCode404() throws Exception {
		// Tạo request
		UserRequest.UserLogin request =  new UserRequest.UserLogin();
		request.setId(DEFAULT_STRING);

		// Map request sang định dạng json
		String requestJson = new Gson().toJson(request);

		// Logic mock
		// Mỗi khi hàm findById được thực hiện với param là "a"
		// Thì sẽ trả ra null (tức là ko tìm thấy trong database)
		given(userRepository.findById(eq(DEFAULT_STRING))).willReturn(Optional.ofNullable(null));

		// TH thuận lợi
		mockMvc.perform(
				post("/api/v1/login").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Mã số thuế chưa tồn tại"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_404.name()));
	}

	@Test
	public void testLoginCode201() throws Exception {
		// Tạo request
		UserRequest.UserLogin request =  new UserRequest.UserLogin();
		request.setId(DEFAULT_STRING);

		// Map request sang định dạng json
		String requestJson = new Gson().toJson(request);

		// tạo ra outout giả
		// khi chạy vào hàm findById("a") tìm được 1 bản ghi user với id là "a"
		// mặc dù không có bản ghi nào đang được lưu
		User mockUser = new User();
		mockUser.setId(DEFAULT_STRING);
		given(userRepository.findById(eq(DEFAULT_STRING))).willReturn(Optional.of(mockUser));
		mockMvc.perform(
				post("/api/v1/login").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCodeEnum.R_200.name()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.item.id").value(DEFAULT_STRING));
	}
}
