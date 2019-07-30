package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;


    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(Brand.class);
        if(!StringUtils.isNullOrEmpty(key)){
            //过滤条件
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        //排序
        if(!StringUtils.isNullOrEmpty(sortBy)){
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //解析分页结果
        PageInfo<Brand> info = new PageInfo<>(list);
        return new PageResult<>(info.getTotal(), list);
    }

    //事务注解
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌
        brand.setId(null);
        int count = brandMapper.insert(brand);
        if (count != 1) {
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        //新增中间表
        for (Long cid : cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }
    }

    //事务注解
    @Transactional
    public void editBrand(Brand brand, List<Long> cids) {
        //修改品牌
        int count = brandMapper.updateByPrimaryKey(brand);
        if (count != 1) {
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }
        //修改中间表
        //删除原中间关系
        count = brandMapper.DeleteCategoryBrand(brand.getId());
        if (count < 1) {
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }
        //重新插入中间关系
        for (Long cid : cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
            }
        }
    }

    //事务注解
    @Transactional
    public void deleteBrand(Long id) {
        //删除品牌
        Brand brand = new Brand();
        brand.setId(id);
        int count = brandMapper.deleteByPrimaryKey(brand);
        if (count != 1) {
            throw new LyException(ExceptionEnum.BRAND_DELETE_ERROR);
        }
        //删除品牌类别关联中间表
        count = brandMapper.DeleteCategoryBrand(id);
        if (count < 1) {
            throw new LyException(ExceptionEnum.BRAND_DELETE_ERROR);
        }
    }
}
