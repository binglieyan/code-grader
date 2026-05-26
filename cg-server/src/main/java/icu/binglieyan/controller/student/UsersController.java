package icu.binglieyan.controller.student;

import icu.binglieyan.dto.UsersLoginDTO;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.UsersService;
import icu.binglieyan.vo.StudentVO;
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
@RestController("studentUsersController")
@RequestMapping("/student/users")
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
     * 学生加入班级
     * @param classCode 班级代码
     */
    @PutMapping("/joinClass/{classCode}")
    @Operation(summary = "学生加入班级")
    public Result<Void> joinClass(@PathVariable String classCode){
        log.info("班级代码：{}", classCode);
        usersService.joinClass(classCode);
        return Result.success();
    }

    /**
     * 查询学生信息
     * @return 学生信息
     */
    @GetMapping("/studentQueryById")
    @Operation(summary = "查询学生信息")
    public Result<StudentVO> studentQueryById(){
        log.info("查询用户信息");
        return Result.success(usersService.studentQueryById());
    }
}
