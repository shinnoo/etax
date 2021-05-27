package com.ptit.etax.common.error;

import com.ptit.etax.common.enums.ResponseCodeEnum;
import com.ptit.etax.controller.response.ResponseBodyDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@RestControllerAdvice
public class ApiExceptionHandler {
	/**
	 * Tất cả các Exception không được khai báo sẽ được xử lý tại đây
	 */
	@ExceptionHandler(Exception.class)
	public @ResponseBody ResponseEntity<ResponseBodyDTO<Object>> handleServerError(Exception ex, WebRequest request) {
		// quá trình kiểm soat lỗi diễn ra ở đây
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseBodyDTO<Object> dtoResult = new ResponseBodyDTO<>();
		dtoResult.setCode(ResponseCodeEnum.E_0001);
		dtoResult.setMessage("Error unknown");
		System.out.println(ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).body(dtoResult);
	}


	@ExceptionHandler(BadRequestException.class)
	public @ResponseBody ResponseEntity<ResponseBodyDTO<Object>> handleBadRequestException(Exception ex,
			WebRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseBodyDTO<Object> dtoResult = new ResponseBodyDTO<Object>();
		dtoResult.setCode(ResponseCodeEnum.R_400);
		dtoResult.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(dtoResult);
	}

	@ExceptionHandler(NotFoundException.class)
	public @ResponseBody ResponseEntity<ResponseBodyDTO<Object>> handleNotFoundException(Exception ex) {
		// quá trình kiểm soat lỗi diễn ra ở đây
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseBodyDTO<Object> dtoResult = new ResponseBodyDTO<>();
		dtoResult.setCode(ResponseCodeEnum.R_404);
		dtoResult.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(dtoResult);
	}

	@ExceptionHandler(ConflictException.class)
	public @ResponseBody ResponseEntity<ResponseBodyDTO<Object>> handleConflictException(Exception ex) {
		// quá trình kiểm soat lỗi diễn ra ở đây
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseBodyDTO<Object> dtoResult = new ResponseBodyDTO<>();
		dtoResult.setCode(ResponseCodeEnum.R_409);
		dtoResult.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).body(dtoResult);
	}
}
