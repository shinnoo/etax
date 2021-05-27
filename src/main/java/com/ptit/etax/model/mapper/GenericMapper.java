package com.ptit.etax.model.mapper;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenericMapper {
	
	@Autowired
	private ModelMapper modelMapper;
	// map cả null sang
	// mục đích sử dụng cho request tạo
	public <T, E> E mapToType(T source, Class<E> typeDestination) {
		return modelMapper.map(source, typeDestination);
	}

	// map bỏ qua null
	// mục đích sử dụng cho request update
	// với những thuộc tính mà update request null (tức là không thay đổi trường đó)
	// thì sẽ không map sang
	public <T, E> E mapIgnoreNull(T source, Class<E> typeDestination) {
		modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull()).setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper.map(source, typeDestination);
	}
}
