package com.kinth.football.bean.moments;

import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.kinth.football.bean.Pageable;

/**
 * 朋友圈的服务器回复数据格式
 * 
 * @author Sola
 *
 */
public class MomentsResponse implements Parcelable {
	private Pageable pageable;
	private List<SharingWithComments> sharings;

	public static final Parcelable.Creator<MomentsResponse> CREATOR = new Creator<MomentsResponse>() {

		@Override
		public MomentsResponse[] newArray(int size) {
			return new MomentsResponse[size];
		}

		@Override
		public MomentsResponse createFromParcel(Parcel source) {
			MomentsResponse momentResponse = new MomentsResponse();
			momentResponse.pageable = source.readParcelable(Pageable.class
					.getClassLoader());

			Parcelable[] pars = source
					.readParcelableArray(SharingWithComments.class
							.getClassLoader());
			momentResponse.sharings = Arrays.asList(Arrays.asList(pars)
					.toArray(new SharingWithComments[pars.length]));

			return momentResponse;
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.pageable, flags);
		dest.writeParcelableArray(this.sharings
				.toArray(new SharingWithComments[this.sharings.size()]), flags);
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public List<SharingWithComments> getSharings() {
		return sharings;
	}

	public void setSharings(List<SharingWithComments> sharings) {
		this.sharings = sharings;
	}

}
