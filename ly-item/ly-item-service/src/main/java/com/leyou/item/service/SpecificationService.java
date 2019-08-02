package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.mapper.SpecificationMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public Specification queryById(Long id) {
        return this.specificationMapper.selectByPrimaryKey(id);
    }

    public void saveSpecification(Specification specification) {
        int count = specificationMapper.updateByPrimaryKey(specification);
        if (count < 1) {
            throw new LyException(ExceptionEnum.SPECIFICATION_SAVE_ERROR);
        }
    }

    public List<SpecGroup> querySpecGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return specGroupMapper.select(specGroup);
    }

    public List<SpecParam> querySpecParamByGid(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        specParam.setGeneric(generic);

        return specParamMapper.select(specParam);
    }

    public List<SpecGroup> querySpecsByCid(Long cid) {
        List<SpecGroup> specGroups = this.querySpecGroupsByCid(cid);

        specGroups.forEach(specGroup -> {
            specGroup.setParams(querySpecParamByGid(specGroup.getId(),null,null,null));
        });
        return specGroups;
    }
}