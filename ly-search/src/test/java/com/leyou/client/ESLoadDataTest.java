package com.leyou.client;

import com.leyou.LySearchService;
import com.leyou.common.vo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.pojo.Goods;
import com.leyou.repository.GoodsRepository;
import com.leyou.service.IndexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchService.class)
public class ESLoadDataTest {

    @Autowired
    private IndexService indexService;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;
    @Test
    public void loadData(){
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            // 查询spu
            PageResult<SpuBo> result = this.goodsClient.querySpuByPage(page, rows, true, null);
            List<SpuBo> spus = result.getItems();

            // spu转为goods
            List<Goods> goods = spus.stream().map(spu -> this.indexService.buildGoods(spu))
                    .collect(Collectors.toList());

            // 把goods放入索引库
            this.goodsRepository.saveAll(goods);

            size = spus.size();
            page++;
        }while (size == 100);
    }
}