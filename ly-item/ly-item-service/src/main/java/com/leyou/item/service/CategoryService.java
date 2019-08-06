package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryByPid(Long pid) {
        Category t = new Category();
        t.setParentId(pid);
        List<Category> list = categoryMapper.select(t);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public List<Category> queryByBrandId(Long bid) {
        return this.categoryMapper.queryByBrandId(bid);
    }

    public List<String> queryNameByIds(List<Long> ids) {
        return this.categoryMapper.selectByIdList(ids).stream().map(Category::getName).collect(Collectors.toList());
    }

    public void addCategory(Category category) {
        category.setId(null);
        int count = categoryMapper.insertSelective(category);
        if (count < 1) {
            throw new LyException(ExceptionEnum.CATEGORY_ADD_EOORO);
        }
    }

    public void deleteCategory(Long cid) {
        Category category=new Category();
        category.setId(cid);
        int count = categoryMapper.deleteByPrimaryKey(category);
        if (count < 1) {
            throw new LyException(ExceptionEnum.CATEGORY_DELETE_EORROR);
        }
    }
}
