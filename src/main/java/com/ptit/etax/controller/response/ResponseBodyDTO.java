package com.ptit.etax.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseBodyDTO<E> {

    /*
     * =============================================================================
     * =================================== ===== PRIVATE PROPERTIES =====
     * =============================================================================
     * ===================================
     */

    /**
     * Reset API: Response code
     */
    private ResponseCode code;

    /**
     * Reset API: Response message
     */
    private String message;

    /**
     * Reset API: Response total items for pagination
     */

    private long totalItems;

    /**
     * Reset API: Response page number for pagination
     */
    private int page;

    /**
     * Reset API: Response size of one page for pagination
     */
    private int size;

    /**
     * Reset API: Response data of 1 item
     */
    private E item;

    /**
     * Reset API: Response data of list items
     */
    private List<E> items;

    public ResponseBodyDTO(Pageable pageable, Page<E> page, ResponseCode code, String message) {
        this.code = code;
        this.message = message;
        this.totalItems = page.getTotalElements();
        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.items = page.getContent();
    }

    public ResponseBodyDTO(E item,ResponseCode code,String message){
        this.code=code;
        this.message=message;
        this.item=item;
    }

    public ResponseBodyDTO(List<E> items,ResponseCode code,String message,int totalItems){
        this.code=code;
        this.message=message;
        this.items=items;
        this.totalItems =totalItems ;
    }

    public ResponseBodyDTO(ResponseCode code,String message){
        this.code=code;
        this.message=message;
    }

    public ResponseBodyDTO(Pageable pageable, List<E> items, ResponseCode code, String message) {
        this.code = code;
        this.message = message;
        this.totalItems = items.size();
        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.items = items;
    }
}
