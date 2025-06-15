package org.xzx.entity.dto;

import lombok.Data;

@Data
public class SearchDTO {
    //关键字和分类
    private  String searchKeyword;
    private  String activeCategory;
}
