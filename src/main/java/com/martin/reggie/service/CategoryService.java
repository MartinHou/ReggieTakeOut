package com.martin.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.martin.reggie.entitty.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long ids);
}
