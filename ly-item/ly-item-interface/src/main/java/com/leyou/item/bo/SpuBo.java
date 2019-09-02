package com.leyou.item.bo;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import lombok.Data;

import java.util.List;

@Data
public class SpuBo extends Spu {



    private String cname;//商品分类名称


    private String bname;//品牌名称

    private List<Sku> skus;

    private SpuDetail spuDetail;

}
