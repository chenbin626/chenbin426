package com.leyou.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.mapper.BrandMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;
    public PageResult<Brand> pageQuery(Integer page, Integer rows, String sortBy, Boolean desc, String key) {

        PageHelper.startPage(page,rows);

        Example example = new Example(Brand.class);

        if(StringUtils.isNoneBlank(key)){
            Example.Criteria criteria = example.createCriteria();
            criteria.andLike("name","%"+key+"%");
        }
        if(StringUtils.isNoneBlank(sortBy)){
            example.setOrderByClause(sortBy+(desc?" desc":" asc"));
        }
        Page<Brand> brandPage=(Page<Brand>)brandMapper.selectByExample(example);

        return  new PageResult<Brand>(brandPage.getTotal(),new Long(brandPage.getPages()),brandPage);

    }

    @Transactional
    public void addBrand(Brand brand, List<Long> cids) {

        brandMapper.insertSelective(brand);
        cids.forEach(cid->{
            brandMapper.insertBrandCategory(brand.getId(),cid);

        });
    }

    public Brand queryBrandById(Long brandId) {
        return  brandMapper.selectByPrimaryKey(brandId);
    }

    public List<Brand> queryBrandByCategory(Long cid) {
        return brandMapper.queryBrandByCategory(cid);
    }
}
