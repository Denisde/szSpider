package jp.autorace.dc;

// Generated 2015-10-13 3:28:11 by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;

/**
 * AutoRacePreRacePlayerLive generated by hbm2java
 */
public class AutoRacePreRacePlayerLive implements java.io.Serializable {

	private AutoRacePreRacePlayerLiveId id;
	private Long uraceId;
	private String playerName;
	private String playerNameEn;
	private Byte clothNo;
	private String lg;
	private Byte age;
	private BigDecimal handicap;
	private String playerImagePath;
	private String motorName;
	private String motorClass;
	private String period;
	private String currentRank;
	private String lastRank;
	private String examinationPoint;
	private Boolean scratch;
	private Date extractTime;
	private BigDecimal trialT;
	private BigDecimal trialOffset;

	public AutoRacePreRacePlayerLive() {
	}

	public AutoRacePreRacePlayerLive(AutoRacePreRacePlayerLiveId id) {
		this.id = id;
	}

	public AutoRacePreRacePlayerLive(AutoRacePreRacePlayerLiveId id,
			Long uraceId, String playerName, String playerNameEn, Byte clothNo,
			String lg, Byte age, BigDecimal handicap, String playerImagePath,
			String motorName, String motorClass, String period,
			String currentRank, String lastRank, String examinationPoint,
			Boolean scratch, Date extractTime, BigDecimal trialT,
			BigDecimal trialOffset) {
		this.id = id;
		this.uraceId = uraceId;
		this.playerName = playerName;
		this.playerNameEn = playerNameEn;
		this.clothNo = clothNo;
		this.lg = lg;
		this.age = age;
		this.handicap = handicap;
		this.playerImagePath = playerImagePath;
		this.motorName = motorName;
		this.motorClass = motorClass;
		this.period = period;
		this.currentRank = currentRank;
		this.lastRank = lastRank;
		this.examinationPoint = examinationPoint;
		this.scratch = scratch;
		this.extractTime = extractTime;
		this.trialT = trialT;
		this.trialOffset = trialOffset;
	}

	public AutoRacePreRacePlayerLiveId getId() {
		return this.id;
	}

	public void setId(AutoRacePreRacePlayerLiveId id) {
		this.id = id;
	}

	public Long getUraceId() {
		return this.uraceId;
	}

	public void setUraceId(Long uraceId) {
		this.uraceId = uraceId;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerNameEn() {
		return this.playerNameEn;
	}

	public void setPlayerNameEn(String playerNameEn) {
		this.playerNameEn = playerNameEn;
	}

	public Byte getClothNo() {
		return this.clothNo;
	}

	public void setClothNo(Byte clothNo) {
		this.clothNo = clothNo;
	}

	public String getLg() {
		return this.lg;
	}

	public void setLg(String lg) {
		this.lg = lg;
	}

	public Byte getAge() {
		return this.age;
	}

	public void setAge(Byte age) {
		this.age = age;
	}

	public BigDecimal getHandicap() {
		return this.handicap;
	}

	public void setHandicap(BigDecimal handicap) {
		this.handicap = handicap;
	}

	public String getPlayerImagePath() {
		return this.playerImagePath;
	}

	public void setPlayerImagePath(String playerImagePath) {
		this.playerImagePath = playerImagePath;
	}

	public String getMotorName() {
		return this.motorName;
	}

	public void setMotorName(String motorName) {
		this.motorName = motorName;
	}

	public String getMotorClass() {
		return this.motorClass;
	}

	public void setMotorClass(String motorClass) {
		this.motorClass = motorClass;
	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getCurrentRank() {
		return this.currentRank;
	}

	public void setCurrentRank(String currentRank) {
		this.currentRank = currentRank;
	}

	public String getLastRank() {
		return this.lastRank;
	}

	public void setLastRank(String lastRank) {
		this.lastRank = lastRank;
	}

	public String getExaminationPoint() {
		return this.examinationPoint;
	}

	public void setExaminationPoint(String examinationPoint) {
		this.examinationPoint = examinationPoint;
	}

	public Boolean getScratch() {
		return this.scratch;
	}

	public void setScratch(Boolean scratch) {
		this.scratch = scratch;
	}

	public Date getExtractTime() {
		return this.extractTime;
	}

	public void setExtractTime(Date extractTime) {
		this.extractTime = extractTime;
	}

	public BigDecimal getTrialT() {
		return this.trialT;
	}

	public void setTrialT(BigDecimal trialT) {
		this.trialT = trialT;
	}

	public BigDecimal getTrialOffset() {
		return this.trialOffset;
	}

	public void setTrialOffset(BigDecimal trialOffset) {
		this.trialOffset = trialOffset;
	}

}
