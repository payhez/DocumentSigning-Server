package com.eintecon.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.eintecon.model.DocumentModel;

public interface DocumentMappingRepository extends MongoRepository<DocumentModel, String> {
	public DocumentModel findByDocumentId(String docID);

	public List<DocumentModel> findByClientAndSigned(String client, boolean signed);
	
	public DocumentModel findDocumentModelByTrid(String trid);
}
