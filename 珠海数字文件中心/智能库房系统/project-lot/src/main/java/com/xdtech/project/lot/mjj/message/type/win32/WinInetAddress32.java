package com.xdtech.project.lot.mjj.message.type.win32;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WinInetAddress32 extends WinType {

    public WinInetAddress32() {

        try {
            InetAddress address = InetAddress.getLocalHost();
            load(address);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }

    public WinInetAddress32(InetAddress value) {
        load(value);
    }

    public WinInetAddress32(String value) {
        try {
            load(InetAddress.getByName(value));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public WinInetAddress32(byte[] value) {
        load(value);
    }

    public void load(InetAddress value) {

        byte[] data = value.getAddress();
        byte[] b = new byte[data.length];

        for (int i = b.length - 1; i >= 0; i--) {
            b[b.length - i - 1] = data[i];
        }

        load(b);
    }

    public InetAddress toAddress() {
        try {
            byte[] data = toBytes();
            byte[] b = new byte[data.length];

            for (int i = b.length - 1; i >= 0; i--) {
                b[b.length - i - 1] = data[i];
            }

            return InetAddress.getByAddress(b);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int sizeof() {
        return 4;
    }

}
