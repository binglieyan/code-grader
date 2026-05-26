package icu.binglieyan.service;

import icu.binglieyan.vo.PlagiarismComparisonsVO;

import java.util.List;

/**
 * @author binglieyan
 */
public interface PlagiarismComparisonsService {

    /**
     * 开始查重
     *
     * @param checkDir       查重文件目录
     * @param outputCheckDir 输出报告目录
     * @param plagiarismCheckId 查重任务ID
     */
    void startCheck(String checkDir, String outputCheckDir, Long plagiarismCheckId);

    /**
     * 查询查重结果
     *
     * @param plagiarismCheckId 查重任务ID
     * @return 查重结果
     */
    List<PlagiarismComparisonsVO> queryPlagiarismComparisons(Long plagiarismCheckId);
}
