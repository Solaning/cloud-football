package com.kinth.football.bean;

/**
 * 分页
 * 
 * @author Sola
 *
 */
public class Paging {
	private Pageable pageable;
	private int totalGotElements;// 目前已经获取的服务器上的动态条数
	private long beginDate;// 该次分页的发起时间点
	private long endDate;//该分页对应的结束时间

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public int getTotalGotElements() {
		return totalGotElements;
	}

	public void setTotalGotElements(int totalGotElements) {
		this.totalGotElements = totalGotElements;
	}

	public long getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(long beginDate) {
		this.beginDate = beginDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	
	
}
