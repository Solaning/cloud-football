package com.kinth.football.ui.cloud5ranking;

import java.util.List;

public class Cloud5RankingBean {
	
	private Pageable pageable;
	private List<CloudFive> cloudFives;
	
	public Pageable getPageable() {
		return pageable;
	}
	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
	public List<CloudFive> getCloudFives() {
		return cloudFives;
	}
	public void setCloudFives(List<CloudFive> CloudFives) {
		this.cloudFives = CloudFives;
	}
	
}
