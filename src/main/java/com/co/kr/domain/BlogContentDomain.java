package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "builder")
public class BlogContentDomain {
	
	private Integer bgSeq;
	private String mbId;

	private String bgTitle;
	private String bgContent;

}
