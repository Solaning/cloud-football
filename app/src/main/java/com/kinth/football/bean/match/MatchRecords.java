package com.kinth.football.bean.match;

import java.util.List;

import com.kinth.football.bean.Record;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 球队总比赛战绩和最近最多5场的比赛结果
 * @author Sola
 *
 */
public class MatchRecords implements Parcelable{

	private Record record;
	private List<String> results;//最近最多5场比赛结果
	
	public static final Parcelable.Creator<MatchRecords> CREATOR = new Creator<MatchRecords>() {

		@Override
		public MatchRecords createFromParcel(Parcel parcel) {
			MatchRecords record = new MatchRecords();
			parcel.readParcelable(Record.class.getClassLoader());
			parcel.readStringList(record.results);
			return record;
		}

		@Override
		public MatchRecords[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MatchRecords[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.record, flags);
		dest.writeStringList(this.results);
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}

}
