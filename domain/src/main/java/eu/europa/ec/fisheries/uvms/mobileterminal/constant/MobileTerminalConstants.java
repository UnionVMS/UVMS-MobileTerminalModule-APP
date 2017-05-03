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
package eu.europa.ec.fisheries.uvms.mobileterminal.constant;

public class MobileTerminalConstants {
	public static final String POLL_FIND_BY_ID = "Poll.findById"; //ToDo: This query string is not implemented anywhere. Either implement it or remove it.
	public static final String POLL_PROGRAM_FIND_BY_ID = "PollProgram.findById";
    public static final String POLL_PROGRAM_FIND_ALIVE = "PollProgram.findAlive";
    public static final String POLL_PROGRAM_FIND_RUNNING_AND_STARTED = "PollProgram.findRunningAndStarted";

	public static final String MOBILE_TERMINAL_FIND_BY_GUID = "Mobileterminal.findByGuid";
	public static final String MOBILE_TERMINAL_FIND_BY_SERIAL_NO = "Mobileterminal.findBySerialNo";

	public static final String PLUGIN_FIND_ALL = "Plugin.findAll";
	public static final String PLUGIN_FIND_BY_SERVICE_NAME = "Plugin.findByServiceName";
	
	public static final String DNID_LIST = "DNIDList.findAll";
	public static final String DNID_LIST_BY_PLUGIN = "DNIDList.findByPlugin";
	
	public static final String OCEAN_REGIONS = "OceanRegion.findAll";
	
    public static final String CREATE_COMMENT = "Automatic create comment";

	public static final String SERIAL_NUMBER = "SERIAL_NUMBER";

    public static String UPDATE_USER = "UVMS";

    public static String TRUE = "TRUE";
    public static String FALSE = "FALSE";

	public static String CAPABILITY_CONFIGURABLE = "CONFIGURABLE";
	public static String CAPABILITY_DEFAULT_REPORTING = "DEFAULT_REPORTING";
	public static String CAPABILITY_POLLABLE = "POLLABLE";
}