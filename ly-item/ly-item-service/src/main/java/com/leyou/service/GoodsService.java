package com.leyou.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.*;
import com.leyou.mapper.SkuMapper;
import com.leyou.mapper.SpuDetailMapper;
import com.leyou.mapper.SpuMapper;
import com.leyou.mapper.StockMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private  CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    public PageResult<SpuBo> querySpuByPage(Integer page, Integer rows,  Boolean saleable, String key) {

        PageHelper.startPage(page,rows);

        Example example = new Example(Spu.class);

        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }

        if(null!=saleable){
            criteria.andEqualTo("saleable",saleable);

        }


        Page<Spu> spuPage=(Page<Spu>)spuMapper.selectByExample(example);
        List<Spu> spus=spuPage.getResult();

        List<SpuBo> spubBos=new ArrayList<SpuBo>();

        spus.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu,spuBo);

            List<String>  names= categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

            spuBo.setCname( StringUtils.join(names,"/"));


            Brand brand=brandService.queryBrandById(spu.getBrandId());
            spuBo.setBname(brand.getName());
            spubBos.add(spuBo);

        });

        return new  PageResult<>(spuPage.getTotal(),new Long(spuPage.getPages()),spubBos);


    }

    @Transactional
    public void saveGoods(SpuBo spuBo) {
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(new Date());

        spuMapper.insertSelective(spuBo);
        SpuDetail spuDetail =spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());


        spuDetailMapper.insertSelective(spuDetail);

        List<Sku> skus = spuBo.getSkus();

        saveSkus(spuBo,skus);

    }

    private void saveSkus(SpuBo spuBo, List<Sku> skus) {
        for (Sku sku:skus){
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            skuMapper.insertSelective(sku);

            Stock stock = new Stock();

            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            stockMapper.insertSelective(stock);
        }


    }

    public SpuDetail querySpuDetailBySpuId(Long id) {
        return spuDetailMapper.selectByPrimaryKey(id);

    }
    public List<Sku> querySkuBySpuId(Long spuId){
        Sku sku= new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus =skuMapper.select(sku);

        for (Sku sk:skus){
            Stock stock = stockMapper.selectByPrimaryKey(sk.getId());
            sk.setStock(stock.getStock());
        }
        return  skus;

    }

    @Transactional
    public void updateGoods(SpuBo spuBo){
        spuBo.setLastUpdateTime(new Date());
        spuMapper.updateByPrimaryKey(spuBo);
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        Sku sku =new Sku();
        sku.setSpuId(spuBo.getId());
        List<Sku> skuList= skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)){
            List<Long> ids=skuList.stream().map(Sku::getId).collect(Collectors.toList());

            //   List<Long> list = new ArrayList<>();
//            for(Sku s:skuList){
//                list.add(s.getId());
//            }

            stockMapper.deleteByIdList(ids);
            skuMapper.deleteByIdList(ids);
        }

        saveSkus(spuBo,spuBo.getSkus());

    }


}
