package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.ReportObjectFactory;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.dto.PlagiarismChecksUpdateDTO;
import icu.binglieyan.entity.DictData;
import icu.binglieyan.entity.PlagiarismChecks;
import icu.binglieyan.entity.PlagiarismComparisons;
import icu.binglieyan.exception.DictDataException;
import icu.binglieyan.exception.PlagiarismChecksException;
import icu.binglieyan.exception.PlagiarismComparisonsException;
import icu.binglieyan.mapper.DictDataMapper;
import icu.binglieyan.mapper.PlagiarismChecksMapper;
import icu.binglieyan.mapper.PlagiarismComparisonsMapper;
import icu.binglieyan.service.PlagiarismComparisonsService;
import icu.binglieyan.vo.PlagiarismComparisonsVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
/**
 @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class PlagiarismComparisonsServiceImpl extends ServiceImpl<PlagiarismComparisonsMapper, PlagiarismComparisons> implements PlagiarismComparisonsService {

    private final ObjectMapper objectMapper;
    private final PlagiarismComparisonsMapper plagiarismComparisonsMapper;
    private final PlagiarismChecksMapper plagiarismChecksMapper;
    private final DictDataMapper dictDataMapper;

    @Override
    public void startCheck(String checkDir, String outputCheckDir, Long plagiarismCheckId) {
        PlagiarismChecksUpdateDTO plagiarismChecksUpdateDTO = new PlagiarismChecksUpdateDTO();
        plagiarismChecksUpdateDTO.setId(plagiarismCheckId);
        plagiarismChecksUpdateDTO.setStartTime(LocalDateTime.now());
        plagiarismChecksUpdateDTO.setStatusCode("PROCESSING");

        Language language = new JavaLanguage();
        Set<File> submissionDirectories = Set.of(new File(checkDir));
        JPlagOptions options = new JPlagOptions(language, submissionDirectories, Set.of());
        try {
            JPlagResult result = JPlag.run(options);
            // Optional
            String outputFile = outputCheckDir + "output.zip";
            String outputFile1 = outputCheckDir + "output.jplag";
            ReportObjectFactory reportObjectFactory = new ReportObjectFactory(new File(outputFile));
            ReportObjectFactory reportObjectFactory1 = new ReportObjectFactory(new File(outputFile1));
            reportObjectFactory.createAndSaveReport(result);
            reportObjectFactory1.createAndSaveReport(result);
            plagiarismChecksUpdateDTO.setCompletedAt(LocalDateTime.now());
            plagiarismChecksUpdateDTO.setReportPath(outputFile1);
            unzipFile(outputFile, outputCheckDir);
            // 提取数据并保存到数据库
            ProcessResult processResult = processAndSaveComparisonData(outputCheckDir, plagiarismCheckId);
            plagiarismChecksUpdateDTO.setTotalComparisons(processResult.getTotalComparisons());
            plagiarismChecksUpdateDTO.setExecutionTime(processResult.getExecutionTime());
            plagiarismChecksUpdateDTO.setStatusCode("COMPLETED");
        } catch (ExitException | FileNotFoundException e) {
            log.error("JPlag查重错误", e);
            plagiarismChecksUpdateDTO.setStatusCode("FAILED");
            plagiarismChecksUpdateDTO.setErrorMessage(e.getMessage());
            throw new PlagiarismComparisonsException(MessageConstant.JPLAG_ERROR);
        }
        finally {
            updatePlagiarismChecks(plagiarismChecksUpdateDTO);
        }
    }

    /**
     * 查询查重结果
     * @param plagiarismCheckId 查重任务ID
     * @return 查重结果
     */
    @Override
    public List<PlagiarismComparisonsVO> queryPlagiarismComparisons(Long plagiarismCheckId) {
        if (plagiarismCheckId == null) {
            throw new PlagiarismComparisonsException(MessageConstant.ID_NOT_NULL);
        }
        LambdaQueryWrapper<PlagiarismComparisons> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PlagiarismComparisons::getPlagiarismCheckId, plagiarismCheckId);
        List<PlagiarismComparisons> comparisonsList = this.list(queryWrapper);
        return comparisonsList.stream()
                .map(comparison -> {
                    PlagiarismComparisonsVO plagiarismComparisonsVO = new PlagiarismComparisonsVO();
                    BeanUtils.copyProperties(comparison, plagiarismComparisonsVO);
                    return plagiarismComparisonsVO;
                })
                .toList();
    }


    /**
     从解压后的JSON文件中提取数据并保存到数据库
     @param outputCheckDir 解压后的目录
     @param plagiarismCheckId 查重任务ID
     */
    private ProcessResult processAndSaveComparisonData(String outputCheckDir, Long plagiarismCheckId) {
        try {
            // 获取所有比较结果的JSON文件
            List<String> comparisonFiles = findComparisonJsonFiles(outputCheckDir);
            log.info("找到 {} 个JSON文件", comparisonFiles.size());
            // 打印所有找到的文件路径
            for (String file : comparisonFiles) {
                log.info("JSON文件: {}", file);
            }
            List<PlagiarismComparisons> comparisonsList  = new ArrayList<>();


            for (String filePath : comparisonFiles) {
                log.info("处理文件: {}", filePath);
                List<ComparisonData> comparisonDataList = parseComparisonJson(filePath);
                for (ComparisonData data : comparisonDataList) {

                    PlagiarismComparisons comparison = buildPlagiarismComparison(
                            data.getFirstSubmission(),
                            data.getSecondSubmission(),
                            data.getSimilarities()
                    );

                    // 设置查重任务ID（可以根据实际情况调整）
                    comparison.setPlagiarismCheckId(plagiarismCheckId);
                    comparison.setMatchDetailsPath(filePath);

                    comparisonsList.add(comparison);
                }
            }

            // 批量保存到数据库
            if (!comparisonsList.isEmpty()) {
                plagiarismComparisonsMapper.insert(comparisonsList);
                log.info("成功保存 {} 条相似度比较记录到数据库", comparisonsList.size());
            } else {
                log.error("没有找到任何相似度比较数据");
            }

            // 解析运行信息文件
            File runInfoFile = new File(outputCheckDir, "runInformation.json");
            RunInfo runInfo = parseRunInformation(runInfoFile);
            return new ProcessResult(runInfo.getTotalComparisons(), runInfo.getExecutionTime());

        } catch (Exception e) {
            log.error("处理查重报告的数据时发生错误", e);
            throw new PlagiarismComparisonsException(MessageConstant.ERROR_PROCESSING_COMPARISON_DATA);
        }
    }

    /**
     查找所有比较结果JSON文件
     @param outputCheckDir 输出目录
     @return JSON文件路径列表
     */
    private List<String> findComparisonJsonFiles(String outputCheckDir) throws IOException {
        List<String> jsonFiles = new ArrayList<>();

        // 指定 comparisons 子文件夹路径
        String comparisonsDir = Paths.get(outputCheckDir, "comparisons").toString();

        try (var paths = Files.list(Paths.get(comparisonsDir))) {
            paths.forEach(path -> jsonFiles.add(path.toString()));
        }
        return jsonFiles;
    }

    /**
     * 解析 runInformation.json 文件，获取 executionTime 和 totalComparisons
     * @param jsonFile 指向 runInformation.json 的文件对象
     * @return RunInfo 对象
     */
    private RunInfo parseRunInformation(File jsonFile) {
        JsonNode root = objectMapper.readTree(jsonFile);
        int executionTime = root.get("executionTime").asInt();
        int totalComparisons = root.get("totalComparisons").asInt();
        return new RunInfo(executionTime, totalComparisons);
    }

    /**
     解析比较结果JSON文件
     @param filePath JSON文件路径
     @return 比较数据列表
     */
    private List<ComparisonData> parseComparisonJson(String filePath) {
        try {
            ComparisonJsonRoot root = objectMapper.readValue(
                    new File(filePath),
                    ComparisonJsonRoot.class
            );

            List<ComparisonData> dataList = new ArrayList<>();
            ComparisonData data = new ComparisonData();
            data.setFirstSubmission(root.getFirstSubmissionId());
            data.setSecondSubmission(root.getSecondSubmissionId());
            data.setSimilarities(root.getSimilarities());
            dataList.add(data);

            return dataList;
        } catch (Exception e) {
            log.error("解析JSON文件失败: {}", filePath, e);
            return new ArrayList<>();
        }
    }

    /**
     构建PlagiarismComparisons实体对象
     @param firstSubmission 第一个提交者
     @param secondSubmission 第二个提交者
     @param similarities 相似度数据
     @return PlagiarismComparisons实体对象
     */
    private PlagiarismComparisons buildPlagiarismComparison(
            String firstSubmission,
            String secondSubmission,
            Similarities similarities) {

        PlagiarismComparisons comparison = new PlagiarismComparisons();
        comparison.setFirstSubmissionName(firstSubmission);
        comparison.setSecondSubmissionName(secondSubmission);
        comparison.setAvgSimilarity(BigDecimal.valueOf(similarities.getAvg()));
        comparison.setMaxSimilarity(BigDecimal.valueOf(similarities.getMax()));
        comparison.setMaximumLength(BigDecimal.valueOf(similarities.getMaximumLength()));
        comparison.setLongestMatch(BigDecimal.valueOf(similarities.getLongestMatch()));

        return comparison;
    }

    /**
     解压 ZIP 文件到指定目录
     @param zipFilePath ZIP 文件路径
     @param destDir 目标目录
     */
    private void unzipFile(String zipFilePath, String destDir) {
        byte[] buffer = new byte[1024];

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            // 直接使用传入的目标目录，不再创建子目录
            File destDirectory = new File(destDir);
            if (!destDirectory.exists() && !destDirectory.mkdirs()) {
                log.error("创建目标目录失败：{}", destDirectory.getPath());
            }

            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                File newFile = new File(destDirectory, entryName);

                // 创建父目录
                File parentDir = newFile.getParentFile();
                if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                    log.error("创建父目录失败：{}", parentDir.getPath());
                }

                if (zipEntry.isDirectory()) {
                    if (!newFile.mkdirs() && !newFile.exists()) {
                        log.error("创建目录失败：{}", newFile.getPath());
                    }
                } else {
                    // 写入文件内容
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }

                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            log.info("ZIP 文件成功解压到：{}", destDir);

        } catch (IOException e) {
            log.error("ZIP 文件解压失败", e);
            throw new PlagiarismComparisonsException(MessageConstant.ZIP_FILE_UNZIP_FAILED);
        }
    }

    /**
     * 修改查重任务
     * @param plagiarismChecksUpdateDTO 查重任务修改信息数据传输对象，包含查重任务的修改信息
     */
    private void updatePlagiarismChecks(PlagiarismChecksUpdateDTO plagiarismChecksUpdateDTO) {
        //1. 查询任务记录是否存在
        if (!plagiarismChecksMapper.exists(
                new LambdaQueryWrapper<PlagiarismChecks>()
                        .eq(PlagiarismChecks::getId, plagiarismChecksUpdateDTO.getId()))) {
            throw new PlagiarismChecksException(MessageConstant.PLAGIARISM_CHECKS_NOT_FOUND);
        }
        //2. 手动设置每个非空字段
        LambdaUpdateWrapper<PlagiarismChecks> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PlagiarismChecks::getId, plagiarismChecksUpdateDTO.getId());
        if (plagiarismChecksUpdateDTO.getTotalComparisons() != null) {
            updateWrapper.set(PlagiarismChecks::getTotalComparisons,plagiarismChecksUpdateDTO.getTotalComparisons());
        }
        if (plagiarismChecksUpdateDTO.getExecutionTime() != null) {
            updateWrapper.set(PlagiarismChecks::getExecutionTime,plagiarismChecksUpdateDTO.getExecutionTime());
        }
        if (StringUtils.isNotBlank(plagiarismChecksUpdateDTO.getReportPath())) {
            updateWrapper.set(PlagiarismChecks::getReportPath,plagiarismChecksUpdateDTO.getReportPath());
        }
        if (StringUtils.isNotBlank(plagiarismChecksUpdateDTO.getStatusCode())) {
            if (!dictDataMapper.exists(new LambdaQueryWrapper<DictData>()
                    .eq(DictData::getDataCode, plagiarismChecksUpdateDTO.getStatusCode())
                    .eq(DictData::getActive, true))) {
                throw new DictDataException(MessageConstant.DICT_DATA_NOT_FOUND);
            }
            updateWrapper.set(PlagiarismChecks::getStatusCode,plagiarismChecksUpdateDTO.getStatusCode());
        }
        if (StringUtils.isNotBlank(plagiarismChecksUpdateDTO.getErrorMessage())) {
            updateWrapper.set(PlagiarismChecks::getErrorMessage,plagiarismChecksUpdateDTO.getErrorMessage());
        }
        if (plagiarismChecksUpdateDTO.getStartTime() != null) {
            updateWrapper.set(PlagiarismChecks::getStartTime,plagiarismChecksUpdateDTO.getStartTime());
        }
        if (plagiarismChecksUpdateDTO.getCompletedAt() != null) {
            updateWrapper.set(PlagiarismChecks::getCompletedAt,plagiarismChecksUpdateDTO.getCompletedAt());
        }
        plagiarismChecksMapper.update(updateWrapper);
    }

    /**
     *内部类：用于映射JSON数据
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class ComparisonJsonRoot {
        @JsonProperty("firstSubmissionId")
        private String firstSubmissionId;
        @JsonProperty("secondSubmissionId")
        private String secondSubmissionId;
        @JsonProperty("similarities")
        private Similarities similarities;
    }

    /**
     *内部类：用于映射相似度数据
     */
    @Data
    private static class Similarities {
        @JsonProperty("AVG")
        private double avg;
        @JsonProperty("MAX")
        private double max;
        @JsonProperty("MAXIMUM_LENGTH")
        private double maximumLength;
        @JsonProperty("LONGEST_MATCH")
        private double longestMatch;
    }

    /**
     *内部类：用于存储解析后的比较数据
     */
    @Data
    private static class ComparisonData {
        private String firstSubmission;
        private String secondSubmission;
        private Similarities similarities;
    }

    /**
     *内部类：用于存储运行信息
     */
    @Data
    @AllArgsConstructor
    private static class RunInfo {
        private  int executionTime;
        private  int totalComparisons;
    }

    /**
     *内部类：用于存储处理结果
     */
    @Data
    @AllArgsConstructor
    private static class ProcessResult {
        private int totalComparisons;
        private int executionTime;
    }
}