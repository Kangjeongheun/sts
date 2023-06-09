package com.co.kr.controller;


import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.MirroredTypeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import com.co.kr.domain.BoardListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.code.Code;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.mapper.UploadMapper;
import com.co.kr.service.UploadService;
import com.co.kr.vo.FileListVO;

import groovyjarjarantlr4.v4.parse.ANTLRParser.throwsSpec_return;
import lombok.extern.slf4j.Slf4j;

import com.co.kr.domain.BlogFileDomain;
import com.co.kr.domain.BlogListDomain;



@Slf4j
@Controller
public class FileListController {
	
	@Autowired
	private UploadService uploadService;
	
	
	@PostMapping(value = "upload")
	public ModelAndView bdUpload(FileListVO fileListVO,MultipartHttpServletRequest request, HttpServletRequest httpReq)throws IOException, ParseException{
		
		ModelAndView mav = new ModelAndView();
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq);
		fileListVO.setContent("");
		fileListVO.setTitle("");
		
		mav =bdSelectOneCall(fileListVO,String.valueOf(bdSeq),request);
		mav.setViewName("board/boardList.html");
		return mav;
	}

	
	public ModelAndView bdSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO, String bdSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
			
		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain =uploadService.boardSelectOne(map);
		System.out.println("boardListDomain"+boardListDomain);
		List<BoardFileDomain> fileList =  uploadService.boardSelectOneFile(map);
		
		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);

	
		session.setAttribute("files", fileList);

		return mav;
	}
	
	@GetMapping("detail") 
	public ModelAndView bdDetail(@ModelAttribute("fileListVO")FileListVO fileListVO, @RequestParam("bdSeq")String bdSep, HttpServletRequest request) throws IOException{
		ModelAndView mav = new ModelAndView();
		
		mav =bdSelectOneCall(fileListVO,bdSep,request);
		mav.setViewName("board/boardList.html");
		return mav;
		}
	
	@GetMapping("edit") //수정하기
	public ModelAndView edit(FileListVO fileListVO, @RequestParam("bdSeq")String bdSeq, HttpServletRequest request ) throws IOException{
		ModelAndView mav = new ModelAndView();
		
		HashMap<String, Object> map= new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain = uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
		
		
		for (BoardFileDomain list  : fileList) {
			String path =list .getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		
		fileListVO.setSeq(boardListDomain.getBdSeq());
		fileListVO.setContent(boardListDomain.getBdContent());
		fileListVO.setTitle(boardListDomain.getBdTitle());
		fileListVO.setIsEdit("edit"); 
		
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen",fileList.size());
		
		mav.setViewName("board/boardEditList.html");		
		return mav;
		}
	
	@PostMapping("editSave")//수정한거 저장하기 
	public ModelAndView editSave(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq)throws IOException{
		ModelAndView mav= new ModelAndView();
		
		uploadService.fileProcess(fileListVO, request, httpReq); //저장하는 부분
		
		
		mav=bdSelectOneCall(fileListVO, fileListVO.getSeq(),request);
		
		mav.setViewName("board/boardList.html");
		return mav;		
	}
	@GetMapping("remove")
	public ModelAndView mbRemove(@RequestParam("bdSeq")String bdSeq, HttpServletRequest request) throws IOException{
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		HashMap<String ,Object> map = new HashMap<String, Object>();
		List<BoardFileDomain> fileList = null;
		if(session.getAttribute("files")!=null) {
			fileList =(List<BoardFileDomain>) session.getAttribute("files");
			}
		
		map.put("bdSeq", Integer.parseInt(bdSeq));
		
		uploadService.bdContentRemove(map);
		
		for(BoardFileDomain list : fileList) {
			list.getUpFilePath();
			Path filePath = Paths.get(list.getUpFilePath());
			
			try {
				
				Files.deleteIfExists(filePath);
				
				uploadService.bdFileRemove(list);
				
				
			}catch (DirectoryNotEmptyException e) {
				throw RequestException.fire(Code.E404,"디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
				
			
		session.removeAttribute("files");
		mav =bdListCall();
		mav.setViewName("board/boardList.html");
		
		return mav;	
	
	}
	
	public ModelAndView bdListCall() {
		ModelAndView mav = new ModelAndView();
		List<BoardListDomain> items = uploadService.boardList();
		mav.addObject("items", items);
		
		return mav;
		
	}
	
	@PostMapping(value = "bgupload")
	public ModelAndView bgUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException {
		ModelAndView mav = new ModelAndView();
		int bgSeq = uploadService.bgfileProcess(fileListVO, request, httpReq);
		fileListVO.setContent(""); //초기화
		fileListVO.setTitle(""); //초기화
		
		
		mav = bgSelectOneCall(fileListVO, String.valueOf(bgSeq),request);
		mav.setViewName("blog/blogList.html");
		return mav;
	}
	
	public ModelAndView bgSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO, String bgSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("bgSeq", Integer.parseInt(bgSeq));
		BlogListDomain blogListDomain =uploadService.bgSelectOne(map);
		System.out.println("blogListDomain"+blogListDomain);
		List<BlogFileDomain> fileList =  uploadService.bgSelectOneFile(map);
		
		for (BlogFileDomain list : fileList) {
			String path = list.getUpFilePathbg().replaceAll("\\\\", "/");
			list.setUpFilePathbg(path);
		}
		mav.addObject("bgdetail", blogListDomain);
		mav.addObject("files", fileList);

		
		session.setAttribute("files", fileList);

		return mav;
	}
		
	@GetMapping("bgdetail")
    public ModelAndView bgDetail(@ModelAttribute("fileListVO") FileListVO fileListVO, @RequestParam("bgSeq") String bgSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
	
		mav = bgSelectOneCall(fileListVO, bgSeq,request);
		mav.setViewName("blog/blogList.html");
		return mav;
	}
	


	@GetMapping("bgedit")
		public ModelAndView bgedit(FileListVO fileListVO, @RequestParam("bgSeq") String bgSeq, HttpServletRequest request) throws IOException {
			ModelAndView mav = new ModelAndView();

			HashMap<String, Object> map = new HashMap<String, Object>();
			HttpSession session = request.getSession();
			
			map.put("bgSeq", Integer.parseInt(bgSeq));
			BlogListDomain blogListDomain =uploadService.bgSelectOne(map);
			List<BlogFileDomain> fileList =  uploadService.bgSelectOneFile(map);
			
			for (BlogFileDomain list : fileList) {
				String path = list.getUpFilePathbg().replaceAll("\\\\", "/");
				list.setUpFilePathbg(path);
			}

			fileListVO.setSeq(blogListDomain.getBgSeq());
			fileListVO.setContent(blogListDomain.getBgContent());
			fileListVO.setTitle(blogListDomain.getBgTitle());
			fileListVO.setIsEdit("bgedit");  
		
			mav.addObject("bgdetail", blogListDomain);
			mav.addObject("files", fileList);
			mav.addObject("fileLen",fileList.size());
			
			mav.setViewName("blog/blogEditList.html");
			return mav;
		}
	
	

	@PostMapping("bgeditSave")
		public ModelAndView bgeditSave(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
			ModelAndView mav = new ModelAndView();
			
			
			uploadService.bgfileProcess(fileListVO, request, httpReq);
			
			mav = bgSelectOneCall(fileListVO, fileListVO.getSeq(),request);
			fileListVO.setContent(""); //초기화
			fileListVO.setTitle(""); //초기화
			mav.setViewName("blog/blogList.html");
			return mav;
		}
	
	@GetMapping("bgremove")
	public ModelAndView bgRemove(@RequestParam("bgSeq") String bgSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<BlogFileDomain> fileList = null;
		if(session.getAttribute("files") != null) {						
			fileList = (List<BlogFileDomain>) session.getAttribute("files");
		}

		map.put("bgSeq", Integer.parseInt(bgSeq));
		
		
		uploadService.bgContentRemove(map);

		for (BlogFileDomain list : fileList) {
			list.getUpFilePathbg();
			Path filePath = Paths.get(list.getUpFilePathbg());
	 
	        try {
	        	
	            
	            Files.deleteIfExists(filePath); 
							uploadService.bgFileRemove(list);
				
	        } catch (DirectoryNotEmptyException e) {
							throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}

	
		session.removeAttribute("files"); 
		mav = bgListCall();
		mav.setViewName("blog/blogList.html");
		
		return mav;
	}


public ModelAndView bgListCall() {
	ModelAndView mav = new ModelAndView();
	List<BlogListDomain> items = uploadService.blogList();
	mav.addObject("items", items);
	return mav;
}



	

	
	
	
	
}
	

