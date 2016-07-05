# MobileTerminalModule

####Module description/purpose

The main purpose with Mobile Terminal module is provide possibility to administrate and store information of a mobile terminal.  A mobile terminal is a telematics which can be installed on e.g. a watercraft or a vehicle to keep track of these position, speed, course and status. A mobile terminal sends its position data by transmit the data to an addressed Land Earth Station (LES), via a satellite. Because it can be different types of telematics installed in the vessels, therefore requires different types of plugins to communicate with the LES. In UVMS-project, two plugins are completed implemented. One is support a mobile terminal type of Imarsat C and the other is for type of Iridium. All position data that comes from the plugin goes first via the Exchange module then sends it forward to the Rules module to verify the position data, and if the position data is valid, it will be sent to Movement module.  Otherwise an alarm will be created by the Rule module to notify an error has occurred. See documentation in the DOC folder about the communication and data flow. 

