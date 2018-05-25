package eu.europa.ec.fisheries.uvms.mobileterminal.service.rest.service;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.rest.AbstractMTRestTest;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.rest.error.ResponseCode;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;

import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class PollResourceTest extends AbstractMTRestTest {

    @Test
    @RunAsClient
    public void getRunningProgramPollsTest() {

        String response = getWebTarget()
                .path("/poll/running")
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();

        assertThat(jsonObject.getInt("code"), CoreMatchers.is(ResponseCode.OK.getCode()));
    }
}
