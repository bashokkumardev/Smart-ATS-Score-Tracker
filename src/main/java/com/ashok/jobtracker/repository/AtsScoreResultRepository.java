package com.ashok.jobtracker.repository;

import com.ashok.jobtracker.entity.AtsScoreResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AtsScoreResultRepository extends MongoRepository<AtsScoreResult, String> {

    List<AtsScoreResult> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<AtsScoreResult> findByIdAndUserId(String id, String userId);
}
