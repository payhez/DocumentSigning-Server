package com.eintecon.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eintecon.model.ClientMappingModel;
import com.eintecon.model.DocumentModel;
import com.eintecon.model.LogModel;
import com.eintecon.service.DocumentSignService;

@RestController
@RequestMapping("/documentController")
public class DocumentController {
	@Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	@Autowired
	DocumentSignService dss;
	/*@GetMapping("/documentGetter/{DocumentID}")
	public DocumentModel  getDocument(@PathVariable String DocumentID){
		DocumentModel dmc = new DocumentModel();
		dmc= dss.documentGetMapping(DocumentID);
		return dmc;
	}
	
	@GetMapping(value="/saveDocument")
	public DocumentModel saveDocumentModel(@RequestBody DocumentModel dm) {
		dm=dss.documentSaveMapping(dm);
		dm.setDocumentUrl("globalUrl/documentGetter/"+dm.getDocumentId());
		return dm;
	}*/
	
	//-----------------------------------------------------------------------------------------------------------------
	@GetMapping(value="/saveDocumentFromLNTrial")
	//public DocumentModel saveDocumentModelFromLnTrial(@RequestBody DocumentModel dm) {
	public DocumentModel saveDocumentModelFromLn() {
		DocumentModel dm =new DocumentModel();
		LogModel logmodel=new LogModel();
		
		if (dm !=null) {
			byte[] bytes = null;
			try {
					bytes = dss.convertDocToByteArray("/disk1/apps/apache-tomcat-8.5.61/documentSignService/files/ornek.pdf");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			ClientMappingModel clntmapModel=new ClientMappingModel();
			clntmapModel=dss.getClientMapping("aa11");
			if(clntmapModel == null) {
				clntmapModel = dss.getClientMapping("aa11");
			}
			String uuid=UUID.randomUUID().toString();
			dm.setTrid(uuid);
			dm.setName(generateString());
			dm.setStatus("Lnden Dosya geldi.");
		    dm.setClient(clntmapModel.getClientID());
		    dm.setSigned(false);
		    dm.setFileExtension(".pdf");
		    
		    dm.setDocumentUrl(dss.UNSIGNED_URL+LocalDate.now().toString()+"/"+dm.getName()+ dm.getFileExtension());
		    logmodel.setIssue("LN'den Dosya Alımı");
		    logmodel.setIssueResult("BAŞARILI");
	    	logmodel.setDetails("LN'den gelen dosya("+dm.getName()+") sisteme kaydedildi.");
	    	try {
		    	dss.convertBytesToFile(dm.isSigned(), bytes, dm.getName(), dm.getFileExtension());
	    	}catch(Exception e1) {
	    		dm.setStatus("Dosya servera kaydedilemedi");
	    		logmodel.setIssueResult("BAŞARISIZ");
				logmodel.setDetails(e1.toString());
	    	}
		    //String stream = Base64.getEncoder().encodeToString(bytes);
			//byte[] newBytes = Base64.getDecoder().decode(stream);
	    	dm.setCrt_date(formatter.format(new Date()));
	    	dm.setBinaryData(null);
	    	
			if (clntmapModel.isConnected()) {
				simpMessagingTemplate.convertAndSend("/topic/greetings/" + clntmapModel.getClientID(), dm);
				dm.setStatus("20-Client aktif ve dosyayı aldı.");
			}else {
				logmodel.setDetails("Client Aktif değil.");
				dm.setStatus("Client Aktif değil.");
			}
			
			dm=dss.documentSaveMapping(dm);
			logmodel.setClientMACID(clntmapModel.getClientID());
			logmodel.setIssueTime(new Date());
			logmodel.setTrid(dm.getTrid());
			logmodel.setHost("");
			dss.logSaveMapping(logmodel);
		}		
		return dm;
	}
	@GetMapping(value = "/getAllLogsForUser/{userMacId}")
	public List<LogModel> postLogModelsToUser(@PathVariable String userMacId){
		List<LogModel> logList = new ArrayList<LogModel>();
		if(userMacId != null) {
			logList = dss.logGetMappingByClientMACID(userMacId);
		}
		return logList;
	}
	/*@GetMapping(value="/saveDocumentFromLN")
	public DocumentModel saveDocumentModelFromLn(@RequestBody DocumentModel dm) {
		LogModel logmodel=new LogModel();
		
		if (dm !=null) {
			byte[] bytes = dm.getBinaryData();
			
			ClientMappingModel clntmapModel=new ClientMappingModel();
			clntmapModel=dss.getClientMapping(dm.getErpID());
			logmodel.setIssue("LN'den Dosya Alımı");
		    logmodel.setIssueResult("BAŞARILI");
			if(clntmapModel == null) {
				logmodel.setIssueResult("BAŞARISIZ");
				logmodel.setDetails("Client veriatabininda bulunamadi.");
				clntmapModel = dss.getClientMapping("aa11");
				//return null;
			}
		    dm.setClient(clntmapModel.getClientID());
		    dm.setSigned(false);
		    dm.setFileExtension(".pdf");
			dm.setStatus("10-LN'den dosya geldi.");
			
			dm.setDocumentUrl(dss.UNSIGNED_URL+LocalDate.now().toString()+"/"+dm.getName() + dm.getFileExtension());
	    	logmodel.setDetails("LN'den gelen dosya("+dm.getName()+") sisteme kaydedildi");
	    	try {
		    	dss.convertBytesToFile(dm.isSigned(), bytes, dm.getName(), dm.getFileExtension());
	    	}catch(Exception e1) {
	    		dm.setStatus("15-Dosya servera kaydedilemedi.");
	    		logmodel.setIssueResult("BAŞARISIZ");
				logmodel.setDetails(e1.toString());
	    	}
		    //String stream = Base64.getEncoder().encodeToString(bytes);
			//byte[] newBytes = Base64.getDecoder().decode(stream);
	    	dm.setCrt_date(formatter.format(new Date()));
	    	dm.setBinaryData(null);
	    	
			if (clntmapModel.isConnected()) {
				simpMessagingTemplate.convertAndSend("/topic/greetings/" + clntmapModel.getClientID(), dm);
				dm.setStatus("20-Client aktif ve dosyayi aldi.");
			}else {
				logmodel.setDetails("Client  aktif degil.");
				dm.setStatus("25-Client aktif degil.");
			}
			
			dm=dss.documentSaveMapping(dm);
			logmodel.setClientMACID(clntmapModel.getClientID());
			logmodel.setIssueTime(new Date());
			logmodel.setTrid(dm.getTrid());
			logmodel.setHost("");
			dss.logSaveMapping(logmodel);
		}		
		return dm;
	}*/
	
	@PostMapping(value="/saveDocumentFromClient")
	public DocumentModel saveDocumentModelFromClient(@RequestBody DocumentModel dm) {
		LogModel logmodel=new LogModel();
		DocumentModel theDocument = null;
		try {
			logmodel.setIssue("DÖKÜMAN İMZALAMA");
			logmodel.setIssueResult("BAŞARILI");
			logmodel.setDetails("Client'tan gelen dosya("+dm.getName()+") sisteme kaydedildi.");
			theDocument=dss.documentGetMappingByTrid(dm.getTrid());
			dss.convertBytesToFile(true,dm.getBinaryData(), theDocument.getName(), theDocument.getFileExtension());
			theDocument.setDocumentUrl(dss.SIGNED_URL+LocalDate.now().toString()+"/" +theDocument.getName() + theDocument.getFileExtension());
			theDocument.setBinaryData(null);
			theDocument.setSign_date(formatter.format(new Date()));
			theDocument.setSigned(true);
			theDocument.setStatus("30-Basarili bir sekilde imzalandi.");
		} catch(Exception e) {
			logmodel.setIssueResult("BAŞARISIZ");
			logmodel.setDetails("Client'tan dosya alma hatası : " + e.toString());
			theDocument.setStatus("35-Döküman imzalanamadı!");

		}
		dss.documentSaveMapping(theDocument);
		logmodel.setClientMACID(dm.getClient());
		logmodel.setIssueTime(new Date());
		logmodel.setTrid(dm.getTrid());
		dss.logSaveMapping(logmodel);
		return dm;
	}
	
	@GetMapping(value="/getDocumentModelFromClientByTrid/{trid}")
	public DocumentModel getDocumentModelByTrid(@PathVariable String trid){ // This is for the purpose of sending decoded bytes as a String to client
		DocumentModel docModel=dss.documentGetMappingByTrid(trid);
		
		try {
			docModel.setBinaryData(dss.convertDocToByteArray(docModel.getDocumentUrl()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String stream = Base64.getEncoder().encodeToString(docModel.getBinaryData());
	    docModel.setBytesData(stream);
	    docModel.setBinaryData(null);
		return docModel;
	}
	
	@GetMapping(value="/getDocumentModelFromClient/{client}/{signed}")
	public List<DocumentModel> getDocumentModelFromClient(@PathVariable String client, @PathVariable String signed) {// This is for the purpose of sending the files that is not received 
		List<DocumentModel> docModelList=null;																			//by the client in real time
		if (client !=null && signed.equals("true")) {
			docModelList=dss.documentGetMappingByClientMacId(client,true);
		}else if(client !=null && signed.equals("false")) {
			docModelList=dss.documentGetMappingByClientMacId(client,false);
		}
		
		return docModelList;
	}
     
     public String generateString() {
    	int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 5;
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    return buffer.toString();
     }
}
