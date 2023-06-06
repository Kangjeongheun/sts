package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class BlogListDomain {

	private String bgSeq;
	private String mbId;
	private String bgTitle;
	private String bgContent;
	private String bgCreateAt;
	private String bgUpdateAt;

}