package com.example.server.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author army
 * @date 2023/3/14 18:21
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<Account> {

    /**
     * 查询所有用户
     *
     * @return
     */
    List<Account> selectList();

    List<Account> selectByUsername(@Param("username") String username);

    List<Account> selectByEmail(@Param("email") String email);

    int resetPasswordByEmail(@Param("password")String password , @Param("email")String email);

    /**
     * 插入一条数据
     *
     * @param account
     * @return
     */
    int insert(@Param("user") Account account);

    int deleteById(@Param("id") Integer id);

}
