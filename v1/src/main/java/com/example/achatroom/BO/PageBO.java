package com.example.achatroom.BO;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageBO implements Serializable {
    int pageIndex;
    int pageSize;
}
