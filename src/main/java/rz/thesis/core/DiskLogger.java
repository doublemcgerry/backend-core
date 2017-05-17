/*
 * This Class saves the log to a file
 * types:
 * 1)syncread
 * 2)syncwrite
 * 3)queueread
 * 4)queuewrite
 * 5)confwrite groupread
 * 6)confwrite groupresponse
 * 7)confwrite groupwrite
 * 8)info groupread
 * 9)info groupresponse
 * 10)info groupwrite
 * 11)timeout
 * 21)syncread done
 * 22)syncwrite done
 * 23)queueread done
 * 24)queuewrite done
 * 98)error
 * 99)message
 * 100)version
 */

package rz.thesis.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.joda.time.DateTime;

import rz.thesis.core.Core.BusOperationType;

/**
 *
 * @author admiral
 */
public class DiskLogger {

    Core core;
    PrintWriter writer;
    private static final long TICKS_AT_EPOCH = 621355968000000000L;
    private static final long TICKS_PER_MILLISECOND = 10000;
    private static final short GROUP_READ = 0x00;
	private static final short GROUP_RESPONSE = 0x40;
	private static final short GROUP_WRITE = 0x80;
	public static enum messageType{
		INFORMATION,CONFIRMATION
	}
    public DiskLogger(Core core,File logfile) {
        this.core=core;
        try {
            writer = new PrintWriter(new FileOutputStream(logfile, true));
//            DateTime dt= new DateTime();
//            System.out.println(dt);
//            long ticks =dt.getMillis()*TICKS_PER_MILLISECOND+TICKS_AT_EPOCH;
//            DateTime date = new DateTime((ticks - TICKS_AT_EPOCH) / TICKS_PER_MILLISECOND);
//            System.out.println(date);
            addVersionMessage("1");
        } catch (FileNotFoundException ex) {
            core.getLogger().error(ex.getMessage());
        }

    }

    public void closeLogger() {
        writer.close();
    }
    private void addVersionMessage(String version){
        addBusLogMessage(Core.BusOperationType.version, "", "", version, "");
    }
    public void addLogMessage(String logMessage) {
        addBusLogMessage(Core.BusOperationType.message, "", "", "", logMessage);
    }
    public void addErrorMessage(String logMessage) {
        addBusLogMessage(Core.BusOperationType.error, "", "", "", logMessage);
    }

    public void addBusLogMessage(Core.BusOperationType type, String groupAddress, String actuatorAddress, String value, String message) {
        try {
            
            writer.print(DateTime.now().getMillis()*TICKS_PER_MILLISECOND+TICKS_AT_EPOCH);
            writer.print("|");
            writer.print(type);
            writer.print("|");
            writer.print(groupAddress);
            writer.print("|");
            writer.print(actuatorAddress);
            writer.print("|");
            writer.print(value);
            writer.print("|");
            writer.print(message);
            writer.println();
            writer.flush();
        } catch (Exception ex) {
            core.getLogger().error(ex.getMessage());
        }
        
    }

    public void addBusLogMessage(Core.BusOperationType type, String groupAddress, String actuatorAddress, String value) {
        addBusLogMessage(type, groupAddress, actuatorAddress, value, "");
    }
    public void addBusLogMessage(messageType msgType, int serviceValue, String groupAddress, String actuatorAddress, String value) {
        Core.BusOperationType type=BusOperationType.error;
        if(serviceValue==GROUP_READ)
        	type=msgType==messageType.INFORMATION?BusOperationType.info_groupread:BusOperationType.confwrite_groupread;
        if(serviceValue==GROUP_WRITE)
        	type=msgType==messageType.INFORMATION?BusOperationType.info_groupwrite:BusOperationType.confwrite_groupwrite;
        if(serviceValue==GROUP_RESPONSE)
        	type=msgType==messageType.INFORMATION?BusOperationType.info_groupresponse:BusOperationType.confwrite_groupresponse;
    	addBusLogMessage(type, groupAddress, actuatorAddress, value, "");
    }

    public void addBusLogMessage(Core.BusOperationType type, String groupAddress, String value) {
        addBusLogMessage(type, groupAddress, "", value, "");
    }

    public void addBusLogMessage(Core.BusOperationType type, String groupAddress) {
        addBusLogMessage(type, groupAddress, "", "", "");
    }

    public void addBusLogMessage(Core.BusOperationType type) {
        addBusLogMessage(type, "", "", "", "");
    }

}
