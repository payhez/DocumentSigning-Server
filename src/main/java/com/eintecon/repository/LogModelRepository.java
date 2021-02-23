package com.eintecon.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.eintecon.model.LogModel;

public interface LogModelRepository extends MongoRepository<LogModel, String>{
	public List<LogModel> findByClientMACID(String clientMACID);
}
