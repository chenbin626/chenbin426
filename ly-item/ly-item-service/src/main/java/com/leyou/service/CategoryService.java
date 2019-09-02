package com.leyou.service;

import com.leyou.item.pojo.Category;
import com.leyou.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryByParentId(Long id) {
        Category category=new Category();
        category.setParentId(id);

        return categoryMapper.select(category);
    }

    public List<String> queryNamesByIds(List<Long> asList) {

        List<String>  namess=  new ArrayList<>();
        
        List<Category> categories= categoryMapper.selectByIdList(asList);

        categories.forEach(t->{
            namess.add(t.getName());

        });
        return namess;
    }
}
