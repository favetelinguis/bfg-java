package org.trading.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommandRepository extends MongoRepository<CommandEntity, String> {

}
