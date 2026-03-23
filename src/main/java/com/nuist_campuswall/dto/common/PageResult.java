package com.nuist_campuswall.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult <T>{
    private long total;   //总数
    private List<T> records;   //当前页面数据
}
