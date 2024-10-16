package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
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

//       //公共自动自动填充 common/MyMetaObjectHandler.java
//       employee.setCreateTime(LocalDateTime.now());
//       employee.setUpdateTime(LocalDateTime.now());

//       获取当前登录用户的id
//       Long empId = (Long) request.getSession().getAttribute("employee");
//       employee.setCreateUser(empId);
//       employee.setUpdateUser(empId);


       employeeService.save(employee);

       return R.success("新增员工成功！");
   }


    /**
     * 员工信息的分页查询
     * 1: 页面发送 ajax请求，将分页查询参数 （page、pageSize）提交到服务端
     * 2: 服务端 Controller 接受页面提交的数据并调用 Service 查询数据
     * 3: Service 调用 Mapper 操作数据库，查询分页数据
     * 4: Controller 将查询到的分页数据响应给页面
     * 5: 页面接收到分页数据并通过ElementUI的 Table 组件展示到页面上
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
   @GetMapping("/page")
   public R<Page> page(int page, int pageSize, String name) {
//       构造分页构造器
       Page pageInfo = new Page(page, pageSize);
//       条件构造器
       LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
       if(name !=null){
           queryWrapper.like(Employee::getName, name);
       }
//       queryWrapper.like(StringUtils.isEmpty(name), Employee::getUsername, name);

       queryWrapper.orderByDesc(Employee::getUpdateTime);

       employeeService.page(pageInfo, queryWrapper);
       return R.success(pageInfo);
   }

    /**
     * 根据id 修改员工信息
     * @param request
     * @param employee
     * @return
     */
   @PutMapping
   public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
//       //公共自动自动填充 common/MyMetaObjectHandler.java
//       Long empId = (Long) request.getSession().getAttribute("employee");
//       employee.setUpdateTime(LocalDateTime.now());
//       employee.setUpdateUser(empId);

       long id = Thread.currentThread().getId();
       log.info("线程id 为：{}", id);

       employeeService.updateById(employee);
       return R.success("员工信息修改成功");
   }

    /**
     * 根据id 获取员工信息
     * @param id
     * @return
     */
   @GetMapping("/{id}")
   public R<Employee> getById(@PathVariable Long id) {
       Employee employee = employeeService.getById(id);
       if(employee != null) {
           return R.success(employee);
       }
       return R.error("没有查询到对应信息");
   }
}
