package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    public PageResult<SpuBo> querySpuByPageAndSort(Integer page, Integer rows, Boolean saleable, String key) {
        // 1、查询SPU
        // 分页,最多允许查100条
        PageHelper.startPage(page, Math.min(rows, 100));
        // 创建查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 是否过滤上下架
        if (saleable != null) {
            criteria.orEqualTo("saleable", saleable);
        }
        // 是否模糊查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        Page<Spu> pageInfo = (Page<Spu>) this.spuMapper.selectByExample(example);

        List<SpuBo> list = pageInfo.getResult().stream().map(spu -> {
            // 2、把spu变为 spuBo
            SpuBo spuBo = new SpuBo();
            // 属性拷贝
            BeanUtils.copyProperties(spu, spuBo);

            // 3、查询spu的商品分类名称,要查三级分类
            List<String> names = this.categoryService.queryNameByIds(
                    Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            // 将分类名称拼接后存入
            spuBo.setCname(StringUtils.join(names, "/"));

            // 4、查询spu的品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), list);
    }

    @Transactional
    public void save(SpuBo spu) {
        // 保存spu
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        this.spuMapper.insert(spu);
        // 保存spu详情
        spu.getSpuDetail().setSpuId(spu.getId());
        this.spuDetailMapper.insert(spu.getSpuDetail());

        saveSkuAndStock(spu.getSkus(),spu.getId());
    }

    private void saveSkuAndStock(List<Sku> skus, Long spuId) {
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            // 保存sku
            sku.setSpuId(spuId);
            // 初始化时间
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insert(sku);

            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock().getStock());
            this.stockMapper.insert(stock);
        }
    }

    //事务提交
    @Transactional
    public void deleteGoods(Long id) {
        Spu delete_spu = new Spu();
        SpuDetail detail = new SpuDetail();
        Example example = new Example(Sku.class);
        Stock stock = new Stock();
        delete_spu.setId(id);
        detail.setSpuId(id);
        example.createCriteria().andEqualTo("spuId", id);
        //删除spu
        int count = spuMapper.deleteByPrimaryKey(delete_spu);
        if (count < 1) {
            throw new LyException(ExceptionEnum.GOOD_DELETE_ERROR);
        }

        //删除spu_detail
        count = spuDetailMapper.deleteByPrimaryKey(detail);
        if (count < 1) {
            throw new LyException(ExceptionEnum.GOOD_DELETE_ERROR);
        }

        //删除库存
        //查询sku
        List<Sku> skus = skuMapper.selectByExample(example);
        for (Sku sku : skus) {
            //逐个删除
            stock.setSkuId(sku.getId());
            count = stockMapper.deleteByPrimaryKey(stock);
            if (count < 1) {
                throw new LyException(ExceptionEnum.GOOD_DELETE_ERROR);
            }
        }

        //删除sku
        count = skuMapper.deleteByExample(example);
        if (count < 1) {
            throw new LyException(ExceptionEnum.GOOD_DELETE_ERROR);
        }


    }

    public SpuDetail getSpuDetailById(Long spu_id) {
        SpuDetail querySD = new SpuDetail();
        querySD.setSpuId(spu_id);
        SpuDetail result = spuDetailMapper.selectByPrimaryKey(querySD);
        if (result == null) {
            throw new LyException(ExceptionEnum.SPUDETAIL_NOT_FOUND);
        }
        return result;
    }

    public List<Sku> getSkusBySpuId(Long spu_id) {
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId", spu_id);
        List<Sku> skus = skuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        for (Sku sku : skus) {
            Stock Sexample = new Stock();
            Sexample.setSkuId(sku.getId());
            Stock stock = stockMapper.selectByPrimaryKey(Sexample);
            if (stock == null) {
                throw new LyException(ExceptionEnum.STOCK_NOT_FOUND);
            }
            sku.setStock(stock);
        }
        return skus;
    }

    @Transactional
    public void edit(SpuBo spu){
        // 查询以前sku
        List<Sku> skus = this.getSkusBySpuId(spu.getId());
        // 如果以前存在，则删除
        if(!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spu.getId());
            this.skuMapper.delete(record);

        }
        // 新增sku和库存
        saveSkuAndStock(spu.getSkus(), spu.getId());

        // 更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spu);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
    }

    public void soldoutGoods(Long spu_id) {
        Spu spu = new Spu();
        spu.setId(spu_id);
        spu.setSaleable(false);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count < 1) {
            throw new LyException(ExceptionEnum.SALEABLE_CHANGE_ERROR);
        }
    }

    public void shelvesGoods(Long spu_id) {
        Spu spu = new Spu();
        spu.setId(spu_id);
        spu.setSaleable(true);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count < 1) {
            throw new LyException(ExceptionEnum.SALEABLE_CHANGE_ERROR);
        }
    }



}