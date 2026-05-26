package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.context.BaseContext;
import icu.binglieyan.entity.DictData;
import icu.binglieyan.entity.PlagiarismChecks;
import icu.binglieyan.exception.DictDataException;
import icu.binglieyan.exception.PlagiarismChecksException;
import icu.binglieyan.exception.UserScopeException;
import icu.binglieyan.mapper.DictDataMapper;
import icu.binglieyan.mapper.PlagiarismChecksMapper;
import icu.binglieyan.service.PlagiarismChecksService;
import icu.binglieyan.service.PlagiarismComparisonsService;
import icu.binglieyan.vo.PlagiarismChecksVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class PlagiarismChecksServiceImpl extends ServiceImpl<PlagiarismChecksMapper, PlagiarismChecks> implements PlagiarismChecksService {

    private final DictDataMapper dictDataMapper;
    private final PlagiarismComparisonsService plagiarismComparisonsService;
    @Value("${cg.uploadFile.uploadDir}")
    private String uploadDir;
    @Value("${cg.outputFile.outputDir}")
    private String outputDir;

    /**
     * 发布查重任务并开始查重
     *
     * @param assignmentId 作业ID
     */
    @Override
    public void publish(Long assignmentId) {
        if (assignmentId == null) {
            throw new PlagiarismChecksException(MessageConstant.ID_NOT_NULL);
        }
        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        if (teacherIdOpt.isEmpty()) {
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        if (!dictDataMapper.exists(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDataCode, "PENDING")
                .eq(DictData::getActive, true))) {
            throw new DictDataException(MessageConstant.DICT_DATA_NOT_FOUND);
        }
        PlagiarismChecks plagiarismChecks = PlagiarismChecks.builder()
                .assignmentId(assignmentId)
                .initiatedById(teacherIdOpt.get())
                .statusCode("PENDING")
                .build();
        this.save(plagiarismChecks);

        String checkDir = uploadDir + "/" + assignmentId;
        String outputCheckDir = outputDir + "/" + assignmentId + plagiarismChecks.getId();
        plagiarismComparisonsService.startCheck(checkDir, outputCheckDir, plagiarismChecks.getId());
    }

    /**
     * 获取查重任务
     *
     * @param assignmentId 作业ID
     * @return 查重任务
     */
    @Override
    public List<PlagiarismChecksVO> queryPlagiarismChecks(Long assignmentId) {
        if (assignmentId == null) {
            throw new PlagiarismChecksException(MessageConstant.ID_NOT_NULL);
        }
        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        if (teacherIdOpt.isEmpty()) {
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        Optional<String> teacherNumberOpt = BaseContext.getCurrentUserNumber();
        if (teacherNumberOpt.isEmpty()) {
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        List<PlagiarismChecks> plagiarismChecksList = this.list(new LambdaQueryWrapper<PlagiarismChecks>()
                .eq(PlagiarismChecks::getAssignmentId, assignmentId)
                .eq(PlagiarismChecks::getInitiatedById, teacherIdOpt.get()));
        
        if (plagiarismChecksList.isEmpty()) {
            return List.of();
        }
        
        // 批量查询字典数据（避免N+1）
        List<String> statusCodes = plagiarismChecksList.stream()
                .map(PlagiarismChecks::getStatusCode)
                .distinct()
                .toList();
        
        List<DictData> dictDataList = dictDataMapper.selectList(
                new LambdaQueryWrapper<DictData>()
                        .in(DictData::getDataCode, statusCodes)
                        .select(DictData::getDataCode, DictData::getDataValue));
        
        Map<String, String> statusCodeToValueMap = dictDataList.stream()
                .collect(Collectors.toMap(DictData::getDataCode, DictData::getDataValue));
        
        String teacherNumber = teacherNumberOpt.get();
        
        return plagiarismChecksList.stream()
                .map(plagiarismChecks -> {
                    String statusValue = statusCodeToValueMap.getOrDefault(plagiarismChecks.getStatusCode(), "");
                    return PlagiarismChecksVO.builder()
                            .id(plagiarismChecks.getId())
                            .assignmentId(plagiarismChecks.getAssignmentId())
                            .initiatedByNumber(teacherNumber)
                            .totalComparisons(plagiarismChecks.getTotalComparisons())
                            .executionTime(plagiarismChecks.getExecutionTime())
                            .statusValue(statusValue)
                            .errorMessage(plagiarismChecks.getErrorMessage())
                            .startTime(plagiarismChecks.getStartTime())
                            .completedAt(plagiarismChecks.getCompletedAt())
                            .build();
                })
                .toList();
    }

    /**
     * 下载查重结果
     *
     * @param id       查重任务ID
     * @param response 响应
     */
    @Override
    public void download(Long id, HttpServletResponse response) {
        if (id == null) {
            throw new PlagiarismChecksException(MessageConstant.ID_NOT_NULL);
        }
        PlagiarismChecks plagiarismChecks = this.getOne(new LambdaQueryWrapper<PlagiarismChecks>()
                .eq(PlagiarismChecks::getId, id)
                .select(
                        PlagiarismChecks::getReportPath,
                        PlagiarismChecks::getInitiatedById
                ));
        if (plagiarismChecks == null) {
            throw new PlagiarismChecksException(MessageConstant.PLAGIARISM_CHECKS_NOT_FOUND);
        }
        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        if (teacherIdOpt.isEmpty()) {
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        if (!teacherIdOpt.get().equals(plagiarismChecks.getInitiatedById())) {
            throw new PlagiarismChecksException(MessageConstant.PLAGIARISM_CHECKS_NOT_MATCH);
        }
        String reportPath = plagiarismChecks.getReportPath();
        if (StringUtils.isBlank(reportPath)) {
            throw new PlagiarismChecksException(MessageConstant.REPORT_NOT_GENERATED);
        }
        Path reportFilePath = Path.of(reportPath);
        if (!Files.exists(reportFilePath)) {
            throw new PlagiarismChecksException(MessageConstant.REPORT_FILE_NOT_EXIST);
        }
        String fileName = reportFilePath.getFileName().toString();
        response.setContentType("application/octet-stream");
        try {
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + java.net.URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8));
            response.setContentLengthLong(Files.size(reportFilePath));
        } catch (IOException e) {
            log.error("设置响应头失败", e);
        }

        // 5. 写入文件流
        try (InputStream inputStream = Files.newInputStream(reportFilePath);
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("下载查重报告失败", e);
            throw new PlagiarismChecksException(MessageConstant.DOWNLOAD_PLAGIARISM_CHECKS_FAILED);
        }
    }
}
