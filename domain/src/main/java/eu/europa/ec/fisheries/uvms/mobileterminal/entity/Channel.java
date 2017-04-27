/*
 Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

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

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.EqualsUtil;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;


/**
 * The persistent class for the mobileterminal database table.
 * 
 */
@Entity
@Table(name="channel")
public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@Size(max=36)
	@NotNull
	@Column(name="guid")
	private String guid;

	@Column(name="archived")
	private Boolean archived;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updattime")
	private Date updateTime;

	@Column(name="updateuser")
	private String updateUser;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="mobterm_id")
	private MobileTerminal mobileTerminal;


	@OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	@NotNull
	private List<ChannelHistory> histories;

	public Channel() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public MobileTerminal getMobileTerminal() {
		return mobileTerminal;
	}

	public void setMobileTerminal(MobileTerminal mobileTerminal) {
		this.mobileTerminal = mobileTerminal;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	@PrePersist
	private void atPrePersist() {
		setGuid(UUID.randomUUID().toString());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Channel) {
			Channel other = (Channel)obj;
			if(!EqualsUtil.compare(guid, other.guid)) return false;
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return EqualsUtil.getHashCode(guid);
	}

	public List<ChannelHistory> getHistories() {
		if (histories == null) {
			histories = new ArrayList<>();
		}
		return histories;
	}

	public void setHistories(List<ChannelHistory> histories) {
		this.histories = histories;
	}

	public ChannelHistory getCurrentHistory() {
		for (ChannelHistory history : histories) {
			if (history.isActive()) {
				return history;
			}
		}

		return null;
	}
}