package com.aurea.zbw.api.repositories;

import com.aurea.zbw.api.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findOneByUsername(@Param("username") String username);

}