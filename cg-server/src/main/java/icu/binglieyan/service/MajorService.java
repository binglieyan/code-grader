package icu.binglieyan.service;

import icu.binglieyan.dto.MajorDTO;
import icu.binglieyan.dto.MajorPageQueryDTO;
import icu.binglieyan.dto.MajorUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.vo.MajorPageQueryVO;

/**
 * @author binglieyan
 */
public interface MajorService {

    /**
     * 添加专业
     * @param majorDTO 专业数据传输对象，包含了专业信息
     */
    void addMajor(MajorDTO majorDTO);

    /**
     * 删除专业
     * @param majorCode 专业编号
     */
    void deleteMajor(String majorCode);

    /**
     * 修改专业信息
     * @param majorUpdateDTO 专业信息
     */
    void updateMajor(MajorUpdateDTO majorUpdateDTO);

    /**
     * 分页查询专业信息
     * @param majorPageQueryDTO 查询条件
     * @return 查询结果
     */
    PageResult<MajorPageQueryVO> pageQuery(MajorPageQueryDTO majorPageQueryDTO);
}
