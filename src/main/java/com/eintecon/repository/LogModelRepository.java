package com.eintecon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.eintecon.model.LogModel;

public interface LogModelRepository extends MongoRepository<LogModel, String>{
	public LogModel findByClientMACID(String clientMACID);
}
