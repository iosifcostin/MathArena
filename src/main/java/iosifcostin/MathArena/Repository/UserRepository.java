package iosifcostin.MathArena.Repository;

import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    User findByFirstName(String firstName);

    User findByEmailAndIdNot(String email, Long id);

    User findByFirstNameAndIdNot(String username, Long id);

    Page<User> findByFirstNameContainingOrderByIdAsc(String username, Pageable pageable);

    Page<User> findByEmailContainingOrderByIdAsc(String email, Pageable pageable);

    Page<User> findByLastNameContainingOrderByIdAsc(String username, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.mathProblems = :#{#user.mathProblems}, " +
            "u.profilePicturePath  = :#{#user.profilePicturePath}," +
            "u.email = :#{#user.email}," +
            "u.enabled = :#{#user.enabled}," +
            "u.firstName = :#{#user.firstName}, " +
            "u.lastName = :#{#user.lastName}," +
            "u.googleAuthId = :#{#user.googleAuthId}," +
            "u.password = :#{#user.password}," +
            "u.roles = :#{#user.roles} WHERE u.id = :#{#user.id}")
    void setProblems(@Param("user") User user);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.profilePicturePath = :#{#path} WHERE u.id = :#{#id}")
    void setProfilePicture(@Param("id") Long id, @Param("path") String path);


    //region Find eagerly
    //==========================================================================
    @Query("SELECT u FROM User u JOIN FETCH u.roles")
    List<User> findAllEagerly();

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = (:email)")
    User findByEmailEagerly(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.mathProblems WHERE u.email = (:email)")
    User findByEmailJoinFetchProblems(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.email = :#{#email}")
    User findByEmail(@Param("email") String email);

    User findByGoogleAuthId(String authId);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = (:id)")
    User findByIdEagerly(@Param("id") Long id);
    //==========================================================================
    //endregion


}
