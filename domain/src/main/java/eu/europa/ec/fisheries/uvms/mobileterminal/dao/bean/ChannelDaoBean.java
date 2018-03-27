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

package eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.ChannelDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.Dao;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.AttributeMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.poll.PollSearchMapper;

/**
 * Created by osdjup on 2016-11-16.
 */
@Stateless
public class ChannelDaoBean extends Dao implements ChannelDao {
    @Override
    public List<Channel> getPollableListSearch(List<String> idList) {
        String sql = PollSearchMapper.createPollableSearchSql(idList);
        TypedQuery<Channel> query = em.createQuery(sql, Channel.class);
        if(idList != null && !idList.isEmpty()) {
            query.setParameter("idList", idList);
        }
        return query.getResultList();
    }

    @Override
    public List<String> getActiveDNID(String pluginName) {
        String sql = getSQLActiveDNID(pluginName);
        TypedQuery<String> query = em.createQuery(sql, String.class);
        List<Map<String, String>> attributes = AttributeMapper.mapAttributeStrings(query.getResultList());

        List<String> dnidList = new ArrayList<>();
        for (Map<String, String> attribute : attributes) {
            for (String key : attribute.keySet()) {
                if (key.equalsIgnoreCase("DNID")) {
                    dnidList.add(attribute.get(key));
                }
            }
        }
        return dnidList;
    }

    private String getSQLActiveDNID(String pluginName) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT DISTINCT ch_hist.attributes FROM ChannelHistory ch_hist ");
        builder.append("INNER JOIN ch_hist.channel ch "); // channel
        builder.append("INNER JOIN ch.mobileTerminal mobTerm "); //Mobileterminal
        builder.append("INNER JOIN mobTerm.plugin p ");
        builder.append("WHERE ch_hist.active = '1' ");
        builder.append("AND mobTerm.archived = '0' AND p.pluginInactive = '0' ");
        builder.append("AND p.pluginServiceName = '" + pluginName + "'");
        return builder.toString();
    }
}
