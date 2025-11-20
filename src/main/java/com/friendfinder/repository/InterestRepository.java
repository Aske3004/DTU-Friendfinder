package com.friendfinder.repository;
import com.friendfinder.model.User;
import com.friendfinder.model.Interest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
@RepositoryRestResource(collectionResourceRel = "interest", path = "interest")
public interface InterestRepository extends PagingAndSortingRepository<Interest, Long>, CrudRepository<Interest, Long> {
    Interest findByName(@Param("name") String name);
}

