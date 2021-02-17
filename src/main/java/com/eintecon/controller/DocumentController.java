package com.eintecon.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
	private String UNSIGNED_URL = "/disk1/apps/files/UnSigned/";
	private String SIGNED_URL = "/disk1/apps/files/Signed/";
	
	@Autowired
	DocumentSignService dss;
	@GetMapping("/documentGetter/{DocumentID}")
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
	}
	@GetMapping(value="/saveDocumentFromLNTrial")
	//public DocumentModel saveDocumentModelFromLnTrial(@RequestBody DocumentModel dm) {
	public DocumentModel saveDocumentModelFromLn() {
		DocumentModel dm =new DocumentModel();
		LogModel logmodel=new LogModel();
		
		if (dm !=null) {
			byte[] bytes = null;
			try {
					bytes = convertDocToByteArray("/disk1/apps/apache-tomcat-8.5.61/documentSignService/files/ornek.pdf");
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
		    
		    dm.setDocumentUrl(UNSIGNED_URL+LocalDate.now().toString()+"/"+dm.getName()+"_"+dm.getTrid() + dm.getFileExtension());
	    	logmodel.setDetails("LN'den gelen dosya("+dm.getName()+") sisteme kaydedildi");
	    	try {
		    	convertBytesToFile(dm.isSigned(), bytes, dm.getName()+"_"+dm.getTrid(), dm.getFileExtension());
	    	}catch(Exception e1) {
	    		dm.setStatus("Dosya servera kaydedilemedi");
				logmodel.setDetails(e1.toString());
	    	}
		    //String stream = Base64.getEncoder().encodeToString(bytes);
			//byte[] newBytes = Base64.getDecoder().decode(stream);
	    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		    Date date = new Date();
	    	dm.setCrt_date(formatter.format(date));
	    	dm.setBinaryData(null);
	    	
			if (clntmapModel.isConnected()) {
				simpMessagingTemplate.convertAndSend("/topic/greetings/" + clntmapModel.getClientID(), dm);
				dm.setStatus("20-Client aktif ve dosyayı aldı.");
			}else {
				logmodel.setDetails("Client Active değil.");
				dm.setStatus("Client Active değil.");
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
	
	@PostMapping(value="/saveDocumentFromLN")
	public DocumentModel saveDocumentModelFromLn(@RequestBody DocumentModel dm) {
		LogModel logmodel=new LogModel();
		
		if (dm !=null) {
			byte[] bytes = dm.getBinaryData();
			
			ClientMappingModel clntmapModel=new ClientMappingModel();
			clntmapModel=dss.getClientMapping(dm.getErpID());
			if(clntmapModel == null) {
				logmodel.setDetails("Client veriatabininda bulunamadi.");
				clntmapModel = dss.getClientMapping("aa11");
				//return null;
			}
		    dm.setClient(clntmapModel.getClientID());
		    dm.setSigned(false);
		    dm.setFileExtension(".pdf");
			dm.setStatus("10-LN'den dosya geldi.");
			
			dm.setDocumentUrl(UNSIGNED_URL+LocalDate.now().toString()+"/"+dm.getName()+"_"+dm.getTrid() + dm.getFileExtension());
	    	logmodel.setDetails("LN'den gelen dosya("+dm.getName()+") sisteme kaydedildi");
	    	try {
		    	convertBytesToFile(dm.isSigned(), bytes, dm.getName()+"_"+dm.getTrid(), dm.getFileExtension());
	    	}catch(Exception e1) {
	    		dm.setStatus("15-Dosya servera kaydedilemedi.");
				logmodel.setDetails(e1.toString());
	    	}
		    //String stream = Base64.getEncoder().encodeToString(bytes);
			//byte[] newBytes = Base64.getDecoder().decode(stream);
	    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		    Date date = new Date();
	    	dm.setCrt_date(formatter.format(date));
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
	}
	
	@PostMapping(value="/saveDocumentFromClient")
	public DocumentModel saveDocumentModelFromClient(@RequestBody DocumentModel dm) {
		LogModel logmodel=new LogModel();
		DocumentModel theDocument = null;
		try {
			logmodel.setDetails("Client'tan gelen dosya("+dm.getName()+") sisteme kaydedildi.");
			convertBytesToFile(true,dm.getBinaryData(), dm.getName(), dm.getFileExtension());
			theDocument=dss.documentGetMappingByTrid(dm.getTrid());
			theDocument.setDocumentUrl(SIGNED_URL+LocalDate.now().toString()+"/" +dm.getName() + "_" + dm.getTrid());
			theDocument.setBinaryData(null);
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		    Date date = new Date();  
			theDocument.setSign_date(formatter.format(date));
			theDocument.setSigned(true);
			theDocument.setStatus("30-Basarili bir sekilde imzalandi.");
		} catch(Exception e) {
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
	public DocumentModel getDocumentModelByTrid(@PathVariable String trid){
		DocumentModel docModel=dss.documentGetMappingByTrid(trid);
		// unsigned dosyadan dosyanın adı ile dosyayı bulacagiz.
		try {
			docModel.setBinaryData(convertDocToByteArray(docModel.getDocumentUrl()));
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
	public List<DocumentModel> getDocumentModelFromClient(@PathVariable String client, @PathVariable String signed) {		
		List<DocumentModel> docModelList=null;
		if (client !=null && signed.equals("true")) {
			docModelList=dss.documentGetMappingByClientMacId(client,true);
		}else if(client !=null && signed.equals("false")) {
			docModelList=dss.documentGetMappingByClientMacId(client,false);
		}
		
		return docModelList;
	}
	
	 public static byte[] convertDocToByteArray(String path)throws FileNotFoundException, IOException{
		File file = new File(path);

        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
            }
        } catch (IOException ex) {
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
	 }
	 
     public void convertBytesToFile(boolean isSigned, byte[] bytes, String fileName, String fileExtension) {
    	 String url = null;
    	 File theDir;
    	 String pathWithDate;
    	 if(isSigned) {
    		pathWithDate =SIGNED_URL+LocalDate.now().toString()+"/";
    		theDir = new File(pathWithDate);
			if(!theDir.exists()){
				theDir.mkdirs();
			}
    		url = pathWithDate;
    	 }else {
    		pathWithDate =UNSIGNED_URL+LocalDate.now().toString()+"/";
    		theDir = new File(pathWithDate);
 			if(!theDir.exists()){
 				theDir.mkdirs();
 			}
    		url =  pathWithDate;
    	 }
    	 
    	 try (FileOutputStream fos = new FileOutputStream((url+fileName+fileExtension))) {
    		   fos.write(bytes);
    		} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
