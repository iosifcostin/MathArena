package iosifcostin.MathArena.config;

import iosifcostin.MathArena.mathMl.MathMlToPng;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
//                "cache.allProblemsPageable",
//                "cache.allProblems",
                "cache.allUsers",
                "cache.allProblemClasses",
//                "cache.allProblemsPageable",
                "cache.allCategories",
//                "cache.MathProblemById",
                "cache.allUsersPageable",
                "cache.byEmailContaining",
                "cache.byFirstNameContaining", "cache.byLastNameContaining",
                "cache.allRoles", "cache.roleByName",
                "cache.roleById","cache.userById"

        );


    }

}
