package jp.autorace.dc;

// Generated 2015-10-13 3:28:11 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * AutoRaceLiveEId generated by hbm2java
 */
public class AutoRaceLiveEId implements java.io.Serializable {

	private long raceid;
	private Date timeStamp;

	public AutoRaceLiveEId() {
	}

	public AutoRaceLiveEId(long raceid, Date timeStamp) {
		this.raceid = raceid;
		this.timeStamp = timeStamp;
	}

	public long getRaceid() {
		return this.raceid;
	}

	public void setRaceid(long raceid) {
		this.raceid = raceid;
	}

	public Date getTimeStamp() {
		return this.timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AutoRaceLiveEId))
			return false;
		AutoRaceLiveEId castOther = (AutoRaceLiveEId) other;

		return (this.getRaceid() == castOther.getRaceid())
				&& ((this.getTimeStamp() == castOther.getTimeStamp()) || (this
						.getTimeStamp() != null
						&& castOther.getTimeStamp() != null && this
						.getTimeStamp().equals(castOther.getTimeStamp())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) this.getRaceid();
		result = 37 * result
				+ (getTimeStamp() == null ? 0 : this.getTimeStamp().hashCode());
		return result;
	}

}
