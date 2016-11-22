# MobileTerminalModule

##Module description/purpose

The main purpose with Mobile Terminal module is provide possibility to administrate and store information of a mobile terminal. 

A mobile terminal is a telematics which can be installed on e.g. a watercraft or a vehicle to keep track of these position, speed, course and status. A mobile terminal sends its position data by transmit the data to an addressed Land Earth Station (LES), via a satellite. Because it can be different types of telematics installed in the vessels, therefore requires different types of plugins to communicate with the LES.

In UVMS-project, two plugins are completed implemented. One is support a mobile terminal type of Imarsat C and the other is for type of Iridium. All position data that comes from the plugin goes first via the Exchange module then sends it forward to the Rules module to verify the position data, and if the position data is valid, it will be sent to Movement module.  Otherwise an alarm will be created by the Rule module to notify an error has occurred. See figure 1 below for information about the communication and data flow. 

![Alt text](/DOC/arch.png.jpg?raw=true "Architecture")

A mobile terminal of type INMARSAT C must have at least one channel configured to be able to communicate with LES. And the channel must have a unique combination of DNID (Data Network ID) and Member number, which defines exactly where the information should be sent to or retrieve from LES. 

From Mobile Terminal module you can send three different types of poll; manual poll, configuration poll and sampling poll to the mobile terminal.
* Manual poll is a command sends to the mobile terminal to request its latest position. 
* Configuration poll is a command send to mobile terminal to update the configuration of a mobile terminal. 
* Sampling poll is a command sends to the mobile terminal to request it to send a set of positions which have been saved in the mobile terminal. 

## Module dependencies

|Name    |Description                                                                       |Repository                                     |
|--------|----------------------------------------------------------------------------------|-----------------------------------------------|
|Audit   |Log all operations which have been executed in all UVMS-modules                   |https://github.com/UnionVMS/UVMS-AuditModule   |
|User    |Authentication operations and access management                                   |https://github.com/UnionVMS/UVMS-User          |
|Exchange|Gateway provides communication ability with other modules or artifacts beside UVMS|https://github.com/UnionVMS/UVMS-ExchangeModule|

## JMS Queue Dependencies
The jndi name example is taken from wildfly8.2 application server

|Name                   |JNDI name example                      |Description                                   |
|-----------------------|---------------------------------------|----------------------------------------------|
|UVMSMobileTerminalEvent|java:/jms/queue/UVMSMobileTerminalEvent|Request queue to MobileTerminal service module|
|UVMSMobileTerminal     |java:/jms/queue/UVMSMobileTerminal     |Response queue to MobileTerminal module       |
|UVMSAuditEvent         |java:/jms/queue/UVMSAuditEvent         |Request queue to Audit service module         |
|UVMSUserEvent          |java:/jms/queue/UVMSUserEvent          |Request queue to User service module          |
|UVMSExchangeEvent      |java:/jms/queue/UVMSExchangeEvent      |Request queue to Exchange service module      |

## Datasources
The jndi name example is taken from wildfly8.2 application server

|Name                   |JNDI name example                  |
|-----------------------|-----------------------------------|
|uvms_mobterm           |java:jboss/datasources/uvms_mobterm|

## Related Repositories

* https://github.com/UnionVMS/UVMS-MobileterminalModule-DB
* https://github.com/UnionVMS/UVMS-MobileterminalModule-MODEL
* https://github.com/UnionVMS/UVMS-MobileTerminalModule-PROXY-NATIONAL