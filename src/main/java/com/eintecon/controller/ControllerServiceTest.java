package com.eintecon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eintecon.model.ClientMappingModel;
import com.eintecon.model.DocumentModel;
import com.eintecon.model.TestModel;
import com.eintecon.service.DocumentSignService;

@RestController
@RequestMapping("/ataley")
public class ControllerServiceTest {
	@Autowired 
	DocumentSignService dss;
	
	static DocumentModel dm = new DocumentModel();
	@GetMapping(value="/getTestModel") 
	public DocumentModel getTestModel() {
		System.out.println("HERE");
		TestModel testModel1 = new TestModel();
		testModel1.setClientID("123");
		testModel1.setERPID("aa123");
		testModel1.setKey("aaaaaabbbbbbcccccc"); // UUID
		
		return dm;
	}
	@PostMapping(value="/sendDocument")
	public DocumentModel sendTestModel(@RequestBody DocumentModel dModel) {
		String clientName =dModel.getClient();
		ClientMappingModel client= dss.getClientMapping(clientName);
		dm=dModel;
		dm.setClient(client.getClientID());
		return dModel;
		
	}
	@PostMapping(value="/saveMapping")
	public ClientMappingModel saveClientMapping(@RequestBody ClientMappingModel clm) {
				
		return dss.saveClientMapping(clm);
		
	}
	/*@GetMapping(value="/getMapping/{erpID}")
	public ClientMappingModel getClientMapping(@PathVariable String erpID) {
				
		return dss.getClientMapping(erpID);
		
	}
	@GetMapping(value="/sendDocument/{documentID}")
	public DocumentModel sendDocumentModel(@PathVariable String documentID) {
		DocumentModel dm = new 	DocumentModel();
		dm= dss.documentGetMapping(documentID);
		
		
		return dm;
	}*/
	@GetMapping(value="/getClientMapping/{erpID}")//clientgetmapping
	public ClientMappingModel getClientMapping(@PathVariable String erpID) {
				
		return dss.getClientMapping(erpID);
		
	}
	@GetMapping(value="/sendDocument/{transID}")
	public DocumentModel sendDocumentModel(@PathVariable String transID) {//transactiid olacak 
		DocumentModel dm = new 	DocumentModel();
		dm= dss.documentGetMapping(transID);
		
		return dm;
	}
}
