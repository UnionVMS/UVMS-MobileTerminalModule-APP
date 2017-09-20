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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;

/**
 * The persistent class for the oceanregion database table.
 *
 */
@Entity
@Table(name = "dnid_list")
@NamedQueries({
	@NamedQuery(name = MobileTerminalConstants.DNID_LIST, query = "SELECT dnid FROM DNIDList dnid"),
	@NamedQuery(name = MobileTerminalConstants.DNID_LIST_BY_PLUGIN, query = "SELECT dnid FROM DNIDList dnid where dnid.pluginName = :pluginName")
})
public class DNIDList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Size(max = 100)
    @Column(name = "value")
    private String dnid;

    @Size(max = 500)
    @Column(name = "plugin_service_name")
    private String pluginName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updattim")
    private Date updateTime;

    @Column(name = "upuser")
    private String updatedBy;

    public DNIDList() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public void setPluginName(final String pluginName) {
        this.pluginName = pluginName;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdateUser(final String user) {
        this.updatedBy = user;
    }

    public String getDNID() {
        return dnid;
    }

    public void setDNID(final String dnid) {
        this.dnid = dnid;
    }

}