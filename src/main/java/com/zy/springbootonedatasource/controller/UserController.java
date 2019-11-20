package com.zy.springbootonedatasource.controller;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * <p>Title: UserController</p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author ZY
 * <p> Just go on !!!</p>
 * @date 2019年11月17日  9:40
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/getUser")
    public List getUser() {
        String sql = "select * from user";
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List resultList = query.getResultList();
        return resultList;
    }

}
