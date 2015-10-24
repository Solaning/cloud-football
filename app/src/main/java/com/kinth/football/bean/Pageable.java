package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 分页
 * @author Sola
 *
 */
public class Pageable implements Parcelable {

	private int totalPages; // 总页数
	private int totalElements; // 总元素

	public static final Parcelable.Creator<Pageable> CREATOR = new Creator<Pageable>() {
		@Override
		public Pageable createFromParcel(Parcel parcel) {
			Pageable pageable = new Pageable();
			pageable.totalPages = parcel.readInt();
			pageable.totalElements = parcel.readInt();

			return pageable;
		}

		@Override
		public Pageable[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Pageable[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(this.totalPages);
		dest.writeInt(this.totalElements);
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}

}
