package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDTO;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lin
 * @date 2024/2/8 16:58
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDTO> queryTreeNodes(String id) {
        List<CourseCategoryTreeDTO> courseCategoryTreeDTOList = courseCategoryMapper.selectTreeNodes(id);

        // 找到每个节点的子节点，并完成封装
        // 转为map，key为id，value为对象，排除根节点
        Map<String, CourseCategoryTreeDTO> map = courseCategoryTreeDTOList.stream()
                .filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toMap(CourseCategory::getId, value -> value, (key1, key2) -> key2));

        List<CourseCategoryTreeDTO> courseCategoryList = new ArrayList<>();

        courseCategoryTreeDTOList.stream()
                .filter(item -> !id.equals(item.getId()))
                .forEach(item -> {
                    // 插入一级节点
                    if (item.getParentid().equals(id)) {
                        courseCategoryList.add(item);
                    }

                    // 找到父节点
                    CourseCategoryTreeDTO courseCategoryParent = map.get(item.getParentid());
                    if (courseCategoryParent != null) {
                        // 插入二级以上节点
                        if (courseCategoryParent.getChildrenTreeNodes() == null) {
                            courseCategoryParent.setChildrenTreeNodes(new ArrayList<>());
                        }
                        courseCategoryParent.getChildrenTreeNodes().add(item);
                    }
                });

        return courseCategoryList;
    }
}
