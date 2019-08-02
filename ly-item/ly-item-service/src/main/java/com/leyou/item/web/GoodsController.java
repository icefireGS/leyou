package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询SPU
     *
     * @param page
     * @param rows
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key) {
        // 分页查询spu信息
        PageResult<SpuBo> result = this.goodsService.querySpuByPageAndSort(page, rows, saleable, key);
        if (result == null /*|| result.getItems().size() == 0*/) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增商品
     *
     * @param spu
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spu) {
        try {
            this.goodsService.save(spu);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteGoods(@RequestParam("id") Long spu_id) {
        goodsService.deleteGoods(spu_id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> getSpuDetailById(@PathVariable("id") Long spu_id) {
        return ResponseEntity.ok(goodsService.getSpuDetailById(spu_id));
    }

    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> getSkusBySpuId(@RequestParam("id") Long spu_id) {
        return ResponseEntity.ok(goodsService.getSkusBySpuId(spu_id));
    }

    @PutMapping
    public ResponseEntity<Void> editGoods(@RequestBody SpuBo spu) {
        goodsService.edit(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/spu/soldout")
    public ResponseEntity<Void> soldoutGoods(@RequestParam("id") Long spu_id) {
        goodsService.soldoutGoods(spu_id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/spu/shelves")
    public ResponseEntity<Void> shelvesGoods(@RequestParam("id") Long spu_id) {
        goodsService.shelvesGoods(spu_id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}