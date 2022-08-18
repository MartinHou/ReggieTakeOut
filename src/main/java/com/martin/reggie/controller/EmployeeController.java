package com.martin.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.martin.reggie.common.R;
import com.martin.reggie.entitty.Employee;
import com.martin.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     * @param request 用于获取session，保存登录信息
     * @param employee 接收json数据，封装成Employee对象（名称属性对应）
     * @return R响应结果
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据用户名查询数据库
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);

        //3.若未查到，返回失败结果
        if(emp==null){
            return R.error("登录失败");
       }

        //4.比对密码
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5.查看员工状态是否可用
        if(emp.getStatus()==0){
            return R.error("账号已禁用");
        }

        //6.成功，员工id放入session，并返回成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工登出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 清理Session中保存的当前登录的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息：{}",employee.toString());
        //设置初始密码,需md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //初始化其他为null的值
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pagesize={},name={}",page,pageSize,name);

        //创建分页器
        Page pageInfo = new Page(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加name相关条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行过滤
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }
}
