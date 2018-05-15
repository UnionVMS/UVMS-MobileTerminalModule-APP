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
package eu.europa.ec.fisheries.uvms.mobileterminal.rest.service;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.AbstractMTRestTest;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.error.ResponseCode;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class MobileTerminalResourceTest extends AbstractMTRestTest {

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalResourceTest.class);

    @Test
    @RunAsClient
    public void createMobileTerminalTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String response = getWebTarget()
                                .path("mobileterminal")
                                .request(MediaType.APPLICATION_JSON)
                                .post(Entity.json(mobileTerminal), String.class);
        
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();
        
        assertThat(jsonObject.getInt("code"), CoreMatchers.is(ResponseCode.OK.getCode()));
    }

    @Test
    @RunAsClient
    public void getMobileTerminalByIdTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), ResponseCode.OK.getCode());

        JsonObject data = jsonObject.getJsonObject("data");
        JsonObject terminalId = data.getJsonObject("mobileTerminalId");
        String guid = terminalId.getString("guid");

        String res = getWebTarget()
                .path("mobileterminal/" + guid)
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(String.class);

        assertTrue(res.contains(guid));
    }

    @Test
    @RunAsClient
    public void updateMobileTerminalTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), ResponseCode.OK.getCode());
        assertFalse(created.contains("IRIDIUM"));

        JsonObject data = jsonObject.getJsonObject("data");
        JsonNumber id = data.getJsonNumber("id");
        JsonObject terminalId = data.getJsonObject("mobileTerminalId");
        String guid = terminalId.getString("guid");

        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(guid);
        mobileTerminal.setId(id.intValue());
        mobileTerminal.setMobileTerminalId(mobileTerminalId);
        mobileTerminal.setType("IRIDIUM");

        String updated = getWebTarget()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(mobileTerminal), String.class);

        assertTrue(updated.contains("IRIDIUM"));
        assertTrue(updated.contains(guid));
        assertTrue(updated.contains(String.valueOf(id.intValue())));
    }
}
