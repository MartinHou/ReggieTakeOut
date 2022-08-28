package com.martin.reggie.dto;

import com.martin.reggie.entitty.Setmeal;
import com.martin.reggie.entitty.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
