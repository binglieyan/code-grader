package icu.binglieyan.service;


import icu.binglieyan.vo.PlagiarismChecksVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author binglieyan
 */
public interface PlagiarismChecksService {
    /**
     * 发布查重任务并开始查重
     * @param assignmentId 作业ID
     */
    void publish(Long assignmentId);

    /**
     * 查询作业下的查重任务
     * @param assignmentId 作业ID
     * @return 查重任务列表
     */
    List<PlagiarismChecksVO> queryPlagiarismChecks(Long assignmentId);

    /**
     * 下载查重报告
     *
     * @param id       查重任务ID
     * @param response HTTP响应
     */
    void download(Long id, HttpServletResponse response);
}
