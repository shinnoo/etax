package com.ptit.etax.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class ResponseDTO<E> {

    private String code;

    private String message;

    private long totalItems;

    @JsonProperty("size")
    private int size;

    @JsonProperty("page")
    private int page;

    /**
     * Reset API: Response data of 1 item
     */
    @JsonProperty("item")
    private E item;

    @JsonProperty("items")
    private List<E> items;

}
