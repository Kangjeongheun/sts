package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.BlogContentDomain;
import com.co.kr.domain.BlogFileDomain;
import com.co.kr.domain.BlogListDomain;
import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;

@Mapper
public interface UploadMapper {

	
	public List<BoardListDomain> boardList();

	
	public void contentUpload(BoardContentDomain boardContentDomain);
	public void fileUpload(BoardFileDomain boardFileDomain);

	public void bdContentUpdate(BoardContentDomain boardContentDomain);
	public void bdFileUpdate(BoardFileDomain boardFileDomain);

	public void bdContentRemove(HashMap<String, Object> map);
	public void bdFileRemove(BoardFileDomain boardFileDomain);
	
	public BoardListDomain boardSelectOne(HashMap<String, Object> map);
	public List<BoardFileDomain> boardSelectOneFile(HashMap<String, Object> map);
	
	
	public List<BlogListDomain> blogList();
	
	
	public void bgcontentUpload(BlogContentDomain blogContentDomain);
	public void bgfileUpload(BlogFileDomain blogFileDomain);

	
	public void bgContentUpdate(BlogContentDomain blogContentDomain);
	public void bgFileUpdate(BlogFileDomain blogFileDomain);

	public void bgContentRemove(HashMap<String, Object> map);
	public void bgFileRemove(BlogFileDomain blogFileDomain);
	
	public BlogListDomain bgSelectOne(HashMap<String, Object> map);
	public List<BlogFileDomain> bgSelectOneFile(HashMap<String, Object> map);

}