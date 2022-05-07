package me.zyy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.common.R;
import me.zyy.reggie.entity.Employee;
import me.zyy.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // MD5格式改变密码
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 获取数据库操作对象
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper); // db_emp

        if (emp == null || !emp.getPassword().equals(password)) return R.error(0, "登录失败");
        if (emp.getStatus() == 0) return R.error(0, "用户已被禁用");

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(1, emp, "登录成功");
    }

    @PostMapping("/logout")
    public R<Employee> logout(HttpServletRequest request) {
        request.getSession().setAttribute("employee", null);
        return R.success(1, "退出登录");
    }

    @PostMapping
    public R<String> add(HttpServletRequest request, @RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long id = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);

        employeeService.save(employee);
        return R.success(1, "新增员工成功");
    }

    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String param) {

        log.info("{}, {}, 查询参数为: {}", page, pageSize, param);
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        // 带有param的情况，添加过滤条件，做模糊查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(param), Employee::getName, param); // 不为空
        // 添加排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 调用service
        employeeService.page(pageInfo, queryWrapper);

        return R.success(1, pageInfo, "查询数据成功");
    }

    /**
     * 修改一条employee记录的信息，修改的内容根据传来的employee对象而不同，可能是status、userinfo...
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee) {
        employeeService.updateById(employee);
        return R.success(1, "修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee emp = employeeService.getById(id);
        return emp == null ? R.error(0, "获取失败") : R.success(1, emp, "获取成功");
    }
}