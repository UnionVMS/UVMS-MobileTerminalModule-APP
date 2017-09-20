/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.mobileterminal.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.EqualsUtil;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.EventCodeEnum;


/**
 * The persistent class for the mobileterminalevent database table.
 * 
 */
@Entity
@NamedQuery(name="MobileTerminalEvent.findAll", query="SELECT m FROM MobileTerminalEvent m")
public class MobileTerminalEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@Size(max=400)
	@Column(name="comment")
	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updattime")
	private Date updatetime;

	@Size(max=60)
	@Column(name="upuser")
	private String updateuser;

	@Size(max=1000)
	@Column(name="attributes")
	private String attributes;

	@Size(max=400)
	@Column(name="connect_id")
	private String connectId;

	@Enumerated(EnumType.STRING)
	@Column(name="eventtype")
	private EventCodeEnum eventCodeType;

	@Column(name="active")
	private boolean active;

	//bi-directional many-to-one association to Mobileterminal
	@NotNull
	@ManyToOne
	@JoinColumn(name="mobileterminal_id")
	@Fetch(FetchMode.SELECT)
	private MobileTerminal mobileterminal;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="chan_def")
	private Channel defaultChannel;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="chan_conf")
	private Channel configChannel;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="chan_poll")
	private Channel pollChannel;

	public MobileTerminalEvent() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long mobtermeventId) {
		this.id = mobtermeventId;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public Date getUpdateTime() {
		return this.updatetime;
	}

	public void setUpdateTime(final Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdatedBy() {
		return this.updateuser;
	}

	public void setUpdatedBy(final String updateuser) {
		this.updateuser = updateuser;
	}

	public EventCodeEnum getEventCodeType() {
		return this.eventCodeType;
	}

	public void setEventCodeType(final EventCodeEnum eventCodeType) {
		this.eventCodeType = eventCodeType;
	}

	public MobileTerminal getMobileTerminal() {
		return this.mobileterminal;
	}

	public void setMobileTerminal(final MobileTerminal mobileterminal) {
		this.mobileterminal = mobileterminal;
	}

	public Channel getDefaultChannel() {
		return defaultChannel;
	}

	public void setDefaultChannel(final Channel defaultChannel) {
		this.defaultChannel = defaultChannel;
	}

	public Channel getConfigChannel() {
		return configChannel;
	}

	public void setConfigChannel(final Channel configChannel) {
		this.configChannel = configChannel;
	}

	public Channel getPollChannel() {
		return pollChannel;
	}

	public void setPollChannel(final Channel pollChannel) {
		this.pollChannel = pollChannel;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(final String attributes) {
		this.attributes = attributes;
	}

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(final String connectId) {
		this.connectId = connectId;
	}

	@Override
	public boolean equals(final Object obj) {
		if(obj instanceof MobileTerminalEvent) {
			final MobileTerminalEvent other = (MobileTerminalEvent)obj;
			if(!EqualsUtil.compare(comment, other.comment)) return false;
			if(!EqualsUtil.compare(eventCodeType.name(), other.eventCodeType.name())) return false;
			if(!EqualsUtil.compare(updatetime, other.updatetime)) return false;
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return EqualsUtil.getHashCode(comment) + EqualsUtil.getHashCode(eventCodeType) + EqualsUtil.getHashCode(updatetime);
	}
}