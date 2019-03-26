package jp.autorace.dc;

// Generated 2015-10-13 3:28:11 by Hibernate Tools 3.4.0.CR1

/**
 * AutoRaceDividendId generated by hbm2java
 */
public class AutoRaceDividendId implements java.io.Serializable {

	private long raceId;
	private byte betTypeId;
	private String combination;

	public AutoRaceDividendId() {
	}

	public AutoRaceDividendId(long raceId, byte betTypeId, String combination) {
		this.raceId = raceId;
		this.betTypeId = betTypeId;
		this.combination = combination;
	}

	public long getRaceId() {
		return this.raceId;
	}

	public void setRaceId(long raceId) {
		this.raceId = raceId;
	}

	public byte getBetTypeId() {
		return this.betTypeId;
	}

	public void setBetTypeId(byte betTypeId) {
		this.betTypeId = betTypeId;
	}

	public String getCombination() {
		return this.combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AutoRaceDividendId))
			return false;
		AutoRaceDividendId castOther = (AutoRaceDividendId) other;

		return (this.getRaceId() == castOther.getRaceId())
				&& (this.getBetTypeId() == castOther.getBetTypeId())
				&& ((this.getCombination() == castOther.getCombination()) || (this
						.getCombination() != null
						&& castOther.getCombination() != null && this
						.getCombination().equals(castOther.getCombination())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) this.getRaceId();
		result = 37 * result + this.getBetTypeId();
		result = 37
				* result
				+ (getCombination() == null ? 0 : this.getCombination()
						.hashCode());
		return result;
	}

}