package com.pronexa.connect.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pronexa.connect.entities.User;


@Repository
public interface UserRepo extends JpaRepository<User, String>{

    // “The method findByEmail(String email) is not a standard Java method—how is its logic implemented without us writing it explicitly?”
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailToken(String emailToken);


    // Basic CRUD Operations:
    // Pagination and Sorting:
    // Defining Custom Queries Using Method Names:
    // Using @Query for Custom JPQL or Native Queries:
    // Transactional and Locking Operations:

    // =============================================================

    // Excellent question! You're asking:

// > *“The method `findByEmail(String email)` is not a standard Java method—how is its logic implemented without us writing it explicitly?”*

// This is a core feature of **Spring Data JPA**, so let’s break it down carefully.

// ---

// ### ✅ **How it works:**

// #### 1️⃣ **Spring Data JPA method name parsing**

// Spring Data JPA looks at the name of the method you define in the repository interface. The name `findByEmail` tells Spring:

// * **find** → It's a query method.
// * **ByEmail** → The condition is based on the `email` field of the `User` entity.

// So at runtime, Spring interprets this and automatically constructs the appropriate query.

// ---

// #### 2️⃣ **Behind the scenes – It generates the implementation**

// When your application starts:

// 1. Spring scans all interfaces that extend `JpaRepository`.
// 2. It detects methods like `findByEmail`.
// 3. It parses the method name and figures out:

//    * The entity to query (`User`).
//    * The field to filter on (`email`).
//    * The return type (`Optional<User>`).
// 4. It dynamically creates a proxy class that implements the interface and executes the required query using **JPA Criteria API** or **JPQL**.

// For `findByEmail`, Spring would internally create a query like:

// ```sql
// SELECT u FROM User u WHERE u.email = :email
// ```

// It then binds the `email` parameter and executes the query when the method is called.

// ---

// #### 3️⃣ **Where is the actual code?**

// You don’t see it in your source files because:

// * Spring Data uses **proxies** and **runtime code generation**.
// * The logic is handled by underlying JPA providers like **Hibernate**.
// * The method signature is sufficient; the framework takes care of everything else.

// ---

// ### ✅ Example of how this relates to the `User` entity:

// ```java
// @Entity
// public class User {
//     @Id
//     private String userId;

//     @Column(unique = true, nullable = false)
//     private String email;

//     // other fields...
// }
// ```

// Because `email` is mapped in the entity, Spring can match it to the database column and construct the query accordingly.

// ---

// ### ✅ Benefits of this approach:

// * **No boilerplate code** – You don’t have to write SQL or JPQL.
// * **Type safety** – The method signature ensures correct data handling.
// * **Readable and declarative** – Method names explain the query purpose.
// * **Extensible** – You can create other queries like `findByNameAndEmail`, `countByEmail`, etc.

// ---

// ### ✅ How it works at runtime — simplified steps:

// 1. Application starts → Spring scans repository interfaces.
// 2. Finds `findByEmail` → interprets it as a query.
// 3. Uses Hibernate (or another JPA provider) to construct the query dynamically.
// 4. Executes the query when you call `userRepo.findByEmail(email)`.

// ---

// Let me know if you want:

// * A deeper explanation of proxies or how Hibernate/JPA providers work internally.
// * Examples of other query methods (`findByNameAndStatus`, `countByRole`, etc.).
// * Help testing this or handling exceptions with `Optional`.





}
