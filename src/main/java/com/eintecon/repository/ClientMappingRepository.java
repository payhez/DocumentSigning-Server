package com.eintecon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.eintecon.model.ClientMappingModel;

public interface ClientMappingRepository extends MongoRepository<ClientMappingModel, String> {

	//public ClientMappingModel findby findByErpID(String erpID);
	public ClientMappingModel  findClientMappingModelByErpID(String erpID);
}
