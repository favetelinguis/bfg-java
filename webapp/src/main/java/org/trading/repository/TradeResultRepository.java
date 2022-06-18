package org.trading.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TradeResultRepository extends MongoRepository<TradeResultEntity, String> {

}
