package jp.autorace.dc;

// Generated 2015-10-13 3:28:11 by Hibernate Tools 3.4.0.CR1

/**
 * AutoRacePostRacePlayerId generated by hbm2java
 */
public class AutoRacePostRacePlayerId implements java.io.Serializable {

	private long raceId;
	private int playerId;

	public AutoRacePostRacePlayerId() {
	}

	public AutoRacePostRacePlayerId(long raceId, int playerId) {
		this.raceId = raceId;
		this.playerId = playerId;
	}

	public long getRaceId() {
		return this.raceId;
	}

	public void setRaceId(long raceId) {
		this.raceId = raceId;
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AutoRacePostRacePlayerId))
			return false;
		AutoRacePostRacePlayerId castOther = (AutoRacePostRacePlayerId) other;

		return (this.getRaceId() == castOther.getRaceId())
				&& (this.getPlayerId() == castOther.getPlayerId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) this.getRaceId();
		result = 37 * result + this.getPlayerId();
		return result;
	}

}
