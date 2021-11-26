package cn.hyperchain.business;

import cn.hyperchain.types.MessageQueue;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.util.Enumeration;

/**
 * @author madj
 */
@Slf4j
public class SerialTool implements SerialPortEventListener {

    SerialPort serialPort;

    private static final String[] PORT_NAMES = {
            "/dev/tty.usbserial-A9007UX1",
            "/dev/ttyUSB0",
            "COM4",
    };

    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 115200;
    private InputStream input = null;
    private final byte[] bytes = new byte[256];
    private final MessageQueue messageQueue = new MessageQueue(2048);

    public SerialTool(String portName) {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            if (currPortId.getName().equals(portName)) {
                portId = currPortId;
                break;
            }
        }
        if (portId == null) {
            log.info("Could not find COM port.");
            return;
        }
        String name = portId.getName();
        String portType = getPortTypeName(portId.getPortType());
        log.info("open "+ portType + " " + name);
        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            // open the streams
            input = serialPort.getInputStream();
            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            log.error("Exception=" + e.getMessage());
        }
    }

    public static String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "cn.hyperchain.business.SerialTool";
            default:
                return "unknown type";
        }
    }

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }


    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        try {
            int bufflenth = input.available();
            while (bufflenth > 0) {
                int recvLen = input.read(bytes);
                messageQueue.puts(bytes, recvLen);
                bufflenth = input.available();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte getNextByte() {
        return messageQueue.take();
    }

    public int getSize() {
        return messageQueue.getSize();
    }

    public byte at(int index) {
        return messageQueue.at(index);
    }


}
