package org.trading.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DroolsRuleRuntimeRepository extends MongoRepository<DroolsRuleRuntimeEventEntity, String> {

}
