package com.martin.reggie.dto;

import com.martin.reggie.entitty.Dish;
import com.martin.reggie.entitty.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
