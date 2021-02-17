package com.eintecon.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.List;

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
	private String UNSIGNED_URL = "C:/Temp/disk1/apps/files/UnSigned/";
	private String SIGNED_URL = "C:/Temp/disk1/apps/files/Signed/";
	
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
	@PostMapping(value="/saveDocumentFromLN")
	public DocumentModel saveDocumentModelFromLn(@RequestBody DocumentModel dm) {
		LogModel logmodel=new LogModel();
		
		if (dm !=null) {
			byte[] bytes = dm.getBinaryData();
			
			ClientMappingModel clntmapModel=new ClientMappingModel();
			clntmapModel=dss.getClientMapping(dm.getErpID());//erpid olacak
			if(clntmapModel == null) {
				clntmapModel = dss.getClientMapping("aa11");
			}
			
			dm.setTrid(dm.getTrid());
			dm.setName(dm.getName());
		    dm.setClient(clntmapModel.getClientID());
		    dm.setSigned(false);
		    dm.setDocumentUrl(UNSIGNED_URL+LocalDate.now().toString()+"/"+dm.getName()+"_"+dm.getTrid() + ".pdf");
		    dm.setFileExtansion(".pdf");
		    try {
		    	logmodel.setDetails("LN'den gelen dosya sisteme kaydedildi");
		    	convertBytesToFile(dm.isSigned(), bytes, dm.getName()+"_"+dm.getTrid(), dm.getFileExtansion());
			    //String stream = Base64.getEncoder().encodeToString(bytes);
				//byte[] newBytes = Base64.getDecoder().decode(stream);
		    	dm.setCrt_date(LocalDate.now().toString());
				dm=dss.documentSaveMapping(dm);
				dm.setBinaryData(null);
				simpMessagingTemplate.convertAndSend("/topic/greetings/" + clntmapModel.getClientID(), dm);
			} catch(Exception e) {
				logmodel.setDetails(e.toString());
			}
		    
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
		
		try {
			
			logmodel.setDetails("Client'tan gelen dosya sisteme kaydedildi");
			
			convertBytesToFile(true,dm.getBinaryData(), dm.getName(), dm.getFileExtansion());
			dm.setDocumentUrl(SIGNED_URL+LocalDate.now().toString()+"/" +dm.getName() + "_" + dm.getTrid());
			dm.setBinaryData(null);
			dm=dss.documentSaveMapping(dm);
		} catch(Exception e) {
			logmodel.setDetails(e.toString());
		}
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
	
	@GetMapping(value="/getDocumentModelFromClient/{client}")
	public List<DocumentModel> getDocumentModelFromClient(@PathVariable String client) {		
		List<DocumentModel> docModelList=null;
		if (client !=null) {
			docModelList=dss.documentGetMappingByClientMacId(client);
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
     
     /*public String generateString() {
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
     }*/
}
