package com.co.kr.service;


import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.code.Code;
import com.co.kr.domain.BlogContentDomain;
import com.co.kr.domain.BlogFileDomain;
import com.co.kr.domain.BlogListDomain;
import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.UploadMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UploadServiceImple implements UploadService {

	@Autowired
	private UploadMapper uploadMapper;
	
	@Override
	public List<BoardListDomain> boardList() {
		
		return uploadMapper.boardList();
	}

	@Override
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) {
		
		HttpSession session = httpReq.getSession();
		
	
		BoardContentDomain boardContentDomain = BoardContentDomain.builder()
				.mbId(session.getAttribute("id").toString())
				.bdTitle(fileListVO.getTitle())
				.bdContent(fileListVO.getContent())
				.build();
		
				if(fileListVO.getIsEdit() != null) {
					boardContentDomain.setBdSeq(Integer.parseInt(fileListVO.getSeq()));
					System.out.println("수정업데이트");
					
					uploadMapper.bdContentUpdate(boardContentDomain);
				}else {	
					
					uploadMapper.contentUpload(boardContentDomain);
					System.out.println(" db 인서트");

				}
				
				
				int bdSeq = boardContentDomain.getBdSeq();
				String mbId = boardContentDomain.getMbId();
				
				
				List<MultipartFile> multipartFiles = request.getFiles("files");
				
				
			
				if(fileListVO.getIsEdit() != null) { 
	
					List<BoardFileDomain> fileList = null;
					
					
					
					for (MultipartFile multipartFile : multipartFiles) {
						
						if(!multipartFile.isEmpty()) {  
							
							
							if(session.getAttribute("files") != null) {	

								fileList = (List<BoardFileDomain>) session.getAttribute("files");
								
								for (BoardFileDomain list : fileList) {
									list.getUpFilePath();
									Path filePath = Paths.get(list.getUpFilePath());
							 
							        try {
							        	
							            
							            Files.deleteIfExists(filePath);
										bdFileRemove(list); 
							        } catch (DirectoryNotEmptyException e) {
										throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
							        } catch (IOException e) {
							            e.printStackTrace();
							        }
								}
								

							}
							
							
						}

					}
					
					
				}
				
				
				
				Path rootPath = Paths.get(new File("C://").toString(),"upload", File.separator).toAbsolutePath().normalize();			
				File pathCheck = new File(rootPath.toString());
				
				
				if(!pathCheck.exists()) pathCheck.mkdirs();
				
				
	
				for (MultipartFile multipartFile : multipartFiles) {
					
					if(!multipartFile.isEmpty()) {  
						
						String originalFileExtension;
						String contentType = multipartFile.getContentType();
						String origFilename = multipartFile.getOriginalFilename();
						
						
						if(ObjectUtils.isEmpty(contentType)){
							break;
						}else { 
							if(contentType.contains("image/jpeg")) {
								originalFileExtension = ".jpg";
							}else if(contentType.contains("image/png")) {
								originalFileExtension = ".png";
							}else {
								break;
							}
						}
						
						
						String uuid = UUID.randomUUID().toString();
						String current = CommonUtils.currentTime();
						String newFileName = uuid + current + originalFileExtension;
						
					
						Path targetPath = rootPath.resolve(newFileName);
						
						File file = new File(targetPath.toString());
						
						try {
							
							multipartFile.transferTo(file);
							file.setWritable(true);
							file.setReadable(true);
							
							
							
							BoardFileDomain boardFileDomain = BoardFileDomain.builder()
									.bdSeq(bdSeq)
									.mbId(mbId)
									.upOriginalFileName(origFilename)
									.upNewFileName("resources/upload/"+newFileName) // WebConfig에 동적 이미지 폴더 생성 했기때문
									.upFilePath(targetPath.toString())
									.upFileSize((int)multipartFile.getSize())
									.build();
							
								
								uploadMapper.fileUpload(boardFileDomain);
								System.out.println("upload done");
							
						} catch (IOException e) {
							throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
						}
					}

				}
				
		
				return bdSeq; 
	}

	@Override
	public void bdContentRemove(HashMap<String, Object> map) {
		uploadMapper.bdContentRemove(map);
	}

	@Override
	public void bdFileRemove(BoardFileDomain boardFileDomain) {
		uploadMapper.bdFileRemove(boardFileDomain);
	}

	@Override
	public BoardListDomain boardSelectOne(HashMap<String, Object> map) {
		
		return uploadMapper.boardSelectOne(map);
	}

	@Override
	public List<BoardFileDomain> boardSelectOneFile(HashMap<String, Object> map) {
		
		return uploadMapper.boardSelectOneFile(map);
	}

	@Override
	public List<BlogListDomain> blogList() {
		
		return uploadMapper.blogList();
	}

	
	
	
	
	
	//blog 파일 저장
	@Override
	public int bgfileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) {
		HttpSession session = httpReq.getSession();
		
		
		BlogContentDomain blogContentDomain = BlogContentDomain.builder()
				.mbId(session.getAttribute("id").toString())
				.bgTitle(fileListVO.getTitle())
				.bgContent(fileListVO.getContent())
				.build();
		
				if(fileListVO.getIsEdit() != null) {
					blogContentDomain.setBgSeq(Integer.parseInt(fileListVO.getSeq()));
					System.out.println("수정업데이트");
				
					uploadMapper.bgContentUpdate(blogContentDomain);
				}else {	
					
					uploadMapper.bgcontentUpload(blogContentDomain);
					System.out.println(" db 인서트");
				}
				
				
				int bgSeq = blogContentDomain.getBgSeq();
				String mbId = blogContentDomain.getMbId();
				
				List<MultipartFile> multipartFiles = request.getFiles("files");
				
				
				if(fileListVO.getIsEdit() != null) { // 수정시 

	
					List<BlogFileDomain> fileList = null;
					
					
					
					for (MultipartFile multipartFile : multipartFiles) {
						
						if(!multipartFile.isEmpty()) {   // 수정시 새로 파일 첨부될때 세션에 담긴 파일 지우기
							
							
							if(session.getAttribute("files") != null) {	

								fileList = (List<BlogFileDomain>) session.getAttribute("files");
								
								for (BlogFileDomain list : fileList) {
									list.getUpFilePathbg();
									Path filePath = Paths.get(list.getUpFilePathbg());
							 
							        try {
							        	
							           
							            Files.deleteIfExists(filePath); 
							          
										bgFileRemove(list); 
										
							        } catch (DirectoryNotEmptyException e) {
										throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
							        } catch (IOException e) {
							            e.printStackTrace();
							        }
								}
								

							}
							
							
						}

					}
					
					
				}
				
				
				
				Path rootPath = Paths.get(new File("C://").toString(),"upload", File.separator).toAbsolutePath().normalize();			
				File pathCheck = new File(rootPath.toString());
				
				
				if(!pathCheck.exists()) pathCheck.mkdirs();
				
				
	
				for (MultipartFile multipartFile : multipartFiles) {
					
					if(!multipartFile.isEmpty()) {  
						
						
						String originalFileExtensionbg;
						String contentTypebg = multipartFile.getContentType();
						String origFilenamebg = multipartFile.getOriginalFilename();
						
				
						if(ObjectUtils.isEmpty(contentTypebg)){
							break;
						}else { 
							if(contentTypebg.contains("image/jpeg")) {
								originalFileExtensionbg = ".jpg";
							}else if(contentTypebg.contains("image/png")) {
								originalFileExtensionbg = ".png";
							}else {
								break;
							}
						}
						
						String uuid = UUID.randomUUID().toString();
						String current = CommonUtils.currentTime();
						String newFileName = uuid + current + originalFileExtensionbg;
						
						//최종경로까지 지정
						Path targetPath = rootPath.resolve(newFileName);
						
						File file = new File(targetPath.toString());
						
						try {
							
							multipartFile.transferTo(file);
							
							file.setWritable(true);
							file.setReadable(true);
							
							
							//파일 domain 생성 
							BlogFileDomain blogFileDomain = BlogFileDomain.builder()
									.bgSeq(bgSeq)
									.mbId(mbId)
									.upOriginalFileNamebg(origFilenamebg)
									.upNewFileNamebg("resources/upload/"+newFileName) // WebConfig에 동적 이미지 폴더 생성 했기때문
									.upFilePathbg(targetPath.toString())
									.upFileSizebg((int)multipartFile.getSize())
									.build();
							
							
								uploadMapper.bgfileUpload(blogFileDomain);
								System.out.println("upload done");
							
						} catch (IOException e) {
							throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
						}
					}

				}
				
		
				return bgSeq; 
	}
	
	@Override
	public void bgContentRemove(HashMap<String, Object> map) {
		uploadMapper.bgContentRemove(map);
		
	}


	@Override
	public void bgFileRemove(BlogFileDomain blogFileDomain) {
		uploadMapper.bgFileRemove(blogFileDomain);		
	}

	@Override
	public BlogListDomain bgSelectOne(HashMap<String, Object> map) {
		return uploadMapper.bgSelectOne(map);
	}

	@Override
	public List<BlogFileDomain> bgSelectOneFile(HashMap<String, Object> map) {
		return uploadMapper.bgSelectOneFile(map);
	}

	

	

	

	
		
	}
	
	

