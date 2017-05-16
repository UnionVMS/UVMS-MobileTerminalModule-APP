package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;


import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.uvms.config.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.config.model.mapper.ModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.consumer.MessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;
import java.util.List;

//import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollToCommandRequestMapper;

@Stateless
public class PluginServiceMOCKBean implements PluginService {

    final static Logger LOG = LoggerFactory.getLogger(PluginServiceMOCKBean.class);

    public static final String EXCHANGE_MODULE_NAME = "exchange";
    public static final String DELIMETER = ".";
    public static final String INTERNAL_DELIMETER = ",";
    public static final String SETTING_KEY_DNID_LIST = "DNIDS";

    @EJB
    MessageProducer messageProducer;

    @EJB
    MessageConsumer reciever;

    //    @EJB(lookup = ServiceConstants.DB_ACCESS_CONFIG_MODEL)
    @EJB
    ConfigModelBean configModel;

    @Override
    public AcknowledgeTypeType sendPoll(PollResponseType poll, String username) throws MobileTerminalServiceException {
        return AcknowledgeTypeType.OK;
    }

    @Override
    public void processUpdatedDNIDList(String pluginName) {
        try {
            List<String> dnidList = configModel.updatedDNIDList(pluginName);

            String settingKey = pluginName + DELIMETER + SETTING_KEY_DNID_LIST;
            StringBuffer buffer = new StringBuffer();
            for (String dnid : dnidList) {
                buffer.append(dnid + INTERNAL_DELIMETER);
            }
            String settingValue = buffer.toString();

            try {
                sendUpdatedDNIDListToConfig(pluginName, settingKey, settingValue);
            } catch (ModelMarshallException | MobileTerminalMessageException e) {
                LOG.debug("Couldn't send to config module. Sending to exchange module.");
                sendUpdatedDNIDListToExchange(pluginName, SETTING_KEY_DNID_LIST, settingValue);
            }
        } catch (MobileTerminalModelException ex) {
            LOG.error("Couldn't get updated DNID List");
        }
    }

    private void sendUpdatedDNIDListToConfig(String pluginName, String settingKey, String settingValue) throws ModelMarshallException, MobileTerminalMessageException {
        SettingType setting = new SettingType();
        setting.setKey(settingKey);
        setting.setModule(EXCHANGE_MODULE_NAME);
        setting.setDescription("DNID list for all active mobile terminals. Plugin use it to know which channels it should be listening to");
        setting.setGlobal(false);
        setting.setValue(settingValue);

        String setSettingRequest = ModuleRequestMapper.toSetSettingRequest(EXCHANGE_MODULE_NAME, setting, "UVMS");
        String messageId = messageProducer.sendModuleMessage(setSettingRequest, ModuleQueue.CONFIG);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        LOG.info("UpdatedDNIDList sent to config module");
    }

    private void sendUpdatedDNIDListToExchange(String pluginName, String settingKey, String settingValue) {
        try {
            String request = ExchangeModuleRequestMapper.createUpdatePluginSettingRequest(pluginName, settingKey, settingValue);
            String messageId = messageProducer.sendModuleMessage(request, ModuleQueue.EXCHANGE);
            TextMessage response = reciever.getMessage(messageId, TextMessage.class);
            LOG.info("UpdatedDNIDList sent to exchange module");
        } catch (ExchangeModelMarshallException | MobileTerminalMessageException e) {
            LOG.error("Failed to send updated DNID list");
        }
    }

}