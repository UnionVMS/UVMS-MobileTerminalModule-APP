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
package eu.europa.ec.fisheries.uvms.mobileterminal.entity2;

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.EventCodeEnum;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * The persistent class for the channel database table.
 * 
 */
@Entity
@Table(name="channel")
@Audited
public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "CHANNEL_UUID")
	@GenericGenerator(name = "CHANNEL_UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private UUID id;

	@Column(unique = true, name = "historyid")
	private UUID historyId;

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

	@ManyToOne
	private MobileTerminal mobileTerminal;

	@Column(name="comchanname")
	private String name;

	@Column(name="active")
	private boolean active;

	@Column(name="attributes")
	private String attributes;

	@Enumerated(EnumType.STRING)
	@Column(name="eventtype")
	private EventCodeEnum eventCodeType;

	// ???????? kanske
	@Fetch(FetchMode.JOIN)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mobterm_event_id")
	private MobileTerminalEvent mobileTerminalEvent;

	@Column(name="chan_def")
	private boolean defaultChannel;

	@Column(name="chan_conf")
	private boolean configChannel;

	@Column(name="chan_poll")
	private boolean pollChannel;

	@PrePersist
	@PreUpdate
	private void generateNewHistoryId() {
		this.historyId = UUID.randomUUID();
	}







}
