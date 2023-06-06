package com.co.kr.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "builder")

public class BlogFileDomain {
	private Integer bgSeq;
	private String mbId;
	
	private String upOriginalFileNamebg;
	private String upNewFileNamebg; 
	private String upFilePathbg;
	private Integer upFileSizebg;

}
