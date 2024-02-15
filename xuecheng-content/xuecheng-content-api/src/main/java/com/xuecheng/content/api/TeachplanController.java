package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeachplanDTO;
import com.xuecheng.content.model.dto.TeachplanDTO;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程计划相关接口
 *
 * @author Lin
 * @date 2024/2/15 13:18
 */
@Api(value = "课程计划相关接口", tags = "课程计划相关接口")
@RestController
@RequestMapping("/teachplan")
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachplanDTO> getTreeNodes(@PathVariable Long courseId) {
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping
    public void saveTeachplan(@RequestBody SaveTeachplanDTO teachplan) {
        teachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("{id}")
    public void deleteTeachplan(@PathVariable Long id) {
        teachplanService.deleteTeachplan(id);
    }

    @ApiOperation("向下移动课程计划")
    @PostMapping("/movedown/{id}")
    public void moveDownTeachplan(@PathVariable Long id) {
        teachplanService.sortTeachplan(false, id);
    }

    @ApiOperation("向上移动课程计划")
    @PostMapping("/moveup/{id}")
    public void moveUpTeachplan(@PathVariable Long id) {
        teachplanService.sortTeachplan(true, id);
    }
}
