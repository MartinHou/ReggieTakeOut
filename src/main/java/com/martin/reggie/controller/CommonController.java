package com.martin.reggie.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.martin.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {    // MultipartFile名必须与表单name一致
        // 此时file为临时文件，需要转存到指定位置，否则本次请求结束会被删除
        log.info(file.toString());

        //原始上传的文件名
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //随机生成的名称，防止重名覆盖，同时加上文件后缀
        String fileName = UUID.randomUUID() +suffix;

        //创建目录对象
        File dir = new File(basePath);
        //判断目录是否存在,不存在则创建目录
        if(!dir.exists()){
            dir.mkdirs();
        }
        //将临时文件转存到指定目录
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

}
