package icu.binglieyan.controller.admin;

import icu.binglieyan.dto.UsersDTO;
import icu.binglieyan.dto.UsersLoginDTO;
import icu.binglieyan.dto.UsersPageQueryDTO;
import icu.binglieyan.dto.UsersUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.UsersService;
import icu.binglieyan.vo.UsersLoginVO;
import icu.binglieyan.vo.UsersPageQueryVO;
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
@RestController("adminUsersController")
@RequestMapping("/admin/users")
@Tag(name = "用户相关接口")
@Log4j2
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    /**
     * 添加用户
     * @param usersDTO 用户数据传输对象，包含用户的基本信息
     * @return 添加结果
     */
    @PostMapping("/addUsers")
    @Operation(summary = "添加用户")
    public Result<Void> addUsers(@RequestBody @Validated UsersDTO usersDTO){
        log.info("添加用户信息：{}", usersDTO);
        usersService.addUsers(usersDTO);
        return Result.success();
    }

    /**
     * 删除用户
     * @param userNumber 用户编号
     */
    @DeleteMapping("/deleteUsers/{userNumber}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUsers(@PathVariable String userNumber){
        log.info("删除用户编号：{}", userNumber);
        usersService.deleteUsers(userNumber);
        return Result.success();
    }

    /**
     * 修改用户信息
     * @param usersUpdateDTO 用户修改信息
     * @return 修改结果
     */
    @PutMapping("/updateUsers")
    @Operation(summary = "修改用户信息")
    public Result<Void> updateUsers(@RequestBody @Validated UsersUpdateDTO usersUpdateDTO){
        log.info("修改用户信息：{}", usersUpdateDTO);
        usersService.updateUsers(usersUpdateDTO);
        return Result.success();
    }

    /**
     * 分页查询用户信息
     * @param usersPageQueryDTO 查询条件
     * @return 查询结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询用户信息")
    public Result<PageResult<UsersPageQueryVO>> pageQuery(@RequestBody UsersPageQueryDTO usersPageQueryDTO){
        log.info("分页查询用户信息：{}", usersPageQueryDTO);
        return Result.success(usersService.pageQuery(usersPageQueryDTO));
    }

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

}
