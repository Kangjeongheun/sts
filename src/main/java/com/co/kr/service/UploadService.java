package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.BlogFileDomain;
import com.co.kr.domain.BlogListDomain;
import com.co.kr.vo.FileListVO;

public interface UploadService {
	
	
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	public List<BoardListDomain> boardList();

	public void bdContentRemove(HashMap<String, Object> map);
	public void bdFileRemove(BoardFileDomain boardFileDomain);
	
	public BoardListDomain boardSelectOne(HashMap<String, Object> map);

	public List<BoardFileDomain> boardSelectOneFile(HashMap<String, Object> map);
	
	
	
	
	public int bgfileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	
	public List<BlogListDomain> blogList();
	
	public void bgContentRemove(HashMap<String, Object> map);


	public void bgFileRemove(BlogFileDomain blogFileDomain);
	

	public BlogListDomain bgSelectOne(HashMap<String, Object> map);

	public List<BlogFileDomain> bgSelectOneFile(HashMap<String, Object> map);
	

}