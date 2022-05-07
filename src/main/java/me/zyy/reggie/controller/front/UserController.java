package me.zyy.reggie.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.common.CustomException;
import me.zyy.reggie.common.R;
import me.zyy.reggie.entity.User;
import me.zyy.reggie.service.UserService;
import me.zyy.reggie.utils.VerificationCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> code(HttpSession session, @RequestBody User user) {
//        log.info("{}", user);
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = VerificationCodeUtil.generateVerificationCode4String(6);
            log.info(code);
            session.setAttribute(phone, code);
            return R.success(1, "手机验证码获取成功");
        }
        return R.error(0, "phone msg fail");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {

        String phone = map.get("phone");
        String code = map.get("code");
        String sessionCode = (String) session.getAttribute(phone);
        if (StringUtils.isNotEmpty(sessionCode) && sessionCode.equals(code)) {
            // 验证码通过，检查是否新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(1, user, "登录用户信息获取成功");
        }
        return R.error(0, "验证码失效");
    }
}
