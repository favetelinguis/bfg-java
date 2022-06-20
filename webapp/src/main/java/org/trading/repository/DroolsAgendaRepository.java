package org.trading.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DroolsAgendaRepository extends MongoRepository<DroolsAgendaEventEntity, String> {

}
