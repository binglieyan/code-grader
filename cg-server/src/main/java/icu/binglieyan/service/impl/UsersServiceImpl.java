package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.JwtClaimsConstant;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.context.BaseContext;
import icu.binglieyan.dto.UsersDTO;
import icu.binglieyan.dto.UsersLoginDTO;
import icu.binglieyan.dto.UsersPageQueryDTO;
import icu.binglieyan.dto.UsersUpdateDTO;
import icu.binglieyan.entity.Classes;
import icu.binglieyan.entity.DictData;
import icu.binglieyan.entity.Major;
import icu.binglieyan.entity.Users;
import icu.binglieyan.exception.*;
import icu.binglieyan.mapper.ClassesMapper;
import icu.binglieyan.mapper.DictDataMapper;
import icu.binglieyan.mapper.MajorMapper;
import icu.binglieyan.mapper.UsersMapper;
import icu.binglieyan.properties.JwtProperties;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.service.UsersService;
import icu.binglieyan.utils.JwtUtil;
import icu.binglieyan.vo.StudentVO;
import icu.binglieyan.vo.TeacherVO;
import icu.binglieyan.vo.UsersLoginVO;
import icu.binglieyan.vo.UsersPageQueryVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.time.Duration;


/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    private final MajorMapper majorMapper;
    private final ClassesMapper classesMapper;
    private final UsersMapper usersMapper;
    private final RedisTemplate<String,Object> redisTemplate;
    private final JwtProperties jwtProperties;
    private final DictDataMapper dictDataMapper;

    /**
     * 角色对应的路径前缀
     */
    private static final Map<String, String> ROLE_PATH_PREFIX = Map.of(
            "/admin/", "ADMIN",
            "/student/", "STUDENT",
            "/teacher/", "TEACHER"
    );
    private static final String ADMIN_ROLE_CODE = "ADMIN";

    /**
     * 添加用户
     * @param usersDTO 用户数据传输对象，包含用户的基本信息
     */
    @Override
    public void addUsers(UsersDTO usersDTO) {
        //1. 判断用户名是否已存在
        if (StringUtils.isNotBlank(usersDTO.getUsername())) {
           if (this.exists(new LambdaQueryWrapper<Users>().eq(Users::getUsername, usersDTO.getUsername()))){
               throw new UsersException(MessageConstant.USERNAME_ALREADY_EXISTS);
           }
        }

        //2. 校验用户代码是否已存在
        if (StringUtils.isNotBlank(usersDTO.getUserNumber())) {
            if (this.exists(new LambdaQueryWrapper<Users>().eq(Users::getUserNumber, usersDTO.getUserNumber()))){
                throw new UsersException(MessageConstant.USER_NUMBER_ALREADY_EXISTS);
            }
        }

        //3. 校验邮箱是否已存在
        if (StringUtils.isNotBlank(usersDTO.getEmail())) {
            if (this.exists(new LambdaQueryWrapper<Users>().eq(Users::getEmail, usersDTO.getEmail()))){
                throw new UsersException(MessageConstant.EMAIL_ALREADY_EXISTS);
            }
        }

        if (ADMIN_ROLE_CODE.equals(usersDTO.getRoleCode())) {
            throw new UsersException(MessageConstant.CREATE_ADMIN_NOT_ALLOWED);
        }

        if (!dictDataMapper.exists(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDataCode, usersDTO.getRoleCode())
                .eq(DictData::getActive, true))) {
            throw new DictDataException(MessageConstant.DICT_DATA_NOT_FOUND);
        }

        Users users = new Users();
        BeanUtils.copyProperties(usersDTO, users);

        //4. 密码加密
        users.setPassword(BCrypt.hashpw(usersDTO.getPassword(), BCrypt.gensalt()));


        //5. 专业编码 -> 专业ID
        if (StringUtils.isNotBlank(usersDTO.getMajorCode())) {
            Major major = majorMapper.selectOne(
                    new LambdaQueryWrapper<Major>()
                            .eq(Major::getMajorCode, usersDTO.getMajorCode()));
            if (major == null) {
                throw new MajorException(MessageConstant.MAJOR_NOT_FOUND);
            }
            users.setMajorId(major.getId());
        }

        //6. 班级编码 -> 班级ID
        if (StringUtils.isNotBlank(usersDTO.getClassCode())) {
            Classes classes = classesMapper.selectOne(
                    new LambdaQueryWrapper<Classes>()
                            .eq(Classes::getClassCode, usersDTO.getClassCode())
            );
            if (classes == null) {
                throw new ClassesException(MessageConstant.CLASSES_NOT_FOUND);
            }
            users.setClassId(classes.getId());
        }

        //7. 保存用户
        this.save(users);
    }

    /**
     * 删除用户
     * @param userNumber 用户编号
     */
    @Override
    public void deleteUsers(String userNumber) {
        // 查询用户是否存在
        Users users = this.getOne(new LambdaQueryWrapper<Users>()
                        .eq(Users::getUserNumber, userNumber)
                        .select(Users::getRoleCode));
        if (users == null) {
            throw new UsersException(MessageConstant.USER_NOT_FOUND);
        }
        if (ADMIN_ROLE_CODE.equals(users.getRoleCode())) {
            throw new UsersException(MessageConstant.DELETE_ADMIN_NOT_ALLOWED);
        }
        this.lambdaUpdate().eq(Users::getUserNumber, userNumber).remove();
    }

    /**
     * 修改用户信息
     *
     * @param usersUpdateDTO 用户修改信息
     */
    @Override
    public void updateUsers(UsersUpdateDTO usersUpdateDTO) {
        //1. 查询用户是否存在
        Users users = this.getOne(new LambdaQueryWrapper<Users>()
                .eq(Users::getUserNumber, usersUpdateDTO.getUserNumber())
                .select(Users::getId));
        if (users == null) {
            throw new UsersException(MessageConstant.USER_NOT_FOUND);
        }
        //2. 手动设置每个非空字段
        LambdaUpdateWrapper<Users> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Users::getId, users.getId());
        if (StringUtils.isNotBlank(usersUpdateDTO.getUsername())) {
            updateWrapper.set(Users::getUsername, usersUpdateDTO.getUsername());
        }
        if (StringUtils.isNotBlank(usersUpdateDTO.getEmail())) {
            updateWrapper.set(Users::getEmail, usersUpdateDTO.getEmail());
        }
        if (StringUtils.isNotBlank(usersUpdateDTO.getRealName())) {
            updateWrapper.set(Users::getRealName, usersUpdateDTO.getRealName());
        }
        if (StringUtils.isNotBlank(usersUpdateDTO.getUserNumber())) {
            updateWrapper.set(Users::getUserNumber, usersUpdateDTO.getUserNumber());
        }
        //3. 专业编码 -> 专业ID
        if (StringUtils.isNotBlank(usersUpdateDTO.getMajorCode())) {
            Major major = majorMapper.selectOne(
                    new LambdaQueryWrapper<Major>()
                            .eq(Major::getMajorCode, usersUpdateDTO.getMajorCode())
            );
            if (major == null) {
                throw new MajorException(MessageConstant.MAJOR_NOT_FOUND);
            }
            updateWrapper.set(Users::getMajorId, major.getId());

        }
        //4. 班级编码 -> 班级ID
        if (StringUtils.isNotBlank(usersUpdateDTO.getClassCode())) {
            Classes classes = classesMapper.selectOne(
                    new LambdaQueryWrapper<Classes>()
                            .eq(Classes::getClassCode, usersUpdateDTO.getClassCode())
            );
            if (classes == null) {
                throw new ClassesException(MessageConstant.CLASSES_NOT_FOUND);
            }
            updateWrapper.set(Users::getClassId, classes.getId());
        }
        //5. 更新用户
        this.update(updateWrapper);

    }

    /**
     * 分页查询用户
     * @param usersPageQueryDTO 用户分页查询信息
     * @return 用户分页查询结果
     */
    @Override
    public PageResult<UsersPageQueryVO> pageQuery(UsersPageQueryDTO usersPageQueryDTO) {
        Integer pageNum = usersPageQueryDTO.getPageNum();
        Integer pageSize = usersPageQueryDTO.getPageSize();
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        // 使用MyBatis-Plus进行分页查询
        Page<UsersPageQueryVO> page = new Page<>(pageNum, pageSize);
        usersMapper.pageQuery(page, usersPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }

    /**
     * 用户登录
     *
     * @param usersLoginDTO 用户登录信息
     * @param request 请求对象
     * @return 用户登录信息
     */
    @Override
    public UsersLoginVO usersLogin(UsersLoginDTO usersLoginDTO, HttpServletRequest request) {
        String userNumber = usersLoginDTO.getUserNumber();
        String password = usersLoginDTO.getPassword();

        // 先从 Redis 中查找用户信息
        String redisKey = "user:" + userNumber;
        @SuppressWarnings("unchecked")
        Map<String, Object> userMap = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);

        Users users;
        if (userMap == null){
            // Redis 未命中：查询数据库
            users = usersMapper.selectOne(
                    new LambdaQueryWrapper<Users>()
                            .eq(Users::getUserNumber, userNumber)
                            .select(Users::getId, Users::getUsername, Users::getUserNumber,
                                    Users::getRoleCode, Users::getPassword));
            if (users == null) {
                throw new UsersException(MessageConstant.USER_NOT_FOUND);
            }

            // 验证密码
            if (!BCrypt.checkpw(password, users.getPassword())){
                throw new UsersException(MessageConstant.PASSWORD_ERROR);
            }

            // 角色校验
            checkUserRole(request, users.getRoleCode());

            // 缓存完整信息到 Redis（包含所有字段供后续使用）
            Map<String, Object> userInfo = creatUserInfoMap(users);
            redisTemplate.opsForValue().set(redisKey, userInfo, Duration.ofHours(4));

        } else {
            // Redis 命中：从 Redis 重建用户信息
            users = Users.builder()
                    .id(Long.parseLong(userMap.get("id").toString()))
                    .username(userMap.get("username").toString())
                    .password(userMap.get("password").toString())
                    .roleCode(userMap.get("roleCode").toString())
                    .userNumber(userMap.get("userNumber").toString())
                    .build();

            // Redis 已命中说明之前验证过密码，这里只需要验证角色
            checkUserRole(request, users.getRoleCode());
        }
        return handleTokenStorage(users);
    }

    /**
     * 检查用户角色是否与请求路径匹配
     */
    private void checkUserRole(HttpServletRequest request, String roleCode) {
        String requestUri = request.getRequestURI();
        for (Map.Entry<String, String> entry : ROLE_PATH_PREFIX.entrySet()) {
            if (requestUri.startsWith(entry.getKey())) {
                if (!entry.getValue().equals(roleCode)) {
                    throw new UsersException(MessageConstant.USER_ROLE_ERROR);
                }
                break;
            }
        }
    }

    /**
     * 处理 Token 的生成和 Redis 存储
     */
    private UsersLoginVO handleTokenStorage(Users users) {
        Map<String, Object> claims = new HashMap<>(3);
        claims.put(JwtClaimsConstant.USER_ID, users.getId());
        claims.put(JwtClaimsConstant.USER_NUMBER, users.getUserNumber());
        claims.put(JwtClaimsConstant.USER_ROLE_CODE, users.getRoleCode());

        String token = JwtUtil.createJwt(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );

        // 为每个用户只保留一个 token 引用
        String userTokenKey = "user:token:" + users.getId();
        String oldToken = (String) redisTemplate.opsForValue().get(userTokenKey);

        // 删除旧的 token 登录状态
        if (oldToken != null) {
            String oldTokenKey = "login:user:" + oldToken;
            redisTemplate.delete(oldTokenKey);
        }

        // 存储新的 Token 到 Redis
        String tokenKey = "login:user:" + token;
        Map<String, Object> userLoginInfo = new HashMap<>(1);
        // 只存储用户 ID 用于令牌验证
        userLoginInfo.put("id", users.getId());
        Duration tokenDuration = Duration.ofMillis(jwtProperties.getUserTtl() + 5 * 60 * 1000);
        redisTemplate.opsForValue().set(
                tokenKey,
                userLoginInfo,
                tokenDuration
        );

        // 修改用户 token 引用
        redisTemplate.opsForValue().set(
                userTokenKey,
                token,
                tokenDuration
        );

        return UsersLoginVO.builder()
                .username(users.getUsername())
                .userNumber(users.getUserNumber())
                .token(token)
                .build();
    }

    /**
     * 创建用户信息Map
     * @param users 用户信息
     * @return 用户信息Map
     */
    private Map<String, Object> creatUserInfoMap(Users users) {
        Map<String, Object> userInfo = new HashMap<>(8);
        userInfo.put("id", users.getId());
        userInfo.put("username", users.getUsername());
        userInfo.put("password", users.getPassword());
        userInfo.put("roleCode", users.getRoleCode());
        userInfo.put("userNumber", users.getUserNumber());
        return userInfo;
    }

    /**
     * 学生加入班级
     * @param classCode 班级代码
     */
    @Override
    public void joinClass(String classCode) {
        if (StringUtils.isBlank(classCode)) {
            throw new ClassesException(MessageConstant.CODE_NOT_NULL);
        }
        //1. 班级代码 -> 班级ID
        LambdaQueryWrapper<Classes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Classes::getClassCode, classCode);
        Classes classes = classesMapper.selectOne(queryWrapper);
        if (classes == null) {
            throw new ClassesException(MessageConstant.CLASSES_NOT_FOUND);
        }

        //2. 获取当前用户ID
        Optional<Long> studentIdOpt = BaseContext.getCurrentId();
        if (studentIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }

        //3.如果已经有班级则拒绝加入
        LambdaQueryWrapper<Users> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Users::getId, studentIdOpt.get());
        queryWrapper1.select(Users::getClassId);
        if (this.exists(queryWrapper1)){
            throw new ClassesException(MessageConstant.STUDENT_ALREADY_JOIN_CLASSES);
        }

        //3. 修改用户信息
        LambdaUpdateWrapper<Users> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Users::getId, studentIdOpt.get())
                .set(Users::getClassId, classes.getId());
        this.update(updateWrapper);
    }

    /**
     * 学生查询信息
     * @return 学生信息
     */
    @Override
    public StudentVO studentQueryById() {
        Optional<Long> studentIdOpt = BaseContext.getCurrentId();
        if (studentIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        Users users = this.getOne(new LambdaQueryWrapper<Users>()
                .eq(Users::getId, studentIdOpt.get())
                .select(
                        Users::getUsername,
                        Users::getEmail,
                        Users::getRealName,
                        Users::getUserNumber,
                        Users::getClassId
                ));

        String classCode = "";
        if (users.getClassId() != null) {
            Classes classes = classesMapper.selectOne(
                    new LambdaQueryWrapper<Classes>()
                            .eq(Classes::getId, users.getClassId())
                            .select(Classes::getClassCode));
            if (classes != null) {
                classCode = classes.getClassCode();
            }
        }
        
        return StudentVO.builder()
                .username(users.getUsername())
                .email(users.getEmail())
                .realName(users.getRealName())
                .userNumber(users.getUserNumber())
                .classCode(classCode)
                .build();
    }

    /**
     * 教师查询信息
     * @return 教师信息
     */
    @Override
    public TeacherVO teacherQueryById() {
        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        if (teacherIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        Users users = this.getOne(new LambdaQueryWrapper<Users>()
                .eq(Users::getId, teacherIdOpt.get())
                .select(
                        Users::getUsername,
                        Users::getEmail,
                        Users::getRealName,
                        Users::getUserNumber,
                        Users::getMajorId
                ));

        String majorCode = "";
        if (users.getMajorId() != null) {
            Major major = majorMapper.selectOne(
                    new LambdaQueryWrapper<Major>()
                            .eq(Major::getId, users.getMajorId())
                            .select(Major::getMajorCode));
            if (major != null) {
                majorCode = major.getMajorCode();
            }
        }
        
        return TeacherVO.builder()
                .username(users.getUsername())
                .email(users.getEmail())
                .realName(users.getRealName())
                .userNumber(users.getUserNumber())
                .majorCode(majorCode)
                .build();
    }

}
