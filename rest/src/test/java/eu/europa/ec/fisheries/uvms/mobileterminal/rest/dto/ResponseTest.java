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
package eu.europa.ec.fisheries.uvms.mobileterminal.rest.dto;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mock.MockData;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.error.ResponseCode;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.service.MobileTerminalResource;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;

import javax.servlet.http.HttpServletRequest;

public class ResponseTest {

    @Mock
    MobileTerminalService mobileTerminalService;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    MobileTerminalResource mobileTerminalResource;

    MobileTerminalResource SERVICE_NULL = new MobileTerminalResource();
    private static final Integer LIST_SIZE = 3;

    private static final Integer MOBILE_TERMINAL_ID_INT = 1;
    private static final String MOBILE_TERMINAL_ID = "NKJSDGHKJy9239";

    private final ResponseCode ERROR_RESULT;

    private final ResponseDto SUCCESS_RESULT_CREATE;
    private final ResponseDto SUCCESS_RESULT_LIST_RESPONSE;
    private final ResponseDto SUCCESS_RESULT_UPDATE;
    private final ResponseDto SUCCESS_RESULT_GET_BY_ID;

    private final MobileTerminalType MOBILE_TERMINAL_DTO = MockData.createMobileTerminalDto(MOBILE_TERMINAL_ID_INT);
    private final MobileTerminalListResponse MOBILE_TERMINAL_LIST_RESPONSE = MockData.createMobileTerminalListResponse();

    public ResponseTest() {
        ERROR_RESULT = ResponseCode.UNDEFINED_ERROR;
        SUCCESS_RESULT_UPDATE = new ResponseDto(MOBILE_TERMINAL_DTO, ResponseCode.OK);
        SUCCESS_RESULT_LIST_RESPONSE = new ResponseDto(MOBILE_TERMINAL_LIST_RESPONSE, ResponseCode.OK);
        SUCCESS_RESULT_CREATE = new ResponseDto(MOBILE_TERMINAL_DTO, ResponseCode.OK);
        SUCCESS_RESULT_GET_BY_ID = new ResponseDto(MOBILE_TERMINAL_DTO, ResponseCode.OK);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetMobileTerminalById() throws MobileTerminalException {
        doReturn(MOBILE_TERMINAL_DTO).when(mobileTerminalService).getMobileTerminalById(MOBILE_TERMINAL_ID);
        final ResponseDto result = mobileTerminalResource.getMobileTerminalById(MOBILE_TERMINAL_ID);
        Mockito.verify(mobileTerminalService).getMobileTerminalById(MOBILE_TERMINAL_ID);
        assertEquals(SUCCESS_RESULT_GET_BY_ID.toString(), result.toString());
    }

    @Test
    public void testGetMobileTerminalList() throws MobileTerminalException {
        doReturn(MOBILE_TERMINAL_LIST_RESPONSE).when(mobileTerminalService).getMobileTerminalList(null);
        final ResponseDto result = mobileTerminalResource.getMobileTerminalList(null);
        Mockito.verify(mobileTerminalService).getMobileTerminalList(null);
        assertEquals(SUCCESS_RESULT_LIST_RESPONSE.toString(), result.toString());
    }

    @Test
    public void testUpdateMobileTeriminal() throws MobileTerminalException {
        doReturn(MOBILE_TERMINAL_DTO).when(mobileTerminalService).updateMobileTerminal(MOBILE_TERMINAL_DTO, "", MobileTerminalSource.INTERNAL, "TEST");
        doReturn("TEST").when(request).getRemoteUser();
        final ResponseDto result = mobileTerminalResource.updateMobileTerminal("", MOBILE_TERMINAL_DTO);
        Mockito.verify(mobileTerminalService).updateMobileTerminal(MOBILE_TERMINAL_DTO, "", MobileTerminalSource.INTERNAL, "TEST");
        assertEquals(SUCCESS_RESULT_UPDATE.toString(), result.toString());
    }

    @Test
    public void testCreateMobileTeriminal() throws MobileTerminalException {
        doReturn(MOBILE_TERMINAL_DTO).when(mobileTerminalService).createMobileTerminal(MOBILE_TERMINAL_DTO, MobileTerminalSource.INTERNAL, "TEST");
        doReturn("TEST").when(request).getRemoteUser();
        final ResponseDto result = mobileTerminalResource.createMobileTerminal(MOBILE_TERMINAL_DTO);
        Mockito.verify(mobileTerminalService).createMobileTerminal(MOBILE_TERMINAL_DTO, MobileTerminalSource.INTERNAL, "TEST");
        assertEquals(SUCCESS_RESULT_UPDATE.toString(), result.toString());
    }

    @Test
    public void checkDtoReturnsValid() {

        final String VALUE = "HELLO_DTO";
        ResponseDto dto = new ResponseDto(VALUE, ResponseCode.OK);
        Assert.assertEquals(dto.getCode().intValue(), ResponseCode.OK.getCode());
        Assert.assertEquals(dto.getData(), VALUE);

        dto = new ResponseDto(null, ResponseCode.UNDEFINED_ERROR);
        Assert.assertEquals(dto.getCode().intValue(), ResponseCode.UNDEFINED_ERROR.getCode());
        Assert.assertEquals(dto.getData(), null);

    }
}