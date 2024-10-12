package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee) {
//       将密码进行md5加密
        String password = employee.getPassword();
       password = DigestUtils.md5DigestAsHex(password.getBytes());

//       根据页面提交的用户名 查询数据库
       LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.eq(Employee::getUsername, employee.getUsername());
       Employee emp = employeeService.getOne(queryWrapper);

       if(emp == null) {
           return R.error("登录失败");
       }

//       获取到的密码和数据库的密码进行比对
        if(!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

//        查看员工状态是否禁用
        if(emp.getStatus() ==0) {
            return R.error("账号已禁用");
        }

//        登录成功，将员工的id 存入到Session并返回等了成功
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
   }

    /**
     * 员工退出
     * @param request
     * @return
     */
   @PostMapping("/logout")
   public R<String> logout(HttpServletRequest request) {
//       清理Session 中保存的当前登录的员工id
       request.getSession().removeAttribute("employee");
        return R.success("退出成功");
   }

    /**
     * 新增员工
     * @param employee
     * @return
     */
   @PostMapping
   public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
       log.info("新增员工，员工信息： {}", employee.toString());
//       设置初始密码，
       employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
       employee.setCreateTime(LocalDateTime.now());
       employee.setUpdateTime(LocalDateTime.now());

//       获取当前登录用户的id
       Long empId = (Long) request.getSession().getAttribute("employee");
       employee.setCreateUser(empId);
       employee.setUpdateUser(empId);


       employeeService.save(employee);

       return R.success("新增员工成功！");
   }
}
