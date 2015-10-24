package com.kinth.football.bean;

import java.util.List;



import android.os.Parcel;
import android.os.Parcelable;

public class FeedbackBean  implements Parcelable {
  private String title;
  private String description;
  private List<String> pictures;
  public static final Parcelable.Creator<FeedbackBean> CREATOR = new Creator<FeedbackBean>(){

	@Override
	public FeedbackBean createFromParcel(Parcel parcel) {
      FeedbackBean feedbackBean = new FeedbackBean();
      feedbackBean.title = parcel.readString();
      feedbackBean.description = parcel.readString();
       parcel.readStringList(feedbackBean.pictures);
		return feedbackBean;
	}

	@Override
	public FeedbackBean[] newArray(int arg0) {
		// TODO Auto-generated method stub
		return new FeedbackBean[arg0];
	}
	  
  };
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(this.title);
		dest.writeString(this.description);
		dest.writeStringList(this.pictures);
	}
	public String gettitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getPictures() {
		return pictures;
	}

	public void setUrlList(List<String> pictures) {
		this.pictures = pictures;
	}

}
