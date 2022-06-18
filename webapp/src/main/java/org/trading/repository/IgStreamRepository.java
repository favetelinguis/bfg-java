package org.trading.repository;

import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IgStreamRepository extends MongoRepository<IgStreamEntity, String> {

}
