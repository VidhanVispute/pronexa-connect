package com.pronexa.connect.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pronexa.connect.entities.Contact;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {

        @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    Page<Contact> findByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Contact> searchByName(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Contact> searchByEmail(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Contact> searchByUserIdAndKeyword(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND c.favorite = true")
    Page<Contact> findFavoritesByUserId(@Param("userId") String userId, Pageable pageable);
}
