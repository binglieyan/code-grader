package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.dto.MajorDTO;
import icu.binglieyan.dto.MajorPageQueryDTO;
import icu.binglieyan.dto.MajorUpdateDTO;
import icu.binglieyan.entity.Department;
import icu.binglieyan.entity.Major;
import icu.binglieyan.exception.DepartmentException;
import icu.binglieyan.exception.MajorException;
import icu.binglieyan.mapper.DepartmentMapper;
import icu.binglieyan.mapper.MajorMapper;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.service.MajorService;
import icu.binglieyan.vo.MajorPageQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {

    private final DepartmentMapper departmentMapper;
    private final MajorMapper majorMapper;

    /**
     * 添加专业
     * @param majorDTO 专业数据传输对象，包含了专业信息
     */
    @Override
    public void addMajor(MajorDTO majorDTO) {
        //1. 校验专业代码是否存在
        if (StringUtils.isNotBlank(majorDTO.getMajorCode())) {
            if (this.exists(new LambdaQueryWrapper<Major>().eq(Major::getMajorCode, majorDTO.getMajorCode()))) {
                throw new MajorException(MessageConstant.MAJOR_CODE_EXISTS);
            }
        }

        //2 校验专业名称是否存在
        if (StringUtils.isNotBlank(majorDTO.getMajorName())) {
            if (this.exists(new LambdaQueryWrapper<Major>().eq(Major::getMajorName, majorDTO.getMajorName()))) {
                throw new MajorException(MessageConstant.MAJOR_NAME_EXISTS);
            }
        }

        //3 院系编码 -> 院系ID
        Major major = null;
        if (StringUtils.isNotBlank(majorDTO.getDepartmentCode())) {
            Department department = departmentMapper.selectOne(
                    new LambdaQueryWrapper<Department>()
                            .eq(Department::getDepartmentCode, majorDTO.getDepartmentCode())
                            .select(Department::getId)
            );
            if (department == null) {
                throw new DepartmentException(MessageConstant.DEPARTMENT_NOT_FOUND);
            }
            major = Major.builder()
                    .majorCode(majorDTO.getMajorCode())
                    .majorName(majorDTO.getMajorName())
                    .departmentId(department.getId())
                    .build();
        }
        this.save(major);
    }

    /**
     * 删除专业
     * @param majorCode 专业编号
     */
    @Override
    public void deleteMajor(String majorCode) {
        if (StringUtils.isBlank(majorCode)) {
            throw new MajorException(MessageConstant.CODE_NOT_NULL);
        }
        // 查询专业是否存在
        if (!this.exists(new LambdaQueryWrapper<Major>().eq(Major::getMajorCode, majorCode))) {
            throw new MajorException(MessageConstant.MAJOR_NOT_FOUND);
        }
        this.lambdaUpdate().eq(Major::getMajorCode, majorCode).remove();
    }

    /**
     * 修改专业信息
     * @param majorUpdateDTO 专业信息
     */
    @Override
    public void updateMajor(MajorUpdateDTO majorUpdateDTO) {
        //1. 查询专业是否存在
        Major major = this.getOne(new LambdaQueryWrapper<Major>().eq(Major::getMajorCode, majorUpdateDTO.getMajorCode()));
        if (major == null){
            throw new MajorException(MessageConstant.MAJOR_NOT_FOUND);
        }
        //2. 手动设置每个非空字段
        LambdaUpdateWrapper<Major> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Major::getId, major.getId());
        if (StringUtils.isNotBlank(majorUpdateDTO.getMajorName())){
            updateWrapper.set(Major::getMajorName, majorUpdateDTO.getMajorName());
        }
        if (StringUtils.isNotBlank(majorUpdateDTO.getMajorCode())){
            updateWrapper.set(Major::getMajorCode, majorUpdateDTO.getMajorCode());
        }
        //3. 系编码 -> 系ID
        if (StringUtils.isNotBlank(majorUpdateDTO.getDepartmentCode())){
            Department department = departmentMapper.selectOne(
                    new LambdaQueryWrapper<Department>()
                            .eq(Department::getDepartmentCode, majorUpdateDTO.getDepartmentCode())
            );
            if (department == null) {
                throw new DepartmentException(MessageConstant.DEPARTMENT_NOT_FOUND);
            }
            updateWrapper.set(Major::getDepartmentId, department.getId());
        }
        //4. 更新专业
        this.update(updateWrapper);
    }

    /**
     * 分页查询专业信息
     * @param majorPageQueryDTO 查询条件
     * @return 查询结果
     */
    @Override
    public PageResult<MajorPageQueryVO> pageQuery(MajorPageQueryDTO majorPageQueryDTO) {
        Integer pageNum = majorPageQueryDTO.getPageNum();
        Integer pageSize = majorPageQueryDTO.getPageSize();
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        // 使用MyBatis-Plus进行分页查询
        Page<MajorPageQueryVO> page = new Page<>(pageNum, pageSize);
        majorMapper.pageQuery(page, majorPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }
}
