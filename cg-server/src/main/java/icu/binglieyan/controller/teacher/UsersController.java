package icu.binglieyan.controller.teacher;

import icu.binglieyan.dto.UsersLoginDTO;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.UsersService;
import icu.binglieyan.vo.TeacherVO;
import icu.binglieyan.vo.UsersLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器类
 * @author binglieyan
 */
@RestController("teacherUsersController")
@RequestMapping("/teacher/users")
@Tag(name = "用户相关接口")
@Log4j2
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    /**
     * 用户登录
     * @param usersLoginDTO 用户登录信息
     * @return 登录结果
     */
    @PostMapping("/userslogin")
    @Operation(summary = "用户登录")
    public Result<UsersLoginVO> usersLogin(@RequestBody @Validated UsersLoginDTO usersLoginDTO, HttpServletRequest request){
        log.info("用户登录：{}", usersLoginDTO);
        return Result.success(usersService.usersLogin(usersLoginDTO, request));
    }

    /**
     * 查询用户信息
     * @return 用户信息
     */
    @GetMapping("/teacherQueryById")
    @Operation(summary = "查询用户信息")
    public Result<TeacherVO> teacherQueryById(){
        log.info("查询用户信息");
        return Result.success(usersService.teacherQueryById());
    }
}
