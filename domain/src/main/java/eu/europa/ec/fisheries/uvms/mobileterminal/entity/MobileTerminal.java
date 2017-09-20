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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.EqualsUtil;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;


/**
 * The persistent class for the mobileterminal database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_GUID, query="SELECT m FROM MobileTerminal m WHERE m.guid = :guid"),
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_SERIAL_NO, query="SELECT m FROM MobileTerminal m WHERE m.serialNo = :serialNo")
})
public class MobileTerminal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@Size(max=36)
	@NotNull
	@Column(name="guid")
	private String guid;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="plugin_id")
	private MobileTerminalPlugin plugin;
	
	@Column(name="archived")
	private Boolean archived = false;

	@Column(name="inactivated")
	private Boolean inactivated = false;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="source")
	private MobileTerminalSourceEnum source;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="type")
	private MobileTerminalTypeEnum mobileTerminalType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updatetime")
	private Date updatetime;

	@Column(name="updateuser")
	private String updateuser;

	@Column(name="serial_no")
	private String serialNo;

	//bi-directional many-to-one association to Mobileterminalevent
	@OneToMany(mappedBy="mobileterminal", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private Set<MobileTerminalEvent> mobileTerminalEvents;

	//bi-directional many-to-one association to Mobileterminalevent
	@OneToMany(mappedBy="mobileTerminal", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private Set<Channel> channels;

	public MobileTerminal() {
	}

	// TODO  is this really OK ??????
	// TODO if GUID set at creation time it will be ovrwritten !!!!!!!!!!!!!!!!!!!!!!!!!!!!
	@PrePersist
	private void atPrePersist() {
		setGuid(UUID.randomUUID().toString());
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Boolean getArchived() {
		return this.archived;
	}

	public void setArchived(final Boolean archived) {
		this.archived = archived;
	}

	public Boolean getInactivated() {
		return this.inactivated;
	}

	public void setInactivated(final Boolean inactivated) {
		this.inactivated = inactivated;
	}

	public MobileTerminalSourceEnum getSource() {
		return this.source;
	}

	public void setSource(final MobileTerminalSourceEnum source) {
		this.source = source;
	}

	public MobileTerminalTypeEnum getMobileTerminalType() {
		return this.mobileTerminalType;
	}

	public void setMobileTerminalType(final MobileTerminalTypeEnum mobileTerminalType) {
		this.mobileTerminalType = mobileTerminalType;
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

	public Set<MobileTerminalEvent> getMobileTerminalEvents() {
		if (mobileTerminalEvents == null) {
			mobileTerminalEvents = new HashSet<>();
		}
		return this.mobileTerminalEvents;
	}

	public void setMobileTerminalEvents(final Set<MobileTerminalEvent> mobileTerminalEvents) {
		this.mobileTerminalEvents = mobileTerminalEvents;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public Set<Channel> getChannels() {
		if (channels == null) {
			channels = new HashSet<>();
		}
		return channels;
	}

	public void setChannels(final Set<Channel> channels) {
		this.channels = channels;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(final String serialNo) {
		this.serialNo = serialNo;
	}

	@Override
	public boolean equals(final Object obj) {
		if(obj instanceof MobileTerminal) {
			final MobileTerminal other = (MobileTerminal)obj;
			if(!EqualsUtil.compare(guid, other.guid)) return false;
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return EqualsUtil.getHashCode(guid);
	}

	public MobileTerminalPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(final MobileTerminalPlugin plugin) {
		this.plugin = plugin;
	}

	public MobileTerminalEvent getCurrentEvent() {
		for (final MobileTerminalEvent event : getMobileTerminalEvents()) {
			if (event.isActive()) {
				return event;
			}
		}
		return null;
	}
}