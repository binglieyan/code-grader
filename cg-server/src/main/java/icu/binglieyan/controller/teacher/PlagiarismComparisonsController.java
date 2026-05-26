package icu.binglieyan.controller.teacher;

import icu.binglieyan.result.Result;
import icu.binglieyan.service.PlagiarismComparisonsService;
import icu.binglieyan.vo.PlagiarismComparisonsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 查重结果控制器类
 * @author binglieyan
 */
@RestController("teacherPlagiarismComparisonsController")
@RequestMapping("/teacher/PlagiarismComparisons")
@Tag(name = "查重结果相关接口")
@Log4j2
@RequiredArgsConstructor
public class PlagiarismComparisonsController {

    private final PlagiarismComparisonsService plagiarismComparisonsService;

    /**
     * 查询查重结果
     * @param plagiarismCheckId 查重任务ID
     * @return 查重结果
     */
    @GetMapping("/queryPlagiarismComparisons/{plagiarismCheckId}")
    @Operation(summary = "查询查重结果")
    public Result<List<PlagiarismComparisonsVO> > queryPlagiarismComparisons(@PathVariable Long plagiarismCheckId){
        log.info("查询查重结果");
        return Result.success(plagiarismComparisonsService.queryPlagiarismComparisons(plagiarismCheckId));
    }
}
