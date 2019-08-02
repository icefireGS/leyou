package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Specification;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("{id}")
    public ResponseEntity<String> querySpecificationByCategoryId(@PathVariable("id") Long id){
        Specification spec = this.specificationService.queryById(id);
        if (spec == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spec.getSpecifications());
    }

    @PutMapping
    public ResponseEntity<Void> saveSpecification(Specification specification) {
        specificationService.saveSpecification(specification);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid) {
        List<SpecGroup> specGroupList = specificationService.querySpecGroupsByCid(cid);

        if (null == specGroupList || 0 == specGroupList.size()) {
            //204
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(specGroupList);
    }

    //params?gid=1
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParamByGid(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching,
            @RequestParam(value = "generic", required = false) Boolean generic) {

        List<SpecParam> specParamList = specificationService.querySpecParamByGid(gid,cid,searching,generic);

        if (null == specParamList || 0 == specParamList.size()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(specParamList);
    }

    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> list = this.specificationService.querySpecsByCid(cid);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

}