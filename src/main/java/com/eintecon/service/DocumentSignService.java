package com.eintecon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eintecon.model.ClientMappingModel;
import com.eintecon.model.DocumentModel;
import com.eintecon.model.LogModel;
import com.eintecon.repository.ClientMappingRepository;
import com.eintecon.repository.DocumentMappingRepository;
import com.eintecon.repository.LogModelRepository;

@Service
public class DocumentSignService {
	@Autowired
	ClientMappingRepository clientRepo;
	@Autowired
	DocumentMappingRepository docRepo;
	@Autowired
	LogModelRepository logRepo;
	public ClientMappingModel saveClientMapping(ClientMappingModel client) {
		return clientRepo.save(client);
	}
	public ClientMappingModel getClientMapping(String erpid) {
		
		return clientRepo.findClientMappingModelByErpID(erpid);
		//return clientRepo.findClientMappingModelByMessage(erpid);
	}
	
	public DocumentModel documentSaveMapping(DocumentModel doc) {
		return docRepo.save(doc);
	}
	public DocumentModel documentGetMapping(String docID) {
		return docRepo.findByDocumentId(docID);
	}
	public List<DocumentModel> documentGetMappingByClientMacId(String client, boolean signed) {
		
		return docRepo.findByClientAndSigned(client,signed);
	}
	public LogModel logSaveMapping(LogModel doc) {
		return logRepo.save(doc);
	}
	public DocumentModel documentGetMappingByTrid(String trid) {
		return docRepo.findDocumentModelByTrid(trid);
	}
}
