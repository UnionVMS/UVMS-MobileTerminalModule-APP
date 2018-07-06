package eu.europa.ec.fisheries.uvms.mobileterminal.client;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class ExchangeRESTClient {

    // TODO read from config?
    private static final String REST_END_POINT = "http://localhost:8080/unionvms/exchange/unsecured/rest/api";

    private Client client;

    public ExchangeRESTClient() {

        ClientBuilder builder = ClientBuilder.newBuilder();
        client = builder.newClient();
    }

    public GetServiceListResponse getServiceList(GetServiceListRequest request) {
        GetServiceListResponse getServiceListResponse = client.target(REST_END_POINT)
                .path("serviceList")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), GetServiceListResponse.class);

        return getServiceListResponse;
    }


}
