package com.eintecon.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
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
	
	public String UNSIGNED_URL = "/disk1/apps/files/UnSigned/";
	public String SIGNED_URL = "/disk1/apps/files/Signed/";
	
	
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
	public List<LogModel> logGetMappingByClientMACID(String macId){
		return logRepo.findByClientMACID(macId);
	}
	
	public LogModel logSaveMapping(LogModel log) {
		return logRepo.save(log);
	}
	public DocumentModel documentGetMappingByTrid(String trid) {
		return docRepo.findDocumentModelByTrid(trid);
	}
	
	public byte[] convertDocToByteArray(String path)throws FileNotFoundException, IOException{
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
}
