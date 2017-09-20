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
package eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalUnmarshallException;

/**
 **/
public class JAXBMarshaller {

    private static Logger LOG = LoggerFactory.getLogger(JAXBMarshaller.class);

    private static Map<String, JAXBContext> contexts = new HashMap<>();

    /**
     * Marshalls a JAXB Object to a XML String representation
     *
     * @param <T>
     * @param data
     * @return
     * @throws MobileTerminalModelMapperException
     */
    public static <T> String marshallJaxBObjectToString(final T data) throws MobileTerminalModelMapperException {
        try {
            JAXBContext jaxbContext = contexts.get(data.getClass().getName());
            if (jaxbContext == null) {
                final long before = System.currentTimeMillis();
                jaxbContext = JAXBContext.newInstance(data.getClass());
                contexts.put(data.getClass().getName(), jaxbContext);
                LOG.debug("Stored contexts: {}", contexts.size());
                LOG.debug("JAXBContext creation time: {}", (System.currentTimeMillis() - before));
            }
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            final StringWriter sw = new StringWriter();
            marshaller.marshal(data, sw);
            final long before = System.currentTimeMillis();
            final String marshalled = sw.toString();
            LOG.debug("StringWriter time: {}", (System.currentTimeMillis() - before));
            return marshalled;
        } catch (final JAXBException e) {
            LOG.error("[ Error when marshalling data. ] {}", e.getMessage());
            throw new MobileTerminalModelMapperException("Error when marshalling " + data.getClass().getName() + " to String");
        }
    }

    /**
     * Unmarshalls A textMessage to the desired Object. The object must be the
     * root object of the unmarchalled message!
     *
     * @param <R>
     * @param textMessage
     * @param clazz pperException
     * @return
     * @throws
     * eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalUnmarshallException
     */
    public static <R> R unmarshallTextMessage(final TextMessage textMessage, final Class clazz) throws MobileTerminalUnmarshallException {
        try {
            JAXBContext jc = contexts.get(clazz.getName());
            if (jc == null) {
                final long before = System.currentTimeMillis();
                jc = JAXBContext.newInstance(clazz);
                contexts.put(clazz.getName(), jc);
                LOG.debug("Stored contexts: {}", contexts.size());
                LOG.debug("JAXBContext creation time: {}", (System.currentTimeMillis() - before));
            }
            final Unmarshaller unmarshaller = jc.createUnmarshaller();
            final StringReader sr = new StringReader(textMessage.getText());
            final StreamSource source = new StreamSource(sr);
            final long before = System.currentTimeMillis();
            final R object = (R) unmarshaller.unmarshal(source);
            LOG.debug("Unmarshalling time: {}", (System.currentTimeMillis() - before));
            return object;
        } catch (JMSException | JAXBException e) {
            throw new MobileTerminalUnmarshallException("Error when unmarshalling response in ResponseMapper: " + e.getMessage());
        }
    }

}