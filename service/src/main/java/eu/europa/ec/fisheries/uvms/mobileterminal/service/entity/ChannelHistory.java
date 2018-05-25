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

package eu.europa.ec.fisheries.uvms.mobileterminal.service.entity;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.AttributeMapper;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * Created by osdjup on 2016-11-16.
 */
@Entity
@Table(name="channel_history")
public class ChannelHistory {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="comchanname")
    private String name;

    @Column(name="active")
    private boolean active;

    @Column(name="attributes")
    private String attributes;

    @Column(name="updattime")
    private Date updateTime;

    @Column(name="updateuser")
    private String updatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name="eventtype")
    private EventCodeEnum eventCodeType;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobterm_event_id")
    private MobileTerminalEvent mobileTerminalEvent;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name="chan_def")
    private boolean defaultChannel;

    @Column(name="chan_conf")
    private boolean configChannel;

    @Column(name="chan_poll")
    private boolean pollChannel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public EventCodeEnum getEventCodeType() {
        return eventCodeType;
    }

    public void setEventCodeType(EventCodeEnum eventCodeType) {
        this.eventCodeType = eventCodeType;
    }

    public void setMobileTerminalEvent(MobileTerminalEvent mobileTerminalEvent) {
        this.mobileTerminalEvent = mobileTerminalEvent;
    }

    public MobileTerminalEvent getMobileTerminalEvent() {
        return mobileTerminalEvent;
    }

    public boolean isConfigChannel() {
        return configChannel;
    }

    public boolean isDefaultChannel() {
        return defaultChannel;
    }

    public boolean isPollChannel() {
        return pollChannel;
    }

    public void setConfigChannel(boolean configChannel) {
        this.configChannel = configChannel;
    }

    public void setDefaultChannel(boolean defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public void setPollChannel(boolean pollChannel) {
        this.pollChannel = pollChannel;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChannelHistory) {
            ChannelHistory other = (ChannelHistory) obj;
            if (!name.equalsIgnoreCase(other.getName())) {
                return false;
            }
            Map<String, String> attr = AttributeMapper.mapAttributeString(attributes);
            Map<String, String> otherAttr = AttributeMapper.mapAttributeString(other.getAttributes());

            if((attr == null) && (otherAttr == null)) return true;
            if(attr == null || otherAttr == null) return false;

            if(attr.size() != otherAttr.size()) return false;

            for (String key : attr.keySet()) {
                if (!otherAttr.containsKey(key)) {
                    return false;
                } else if (attr.get(key) != null && !attr.get(key).equalsIgnoreCase(otherAttr.get(key))) {
                    return false;
                }
            }

            if (configChannel != other.isConfigChannel()) {
                return false;
            }
            if (defaultChannel != other.isDefaultChannel()) {
                return false;
            }
            if (pollChannel != other.isPollChannel()) {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
